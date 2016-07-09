package com.karlsoft.wrapper;

import com.karlsoft.wrapper.config.ApplicationConfigImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.karlsoft.wrapper.api.Service;

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
        ApplicationConfigImpl appConfig = (ApplicationConfigImpl) ctx.getBean("appConfig");
        if (appConfig.isPlainServiceEnabled()) {
            ((Service) ctx.getBean("plainProxyService")).start();
        }
    }
}
