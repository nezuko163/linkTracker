package backend.academy.scrapper.testContainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@TestConfiguration
@TestPropertySource(properties = {"spring.config.location=scrapper/src/main/resources/application-test.yaml"})
public class BaseIntegrationTest {

    private String jdbcUrl;
    private String username;
    private String password;

    @Value("${spring.liquibase.change-log}")
    private String changeLogPath;

    //    @Bean
    //    @RestartScope
    //    @ServiceConnection
    //    KafkaContainer kafkaContainer() {
    //        var imageName = DockerImageName.parse("confluentinc/cp-kafka:latest")
    //            .asCompatibleSubstituteFor("apache/kafka");
    //        var contaiter = new KafkaContainer(imageName);
    //        contaiter.start();
    //        return contaiter;
    //    }

    @Bean
    @RestartScope
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        var postgres = new PostgreSQLContainer<>("postgres:17.4")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");

        postgres.start();

        jdbcUrl = postgres.getJdbcUrl();
        username = postgres.getUsername();
        password = postgres.getPassword();

        runLiquibaseMigrations();

        return postgres;
    }

    void runLiquibaseMigrations() {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Database database =
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(changeLogPath, new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
