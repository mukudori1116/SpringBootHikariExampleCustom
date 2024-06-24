package software.amazon.SpringBootHikariExample;

import java.util.Properties;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcConnectionDetails;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import software.amazon.jdbc.ds.AwsWrapperDataSource;


@Configuration
public class AwsWrapperDataSourceConfiguration {

    @Autowired
    DataSourceProperties dataSourceProperties;

    @Bean
    HikariDataSource AwsWrapperDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setMaximumPoolSize(5);
        ds.setIdleTimeout(60000);
        ds.setUsername(dataSourceProperties.getUsername());
        ds.setPassword(dataSourceProperties.getPassword());

        ds.setDataSourceClassName(AwsWrapperDataSource.class.getName());
        ds.addDataSourceProperty("jdbcProtocol", "jdbc:postgresql:");
        ds.addDataSourceProperty("serverName", "iakuf-apg16.c309m6ylho0k.ap-northeast-1.rds.amazonaws.com");
        ds.addDataSourceProperty("serverPort", "5432");
        ds.addDataSourceProperty("database", "test");

        Properties targetDataSourceProps = new Properties();
        targetDataSourceProps.setProperty("wrapperProfile", "D0");
        ds.addDataSourceProperty("targetDataSourceProperties", targetDataSourceProps);

        return ds;
    }
}
