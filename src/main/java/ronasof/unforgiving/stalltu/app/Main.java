package ronasof.unforgiving.stalltu.app;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author lrodriguezn
 */
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableAutoConfiguration
public class Main {

    @RequestMapping("/test")
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
//        SpringApplication app = new SpringApplication(ConfigureApp.class);
//        app.setBannerMode(Banner.Mode.OFF);
//        app.run(args);
        SpringApplication.run(Main.class, args);
    }
}
