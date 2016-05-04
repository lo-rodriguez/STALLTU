/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.classificationFiles;

import java.util.ArrayList;
import java.util.List;
import net.sf.classifier4J.bayesian.BayesianClassifier;
import net.sf.classifier4J.bayesian.WordProbability;
import net.sf.classifier4J.bayesian.WordsDataSourceException;

/**
 * Batches words for performance against the JdbcWordsDataSource. This is 
 * specific to this application's needs, so the constructor forces the caller
 * to provide specific implementations of the super-class's ctor args.
 */
public class BatchingBayesianClassifier extends BayesianClassifier {

  public BatchingBayesianClassifier(JdbcWordsDataSource wordsDataSource, 
      CyberNekoHtmlTokenizer tokenizer, FileDrivenStopWordProvider stopwordsProvider) {
    super(wordsDataSource, tokenizer, stopwordsProvider);
  }
  
  protected boolean isMatch(String category, String input[]) throws WordsDataSourceException {
    return (super.classify(category, input) > super.getMatchCutoff());
  }

  protected double classify(String category, String words[]) throws WordsDataSourceException {
    List<String> nonStopwords = new ArrayList<String>();
    FileDrivenStopWordProvider stopwordsProvider = (FileDrivenStopWordProvider) super.getStopWordProvider();
    for (String word : words) {
      if (stopwordsProvider.isStopWord(word)) {
        continue;
      }
      nonStopwords.add(word);
    }
    JdbcWordsDataSource wds = (JdbcWordsDataSource) super.getWordsDataSource();
    WordProbability[] wordProbabilities = wds.calcWordsProbability(category, nonStopwords.toArray(new String[0]));
    return super.normaliseSignificance(super.calculateOverallProbability(wordProbabilities));
  }

  protected void teachMatch(String category, String words[]) throws WordsDataSourceException {
    JdbcWordsDataSource wds = (JdbcWordsDataSource) super.getWordsDataSource();
    wds.initWordCountMap();
    super.teachMatch(category, words);
    wds.flushWordCountMap(category, true);
  }

  protected void teachNonMatch(String category, String words[]) throws WordsDataSourceException {
    JdbcWordsDataSource wds = (JdbcWordsDataSource) super.getWordsDataSource();
    wds.initWordCountMap();
    super.teachNonMatch(category, words);
    wds.flushWordCountMap(category, false);
  }

}
