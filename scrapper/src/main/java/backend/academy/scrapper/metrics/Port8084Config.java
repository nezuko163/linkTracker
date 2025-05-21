package backend.academy.scrapper.metrics;

import backend.academy.scrapper.controllers.MetricsController;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
public class Port8084Config {

    @Bean
    public ServletRegistrationBean<DispatcherServlet> dispatcherServlet8084() {
        // Создаём отдельный контекст для порта 8084
        WebApplicationContext context = createContext();

        // Создаём DispatcherServlet с этим контекстом
        DispatcherServlet servlet = new DispatcherServlet(context);

        // Регистрируем его и привязываем только к порту 8084
        ServletRegistrationBean<DispatcherServlet> registration =
            new ServletRegistrationBean<>(servlet, "/");

        registration.setName("dispatcherServlet8084");
        registration.setLoadOnStartup(1);
        registration.addInitParameter("server.port.include", "8084");


        return registration;
    }

    private WebApplicationContext createContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(MetricsController.class);
        return context;
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> additionalPortCustomizer() {
        return factory -> {
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setPort(8084);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }
}
