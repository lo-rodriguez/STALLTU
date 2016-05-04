/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core.classificationFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sf.classifier4J.IStopWordProvider;
import org.apache.commons.lang3.StringUtils;


public class FileDrivenStopWordProvider implements IStopWordProvider {

  private SortedSet<String> words = new TreeSet<String>();
  
  public FileDrivenStopWordProvider(File stopWordFile) throws IOException {
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(new FileInputStream(stopWordFile)));
      String word;
      while ((word = reader.readLine()) != null) {
        words.add(StringUtils.lowerCase(word.trim()));
      }
    } catch (FileNotFoundException e) {
     
    } catch (IOException e) {
    }
  }
  
  public boolean isStopWord(String word) {
    return words.contains(StringUtils.lowerCase(word.trim())) || StringUtils.isNumeric(word);
  }
}
