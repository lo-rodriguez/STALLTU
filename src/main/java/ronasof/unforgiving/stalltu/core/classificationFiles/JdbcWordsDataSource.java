/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.classificationFiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import net.sf.classifier4J.ICategorisedClassifier;
import net.sf.classifier4J.bayesian.ICategorisedWordsDataSource;
import net.sf.classifier4J.bayesian.WordProbability;
import net.sf.classifier4J.bayesian.WordsDataSourceException;


import org.springframework.jdbc.core.JdbcTemplate;


/**
 * A Jdbc based implementation of ICategorisedWordsDataSource that can be
 * independently trained using files.
 */
public class JdbcWordsDataSource implements ICategorisedWordsDataSource {

  private final JdbcTemplate jdbcTemplate;
  private final Map<String,Integer> wordCountMap = new HashMap<String,Integer>();
//  private Transformer quotingLowercasingTransformer = new Transformer() {
//    public Object transform(Object input) {
//      return "'" + StringUtils.lowerCase((String) input) + "'";
//    }
//  };
  
  public JdbcWordsDataSource(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }
  
  @Override
  public void addMatch(String word) throws WordsDataSourceException {
    addMatch(ICategorisedClassifier.DEFAULT_CATEGORY, word);
  }

  @Override
  public void addMatch(String category, String word) throws WordsDataSourceException {
    addWord(word);
  }

  @Override
  public void addNonMatch(String word) throws WordsDataSourceException {
    addNonMatch(ICategorisedClassifier.DEFAULT_CATEGORY, word);
  }

  @Override
  public void addNonMatch(String category, String word) throws WordsDataSourceException {
    addWord(word);
  }

  @Override
  public WordProbability getWordProbability(String word) throws WordsDataSourceException {
    return getWordProbability(ICategorisedClassifier.DEFAULT_CATEGORY, word);
  }

  @SuppressWarnings("unchecked")
  public WordProbability getWordProbability(String category, String word) 
      throws WordsDataSourceException {
    int matchCount = 0;
    int nonmatchCount = 0;
    List<Map<String,Object>> rows = jdbcTemplate.queryForList(
      "select match_count, nonmatch_count " +
      "from word_probability " +
      "where word = ? and category = ?", 
      new String[] {word, category});
    for (Map<String,Object> row : rows) {
      matchCount =(Integer) row.get("MATCH_COUNT");
      nonmatchCount =(Integer) row.get("NONMATCH_COUNT");
      break;
    }
    return new WordProbability(word, matchCount, nonmatchCount);
  }

  @SuppressWarnings("unchecked")
  public WordProbability[] calcWordsProbability(String category, String[] words) {
    List<WordProbability> wordProbabilities = new ArrayList<WordProbability>();
    List<String> wordsList = Arrays.asList(words);
    String query = "";
//    String query = "select word, match_count, nonmatch_count from word_probability where word in (" +
//      StringUtils.join(new TransformIterator(wordsList.iterator(), quotingLowercasingTransformer), ',') +
//      ") and category=?"; 
    List<Map<String,Object>> rows = jdbcTemplate.queryForList(query, new String[] {category});
    for (Map<String,Object> row : rows) {
      String word = (String) row.get("WORD");
      int matchCount = (Integer) row.get("MATCH_COUNT");
      int nonmatchCount = (Integer) row.get("NONMATCH_COUNT");
      WordProbability wordProbability = new WordProbability(word, matchCount, nonmatchCount);
      wordProbability.setCategory(category);
      wordProbabilities.add(wordProbability);
    }
    return wordProbabilities.toArray(new WordProbability[0]);
  }
  
  public void initWordCountMap() {
    wordCountMap.clear();
  }
  
  public void flushWordCountMap(String category, boolean isMatch) {
    for (String word : wordCountMap.keySet()) {
      int count = wordCountMap.get(word);
      if (isWordInCategory(category, word)) {
        updateWordMatch(category, word, count, isMatch);
      } else {
        insertWordMatch(category, word, count, isMatch);
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  public void removeDuplicateWords() {
    List<Map<String,Object>> rows = jdbcTemplate.queryForList(
      "select word, count(*) dup_count " +
      "from word_probability " +
      "group by word " +
      "having dup_count > 1");
    List<String> words = new ArrayList<String>();
    for (Map<String,Object> row : rows) {
      words.add((String) row.get("WORD"));
    }
    jdbcTemplate.update("delete from word_probability where word in (VALUE)");
  }
  
  private void addWord(String word) {
    int originalCount = 0;
    if (wordCountMap.containsKey(word)) {
      originalCount = wordCountMap.get(word);
    }
    wordCountMap.put(word, (originalCount + 1));
  }
  
  /**
   * Return true if the word is found in the category.
   * @param category the category to look up 
   * @param word the word to look up.
   * @return true or false
   */
  @SuppressWarnings("unchecked")
  private boolean isWordInCategory(String category, String word) {
//    List<Map<String,String>> rows = jdbcTemplate.queryForList(
//      "select word from word_probability where category = ? and word = ?", 
//      new String[] {category, word});
//    return (rows.size() > 0);
      return true;
  }

  /**
   * @param category the category to update.
   * @param word the word to update.
   * @param isMatch if true, the word is a match for the category.
   */
  private void updateWordMatch(String category, String word, int count, boolean isMatch) {
    if (isMatch) { 
      jdbcTemplate.update(
        "update word_probability set match_count = match_count + ? " +
        "where category = ? and word = ?", 
        new Object[] {count, category, word});
    } else {
      jdbcTemplate.update(
        "update word_probability set nonmatch_count = nonmatch_count + ? " +
        "where category = ? and word = ?", 
        new Object[] {count, category, word});
    }
  }

  /**
   * @param category the category to insert.
   * @param word the word to update.
   * @param isMatch if true, the word is a match for the category.
   */
  private void insertWordMatch(String category, String word, int count, boolean isMatch) {
    if (isMatch) {
      jdbcTemplate.update("insert into word_probability(" +
        "category, word, match_count, nonmatch_count) values (?, ?, ?, 0)", 
        new Object[] {category, word, count});
    } else {
      jdbcTemplate.update("insert into word_probability(" +
          "category, word, match_count, nonmatch_count) values (?, ?, 0, ?)", 
          new Object[] {category, word, count});
    }
  }
}
