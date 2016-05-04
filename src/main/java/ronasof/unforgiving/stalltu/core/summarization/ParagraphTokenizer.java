/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.summarization;

/**
 *
 * @author lrodriguezn
 */
import java.io.File;
import java.text.BreakIterator;

import org.apache.commons.io.FileUtils;

import com.ibm.icu.text.RuleBasedBreakIterator;
import java.io.InputStream;

/**
 * Tokenizes the input into paragraphs, using ICU4J's rule-based break
 * iterator. Rule file is adapted from the rule file used internally by
 * the RBBI sentence tokenizer.
 */
public class ParagraphTokenizer {

  private String text;
  private int index = 0;
  private final RuleBasedBreakIterator breakIterator;
  
    public ParagraphTokenizer() throws Exception {
        super();
//    String rules =FileUtils.readFileToString(
//      new File("src/main/resources/paragraph_break_rules.txt"), "UTF-8");
//    this.breakIterator = new RuleBasedBreakIterator(
//      FileUtils.readFileToString(
//      new File("src/main/resources/paragraph_break_rules.txt"), "UTF-8"));
        
        InputStream is = ParagraphTokenizer.class.getClassLoader().getResourceAsStream("paragraph_break_rules.txt");
        
        this.breakIterator = RuleBasedBreakIterator.getInstanceFromCompiledRules(is);
        is.close();
    }
  
  public void setText(String text) {
    this.text = text;
    this.breakIterator.setText(text);
    this.index = 0;
  }
  
  public String nextParagraph() {
    int end = breakIterator.next();
    if (end == BreakIterator.DONE) {
      return null;
    }
    String sentence = text.substring(index, end);
    index = end;
    return sentence;
  }
}
