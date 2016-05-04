/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hwpf.extractor.*;
import org.apache.poi.hwpf.usermodel.HeaderStories;
//New Import

import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

//
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

/**
 *
 * @author lrodriguezn
 */
public class ReadAnalyzeDocuments {

    private DocumentSummary docSummary;
    
    public ReadAnalyzeDocuments(){
        docSummary = new DocumentSummary();
    }

    public void readMyDocument(String fileName) {
        POIFSFileSystem fs = null;     
        try {
          FileInputStream f =  new FileInputStream(fileName);
            fs = new POIFSFileSystem(f);
            HWPFDocument doc = new HWPFDocument(fs);

            /**
             * Read the content *
             */
            readParagraphs(doc);

            int pageNumber = 1;

            /**
             * We will try reading the header for page 1*
             */
            readHeader(doc, pageNumber);

            /**
             * Let's try reading the footer for page 1*
             */
            readFooter(doc, pageNumber);

            /**
             * Read the document summary*
             */
            readDocumentSummary(doc);
            
            String text = processDOC(f) ;

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable ex) {
            Logger.getLogger(ReadAnalyzeDocuments.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readParagraphs(HWPFDocument doc) throws Exception {
        WordExtractor we = new WordExtractor(doc);

        /**
         * Get the total number of paragraphs*
         */
        String[] paragraphs = we.getParagraphText();
        System.out.println("Total Paragraphs: " + paragraphs.length);

        for (int i = 0; i < paragraphs.length; i++) {

            System.out.println("Length of paragraph " + (i + 1) + ": " + paragraphs[i].length());
            System.out.println(paragraphs[i].toString());

        }

    }

    public static void readHeader(HWPFDocument doc, int pageNumber) {
        HeaderStories headerStore = new HeaderStories(doc);
        String header = headerStore.getHeader(pageNumber);
        System.out.println("Header Is: " + header);

    }

    public static void readFooter(HWPFDocument doc, int pageNumber) {
        HeaderStories headerStore = new HeaderStories(doc);
        String footer = headerStore.getFooter(pageNumber);
        System.out.println("Footer Is: " + footer);

    }

    public void readDocumentSummary(HWPFDocument doc) {
        DocumentSummaryInformation summaryInfo = doc.getDocumentSummaryInformation();
        String category = summaryInfo.getCategory();
        docSummary.setCategory(category);
        String company = summaryInfo.getCompany();
        docSummary.setCompany(company);
 
       ///////////////////////////////

    }

    private String processDOC(FileInputStream fs) throws Throwable {
        POIFSFileSystem poifs = new POIFSFileSystem(fs);
        HWPFDocument doc = new HWPFDocument(fs);
        DirectoryEntry dir = poifs.getRoot();
        SummaryInformation si = null;
        DocumentEntry siEntry = (DocumentEntry) dir.getEntry(SummaryInformation.DEFAULT_STREAM_NAME);
        DocumentInputStream dis = new DocumentInputStream(siEntry);
        PropertySet ps = new PropertySet(dis);
        si = new SummaryInformation(ps);
        if (si.getTitle() != null) {
           docSummary.setDocumentTitle(si.getTitle());
        }
        if (si.getAuthor() != null) {
           docSummary.setAutor(si.getAuthor());
        }
        /*if (si.getSubject() != null) {
            metadata.put("subject", si.getSubject());
        }
        if (si.getKeywords() != null) {
            metadata.put("keywords", si.getKeywords());
        }*/
        
        docSummary.setDateCreate(si.getCreateDateTime());
        return (String) doc.getText().toString();
    }

    private String processPDF(File file, String outputDateMask, Map<String, String> metadata) throws Throwable {
        PDFTextStripper pdfS = new PDFTextStripper();
        PDDocument pdoc = new PDDocument();
        pdoc = PDDocument.load(file);
        PDDocumentInformation info = pdoc.getDocumentInformation();
        if (info.getTitle() != null) {
            metadata.put("title", info.getTitle());
        }
        if (info.getAuthor() != null) {
            metadata.put("author", info.getAuthor());
        }
        if (info.getSubject() != null) {
            metadata.put("subject", info.getSubject());
        }
        if (info.getKeywords() != null) {
            metadata.put("keywords", info.getKeywords());
        }
        if (info.getCreator() != null) {
            metadata.put("creator", info.getCreator());
        }
        if (info.getProducer() != null) {
            metadata.put("producer", info.getProducer());
        }
        SimpleDateFormat sdf = new SimpleDateFormat(outputDateMask);
        if (info.getModificationDate() != null) {
            metadata.put("published", sdf.format(info.getModificationDate().getTime()));
        } else if (info.getCreationDate() != null) {
            metadata.put("published", sdf.format(info.getCreationDate().getTime()));
        }
        return pdfS.getText(pdoc);
    }
    
    private void countWord(String st) {
        Iterable<String> words = Splitter.on(" ").trimResults().split(st);
         Map<String, Integer> countByWords = new HashMap<String, Integer>();    
        Multiset<String> wordsMultiset = HashMultiset.create();
        for (String string : words) {
            wordsMultiset.add(string.toLowerCase());
        }
        Set<String> result = wordsMultiset.elementSet();
        int temp=0;
        String wordFind=null;
        for (String string : result) {
            int count =wordsMultiset.count(string);
            countByWords.put(string, count);
                if(count>temp){
                    wordFind=string;
                    temp =count;
                }            
        }
     docSummary.setAmountTimesWordUsedDocument(temp);
     docSummary.setWordMostUsedDocument(wordFind);
     docSummary.setKeywords(countByWords);
    }
    
    public DocumentSummary getSummary(){
        return docSummary;
    }
}
