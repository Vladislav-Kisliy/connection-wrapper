package com.karlsoft.wrapper;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.karlsoft.wrapper.api.Service;
import com.karlsoft.wrapper.config.ApplicationConfig;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        new App().initServices();
    }

    private void initServices() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-application.xml");
        ApplicationConfig appConfig = (ApplicationConfig) ctx.getBean("appConfig");
        if (appConfig.isPlainServiceEnabled()) {
            ((Service) ctx.getBean("plainProxyService")).start();
        }
        if (appConfig.isSSLServiceEnabled()) {
            ((Service) ctx.getBean("sslProxyService")).start();
        }
    }
}
