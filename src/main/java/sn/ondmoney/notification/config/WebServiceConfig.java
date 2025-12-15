package sn.ondmoney.notification.config;

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
 * Configuration Spring Web Services pour les endpoints SOAP
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    /**
     * Configuration du servlet MessageDispatcher pour SOAP
     * L'URL de base sera : http://localhost:8081/ws/*
     */
    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
        ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    /**
     * Définition WSDL pour le service de notification
     * Accessible via : http://localhost:8081/ws/notifications.wsdl
     */
    @Bean(name = "notifications")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema notificationSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("NotificationPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://ondmoney.sn/notification");
        wsdl11Definition.setSchema(notificationSchema);
        return wsdl11Definition;
    }

    /**
     * Schéma XSD définissant le contrat SOAP
     */
    @Bean
    public XsdSchema notificationSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/schema.xsd"));
    }

}
