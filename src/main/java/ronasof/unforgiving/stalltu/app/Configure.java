/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.app;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 *
 * @author lrodriguezn
 */
@Configuration
@ComponentScan("ronasof.unforgiving.stalltu")
//@PropertySource("classpath:application.properties")
public class Configure {
//    @Value("${spring.datasource.driverClassName}")
//    private String databaseDriverClassName;
//
//    @Value("${spring.datasource.url}")
//    private String datasourceUrl;
//
//    @Value("${spring.datasource.username}")
//    private String databaseUsername;
//
//    @Value("${spring.datasource.password}")
//    private String databasePassword;

    @Bean(name="homeDirectoryScan")
    public String getHomeDirectoryScan(){
        return null;
    }
    @Bean(name="homeDirectoryFtp")
    public String getHomeDirectoryFtp(){
        return null;
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

@Bean(name="jdbTemplate")
	public JdbcTemplate getJdbcTemplate(){
	  return new JdbcTemplate(dataSource());
	}
	private DataSource dataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder
			.setType(EmbeddedDatabaseType.HSQL) //.H2 or .DERBY
			.addScript("/db/sql/create-db.sql")
			.addScript("/db/sql/insert-data.sql")
			.build();
		return db;
	}

   
    
}
