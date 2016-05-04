/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

/**
 *
 * @author lrodriguezn
 */
public class IACriteria {

    //Criterios
    static final private long MAX_SIZE_FILE = 10000000;
    static final private long MIN_SIZE_FILE = 1024;
    static final private int MIN_USED_WORD = 3;
    static final private int MAX_USED_WORD = 100;
    static final private String USER = System.getProperty("user.name");
    static final private long MAX_DATE_FILE_BK = 365 * 24 * 60 * 60 * 1000;
    static final private long MIN_DATE_FILE_BK = 24 * 60 * 60 * 1000;
    static final private String EXT_FILE_USER[] = {"txt", "pdf", "doc", "jpg", "mp3"};
    static final private String EXT_FILE_DEVELOPER[] = {"pc", "java", "js", "html", "xml", "c++", "sql"};
    static final private String EXT_FILE_SYSTEM_WIN[] = {"dll", "exe", "mof", "cpl"};
    static final private String EXT_FILE_BACKUP[] = {"zip", "tar", "gz", "rar", "bk"};
    static final private String EXT_FILE_TEMP[] = {"tmp", "temp", "out", "conf", "dat", "data"};
    static final private String DICTIONARY_KEYWORDS_ES[] = {"lorenzo", "rodriguez", "privado", "indra", "proyecto", "cuenta", "contraseña", "importante", "mail"};

    protected Boolean isRelevant(File file, Short op) throws IOException {
        if (!criteriaSize(file, op)) {
            return false;
        } else if (!criteriaMimeType(file, op)) {
            return false;
        }
        return true;
    }

    private boolean criteriaSize(File file, Short op) {
        switch (op) {
            case 1:
                if (file.length() > MIN_SIZE_FILE && file.length() <= MAX_SIZE_FILE) {
                    return true;
                }
                break;
            case 2:
                break;
        }
        return false;
    }

    private boolean criteriaMimeType(File file, Short op) throws IOException {

        String mimeType = Files.probeContentType(file.toPath());
        long t = Files.getLastModifiedTime(file.toPath()).toMillis();
//        Files.getOwner(file.toPath(), options);
        FileOwnerAttributeView view = Files.getFileAttributeView(file.toPath(),
                FileOwnerAttributeView.class);
        UserPrincipal userPrincipal = view.getOwner();
        //System.out.println(userPrincipal.getName());
        switch (op) {
            case 1:
                for (String m : EXT_FILE_USER) {
                    if (mimeType.equals(m) && t >= MIN_DATE_FILE_BK && isTheContentRelevant(file.getAbsolutePath())) {
                        return true;
                    }
                }
                for (String m : EXT_FILE_BACKUP) {
                    if (mimeType.equals(m) && t <= MAX_DATE_FILE_BK) {
                        return true;
                    }
                }
                for (String m : EXT_FILE_DEVELOPER) {
                    if (mimeType.equals(m) && t <= MAX_DATE_FILE_BK && t >= MIN_DATE_FILE_BK) {
                        return true;
                    }
                }
                for (String m : EXT_FILE_SYSTEM_WIN) {
                    if (mimeType.equals(m) && t <= MAX_DATE_FILE_BK && userPrincipal.getName().equals(USER)) {
                        return true;
                    }
                }
                for (String m : EXT_FILE_TEMP) {
                    if (mimeType.equals(m) && t <= MAX_DATE_FILE_BK && isTheContentRelevant(file.getAbsolutePath())) {
                        return true;
                    }
                }
                break;
            case 2:
                break;
        }
        return false;
    }

    private boolean isTheContentRelevant(String nameFile) {
        ReadAnalyzeDocuments readDocuments = new ReadAnalyzeDocuments();
        readDocuments.readMyDocument(nameFile);
        DocumentSummary summary = readDocuments.getSummary();
        String wordMostUsed = summary.getWordMostUsedDocument();
        long usedWord = summary.getAmountTimesWordUsedDocument();
        for (String w : DICTIONARY_KEYWORDS_ES) {
            if (w.equals(wordMostUsed) && usedWord > MIN_USED_WORD) {
                return true;
            }
        }

        for (String w : DICTIONARY_KEYWORDS_ES) {
            if (summary.getKeywords().containsKey(w)) {
                if (summary.getKeywords().get(w) > MIN_USED_WORD) {
                    return true;
                }
            }
        }
        return false;
    }
}
