package com.karlsoft.wrapper;

import com.karlsoft.wrapper.api.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App {

    @Autowired
    private ApplicationConfig appConfig;

    public static void main(String[] args) {
        new App().startup();
        System.out.println("Hello World!");

    }

    private void startup() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("spring-application.xml");
//        Thinker volunteer = (Thinker) ctx.getBean("volunteer");
//        AppConfig appConfig = (AppConfig) ctx.getBean("appconfig");
        System.out.println("port =" + appConfig.getServerPort());
    }
}
