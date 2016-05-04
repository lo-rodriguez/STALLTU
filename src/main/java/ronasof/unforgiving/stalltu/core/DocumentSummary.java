/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author lrodriguezn
 */
public class DocumentSummary {
//    number of words in the document
    private long  numWordDocument;
//word most used in the document
    private String wordMostUsedDocument;
//amount of times you use the word most used in the document
    private long amountTimesWordUsedDocument;
//    Document title
    private String documentTitle;
//category
    private String category;
 //company
    private String company;
 //keywords   
    Map <String,Integer> keywords;
//Autor 
    private String autor;
 //Date create   
    private Date dateCreate;

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

 

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public long getNumWordDocument() {
        return numWordDocument;
    }

    public void setNumWordDocument(long numWordDocument) {
        this.numWordDocument = numWordDocument;
    }

    public String getWordMostUsedDocument() {
        return wordMostUsedDocument;
    }

    public void setWordMostUsedDocument(String wordMostUsedDocument) {
        this.wordMostUsedDocument = wordMostUsedDocument;
    }

    public long getAmountTimesWordUsedDocument() {
        return amountTimesWordUsedDocument;
    }

    public void setAmountTimesWordUsedDocument(long amountTimesWordUsedDocument) {
        this.amountTimesWordUsedDocument = amountTimesWordUsedDocument;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Map<String, Integer> getKeywords() {
        return keywords;
    }

    public void setKeywords(Map<String, Integer> keywords) {
        this.keywords = keywords;
    }
    
}
