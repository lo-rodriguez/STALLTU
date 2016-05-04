/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.summarization;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.PorterStemFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 * Special purpose analyzer that uses a chain of PorterStemFilter, 
 * StopFilter, LowercaseFilter and StandardFilter to wrap a 
 * StandardTokenizer. The StopFilter uses a custom stop word set
 * adapted from:
 * http://www.onjava.com/onjava/2003/01/15/examples/EnglishStopWords.txt
 * For ease of maintenance, we put these words in a flat file and
 * import them on analyzer construction.
 */
public class SummaryAnalyzer extends Analyzer {

  private Set<String> stopset;
  
  public SummaryAnalyzer() throws IOException {
    String[] stopwords = filterComments(StringUtils.split(
      FileUtils.readFileToString(new File(
      "src/main/resources/stopwords.txt"), "UTF-8")));
    this.stopset = StopFilter.makeStopSet(stopwords, true);
  }
  
  @Override
  public TokenStream tokenStream(String fieldName, Reader reader) {
    return new PorterStemFilter(
      new StopFilter(
        new LowerCaseFilter(
          new StandardFilter(
            new StandardTokenizer(reader))), stopset));
  }
  
  private String[] filterComments(String[] input) {
    List<String> stopwords = new ArrayList<>();
    for (String stopword : input) {
      if (! stopword.startsWith("#")) {
        stopwords.add(stopword);
      }
    }
    return stopwords.toArray(new String[0]);
  }
}
