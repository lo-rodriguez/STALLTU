/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu;

/**
 *
 * @author lrodriguezn
 */


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ronasof.unforgiving.stalltu.app.Configure;
import ronasof.unforgiving.stalltu.core.FileTreeWalk;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Configure.class)
@TestPropertySource(locations="classpath:test.properties")
public class TestGeneral {
    @Autowired
    JdbcTemplate jdbTemplate;
    @Test
    public void testVisitFiles() throws IOException{
        Path path = Paths.get(".");
        System.out.print("Comenzando a testear");
        FileTreeWalk visit = new FileTreeWalk() ;
        Files.walkFileTree(path, visit);        
    }
}
