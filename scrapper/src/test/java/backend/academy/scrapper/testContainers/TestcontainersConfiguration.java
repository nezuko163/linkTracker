package backend.academy.scrapper.testContainers;

// isolated from the "bot" module's containers!
// @TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    //    @Bean
    //    @RestartScope
    //    @ServiceConnection(name = "redis")
    //    GenericContainer<?> redisContainer() {
    //        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);
    //    }
    //
    //    @Bean
    //    @RestartScope
    //    @ServiceConnection
    //    PostgreSQLContainer<?> postgresContainer() {
    //        return new PostgreSQLContainer<>("postgres:17.4")
    //            .withExposedPorts(0)
    //            .withDatabaseName("testdb")
    //            .withUsername("test")
    //            .withPassword("test");
    //    }
}
