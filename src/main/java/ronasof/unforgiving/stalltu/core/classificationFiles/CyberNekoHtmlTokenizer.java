/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.classificationFiles;



import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import java.io.ByteArrayInputStream;
import net.sf.classifier4J.DefaultTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CyberNekoHtmlTokenizer extends DefaultTokenizer {

  public CyberNekoHtmlTokenizer() {
    super();
  }
  
  public CyberNekoHtmlTokenizer(int tokenizerConfig) {
    super(tokenizerConfig);
  }
  
  /**
   * Uses the Cyberneko HTML parser to parse out the body content from the
   * HTML file as a stream of text.
   * @see net.sf.classifier4J.ITokenizer#tokenize(java.lang.String)
   */
  public String[] tokenize(String input) {
    return super.tokenize(getBody(input));
  }
  
  public String getBody(String input) {
    try {
      DOMParser parser = new DOMParser();
      parser.parse(new InputSource(new ByteArrayInputStream(input.getBytes())));
      Document doc = parser.getDocument();
      NodeList bodyTags = doc.getElementsByTagName("BODY");
      if (bodyTags.getLength() == 0) {
        throw new Exception("No body tag in this HTML document");
      }
      Node bodyTag = bodyTags.item(0);
      return bodyTag.getTextContent();
    } catch (Exception e) {
      throw new RuntimeException("HTML Parsing failed on this document", e);
    }
  }
}