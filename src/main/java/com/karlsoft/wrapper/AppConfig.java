/*
 * Copyright (C) 2016 vlad
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.karlsoft.wrapper;

import com.karlsoft.wrapper.api.ApplicationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 *
 * @author vlad
 */


//@Configuration
//@PropertySource("classpath:application.properties")
////@Configuration
////@PropertySources({
////    @PropertySource("classpath:application1.properties"),
////    @PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true)
////})
//public class AppConfig implements EnvironmentAware {
//
//    private Environment environment;
//
//    @Override
//    public void setEnvironment(final Environment environment) {
//        this.environment = environment;
//    }
//
//    public String getServerPort() {
//        return environment.getProperty("server.port");
//    }
//
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }
//}

@Component("application.applicationConfiguration")
//@Configuration
//@PropertySources({
//    @PropertySource("classpath:application1.properties"),
//    @PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true)
//})
public class AppConfig implements ApplicationConfig {

    @Value("${platform.connection.host}")
    private String connectionHost;
    @Value("${server.port}")
    private String connectionPort;

    @Override
    public String getServerPort() {
        return connectionPort;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
