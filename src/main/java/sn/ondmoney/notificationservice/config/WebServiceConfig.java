package sn.ondmoney.notificationservice.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Configuration pour les Web Services SOAP
 * Cette classe configure l'endpoint SOAP et génère le WSDL automatiquement
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    /**
     * Configure le servlet qui va traiter les requêtes SOAP
     * URL : /ws/*
     */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * Définit le WSDL qui sera généré automatiquement
     * Accessible à : http://localhost:8081/ws/notifications.wsdl
     */
    @Bean(name = "notifications")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema notificationsSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("NotificationsPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://ondmoney.sn/notification/schemas");
        wsdl11Definition.setSchema(notificationsSchema);
        return wsdl11Definition;
    }

    /**
     * Charge le schéma XSD
     */
    @Bean
    public XsdSchema notificationsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/notifications.xsd"));
    }
}
