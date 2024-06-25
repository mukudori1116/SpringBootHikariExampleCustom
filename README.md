## Abstract

This is a starter template that includes the following components:

- Spring Boot
- HikariCP connection pool
- AWS Advanced JDBC Wrapper

## How to start

1. Set up the connection information:

```
$ export PGHOST=<hostname>
$ export PGUSER=<username>
$ export PGPASSWORD=<password>
```

2. Start the server:

```
$ ./gradlew bootRun
```

3. Test the application:

```
$ curl http://localhost:8081/select1
$ curl http://localhost:8081/test-failover
```

## Change settings

You can change settings by editing properties file in `src/main/resources`. SpringBoot server configurations are included in `application.yml`, HikariCP configurations are in `hikari.properties`, and advanced JDBC wrapper configs are in `wrapper.properties`.

## Test fast failover recovery

1. Following command start excecuting query loop to Aurora PostgreSQL DB Cluster/instance.

```
$ curl http://localhost:8081/test-failover
```

The connection executes the query which get the host server ip address.

2. Try failover the DB cluster, and check the log `logs/application.log`

You can adjaust the log level by editing `src/resources/application.yml`.

```
logging:  
  level:  
    root: INFO
    org.postgresql: INFO
    com.zaxxer.hikari: DEBUG
    software.amazon.jdbc: TRACE
  file:
    name: logs/application.log
```

3. Failover recovery get faster if Setting `FailoverMode` changes to `reader-or-writer`.
