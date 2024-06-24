/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.amazon.SpringBootHikariExample;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.annotation.PostConstruct;
import software.amazon.jdbc.plugin.failover.FailoverSuccessSQLException;

@RestController
public class ApiController {

    Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private AwsWrapperDataSourceConfiguration awsWrapperDataSourceConfiguration;

    @PostConstruct
    private void additionalDataSourceConfigs() {
        // 自動作成された HikariDataSource に追加の処理を行いたい場合はここで
        // HikariDataSource ds = dataSource;
        // ...
    }

    @GetMapping(value = "/select1")
    public Integer getOne() {
        DataSource ds = awsWrapperDataSourceConfiguration.AwsWrapperDataSource();
        try {
            Connection conn = ds.getConnection();
                try {
                    Statement stetement = conn.createStatement();
                    String SQL = "SELECT 1;";
                    ResultSet rs = stetement.executeQuery(SQL);
                    rs.next();
                    return rs.getInt(1);
                } catch (FailoverSuccessSQLException fse) {
                    logger.info("failover success");
                } catch (Exception e) {
                    logger.error(e.toString());
                }
        } catch (SQLException e) {
            logger.error(e.toString());
        }
        return -1;
    }

    @GetMapping(value = "/test-failover")
    public void testFailover() {

        boolean isFailover = false;
        Integer loopAfterFailover = 0;
        
        DataSource ds = awsWrapperDataSourceConfiguration.AwsWrapperDataSource();
        try {
            Connection conn = ds.getConnection();
            while (true) {
                try {
                    Statement stetement = conn.createStatement();
                    String SQL = "SELECT inet_server_addr();";
                    ResultSet rs = stetement.executeQuery(SQL);
                    rs.next();
                    logger.info(rs.getString(1));
                } catch (FailoverSuccessSQLException fse) {
                    logger.info("failover success");
                    isFailover = true;
                } catch (Exception e) {
                    logger.error(e.toString());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error(e.toString());
                }
                loopAfterFailover = isFailover ? loopAfterFailover + 1 : 0;
                if (loopAfterFailover > 5) {
                    logger.info("failover test finished");
                    break;
                }
            }
        } catch (SQLException e) {
            logger.error(e.toString());
        }
    }
}
