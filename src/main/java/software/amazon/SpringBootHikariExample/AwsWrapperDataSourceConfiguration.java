package software.amazon.SpringBootHikariExample;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.zaxxer.hikari.HikariDataSource;

import software.amazon.jdbc.ds.AwsWrapperDataSource;

import com.zaxxer.hikari.HikariConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AwsWrapperDataSourceConfiguration {

    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    DataSourceProperties dataSourceProperties;

    @Bean
    HikariDataSource AwsWrapperDataSource() {

        final String USER = System.getenv("PGUSER") != null ? System.getenv("PGUSER") : "postgres";
        final String PASSWORD = System.getenv("PGPASSWORD");
        if (PASSWORD == null) {
            logger.error("cannot get password. set the PGPASSWORD");
        }
        final String DATABASE = System.getenv("PGDATABASE") != null ? System.getenv("PGDATABASE") : "postgres";
        final String PORT = System.getenv("PGPORT") != null ? System.getenv("PGPORT") : "5432";
        final String HOST = System.getenv("PGHOST");
        if (HOST == null) {
            logger.error("cannot get host server name. set the PGHOST");
        }

        Properties targetDataSourceProps = new Properties();
        try {
            targetDataSourceProps.load(Files.newBufferedReader(Paths.get("src/main/resources/wrapper.properties"), StandardCharsets.UTF_8));
        } catch (IOException e) {
            // ファイル読み込みに失敗
            logger.error(String.format("Failed to read properties file. file:%s", "src/main/resources/wrapper.properties"), e);
        }
        
        HikariConfig config = new HikariConfig("src/main/resources/hikari.properties");
        
        config.setDataSourceClassName(AwsWrapperDataSource.class.getName());
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.addDataSourceProperty("jdbcProtocol", "jdbc:postgresql:");
        config.addDataSourceProperty("serverName", HOST);
        config.addDataSourceProperty("serverPort", PORT);
        config.addDataSourceProperty("database", DATABASE);
        config.addDataSourceProperty("targetDataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        
        config.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);

        return new HikariDataSource(config);
    }
}
