/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.classificationFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.classifier4J.ClassifierException;
import net.sf.classifier4J.DefaultTokenizer;
import net.sf.classifier4J.bayesian.WordsDataSourceException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;



/**
 *
 * @author lrodriguezn
 */
public class AutoTagger {

  private static final double CLASSIFICATION_CUTOFF_PROBABILITY = 0.5;
  
  private File stopwordFile;
  private DataSource dataSource;
  
  private final Map<String,BatchingBayesianClassifier> classifiers = 
    new HashMap<>();
  private final MultiMap categoryMap = new MultiValueMap();
  
  public AutoTagger() {
    super();
  }
  
  public void setStopwordFile(File stopwordFile) {
    this.stopwordFile = stopwordFile;
  }
  
  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
  
  public void addTrainingFiles(String category, File[] trainingFiles) throws IOException {
    for (File trainingFile : trainingFiles) {
      categoryMap.put(category, trainingFile);
    }
    // if an instance of the classifier does not exist for category, create one
    if (! classifiers.containsKey(category)) {
      BatchingBayesianClassifier classifier = new BatchingBayesianClassifier(
        new JdbcWordsDataSource(dataSource),
        new CyberNekoHtmlTokenizer(DefaultTokenizer.BREAK_ON_WORD_BREAKS),
        new FileDrivenStopWordProvider(stopwordFile));
      classifiers.put(category, classifier);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void train() throws WordsDataSourceException, ClassifierException, IOException {
    List<String> categoryList = new ArrayList<>();
    categoryList.addAll(categoryMap.keySet());
    // teach the classifiers in all categories
    for (int i = 0; i < categoryList.size(); i++) {
      String matchCategory = categoryList.get(i);
      List<String> nonmatchCategories = new ArrayList<>();
      for (int j = 0; j < categoryList.size(); j++) {
        if (i != j) {
          nonmatchCategories.add(categoryList.get(j));
        }
      }
      BatchingBayesianClassifier classifier = classifiers.get(matchCategory);
      List<File> teachMatchFiles = (List<File>) categoryMap.get(matchCategory);
      for (File teachMatchFile : teachMatchFiles) {
        String trainingFileName = teachMatchFile.getName();
        classifier.teachMatch(matchCategory, FileUtils.readFileToString(teachMatchFile, "UTF-8"));
        classifiers.put(matchCategory, classifier);
        for (String nonmatchCategory : nonmatchCategories) {
            classifier.teachNonMatch(nonmatchCategory,
            FileUtils.readFileToString(teachMatchFile, "UTF-8"));
          classifiers.put(nonmatchCategory, classifier);
        }
      }
    }
    classifiers.clear();
  }
  
  public boolean isFileInCategory(String category, File file)
      throws ClassifierException, WordsDataSourceException, IOException {
    return getProbabilityOfFileInCategory(category, file) >= CLASSIFICATION_CUTOFF_PROBABILITY;
  }
  
  public double getProbabilityOfFileInCategory(String category, File file) 
      throws ClassifierException, WordsDataSourceException, IOException {
    if (! classifiers.containsKey(category)) {
      BatchingBayesianClassifier classifier = new BatchingBayesianClassifier(
        new JdbcWordsDataSource(dataSource),
        new CyberNekoHtmlTokenizer(DefaultTokenizer.BREAK_ON_WORD_BREAKS),
        new FileDrivenStopWordProvider(stopwordFile));
      classifiers.put(category, classifier);
    }
    BatchingBayesianClassifier classifier = classifiers.get(category);
    if (classifier == null) {
      throw new IllegalArgumentException("Unknown category:" + category);
    }
    return classifier.classify(category, FileUtils.readFileToString(file, "UTF-8"));
  }
}
