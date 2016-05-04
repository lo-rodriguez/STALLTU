/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu;

import java.io.File;
import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

//import net.sf.classifier4J.summariser.ISummariser;
//import net.sf.classifier4J.summariser.SimpleSummariser;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ronasof.unforgiving.stalltu.core.summarization.LuceneSummarizer;
import ronasof.unforgiving.stalltu.core.summarization.SummaryAnalyzer;
/**
 *
 * @author lrodriguezn
 */
public class SummarizerTest {

  private static String[] TEST_FILES = {
    "src/test/resources/data/sujitpal-actors.txt",
    "src/test/resources/data/nytimes-obama.txt",
    "src/test/resources/data/resample-nb.txt"
  };
  
  @Test
  public void testC4JSummarizer() throws Exception {
    for (String testFile : TEST_FILES) {
      String text = FileUtils.readFileToString(new File(testFile), "UTF-8");
      ISummariser summarizer = new SimpleSummariser();
      System.out.println("Input: " + testFile);
      String summary = summarizer.summarise(text, 2);
      // replace newlines with ellipses
      summary = summary.replaceAll("\n+", "...");
      System.out.println(">>> Summary (from C4J): " + summary);
    }
  }

  @Test
  public void testLuceneSummarizer() throws Exception {
    for (String testFile : TEST_FILES) {
      String text = FileUtils.readFileToString(new File(testFile), "UTF-8");
      LuceneSummarizer summarizer = new LuceneSummarizer();
      summarizer.setAnalyzer(new SummaryAnalyzer());
      summarizer.setNumSentences(2);
      summarizer.setTopTermCutoff(0.5F);
      summarizer.setSentenceDeboost(0.2F);
      summarizer.init();
      System.out.println("Input: " + testFile);
      String summary = summarizer.summarize(text);
      System.out.println(
        ">>> Summary (from LuceneSummarizer): " + summary);
    }
  }
}