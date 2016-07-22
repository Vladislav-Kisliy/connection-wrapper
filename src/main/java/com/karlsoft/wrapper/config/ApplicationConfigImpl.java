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
package com.karlsoft.wrapper.config;

/**
 * Default ApplicationConfig implementation.
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class ApplicationConfigImpl implements ApplicationConfig {

    private final String plainServiceStatus;
    private final String sslServiceStatus;
    private final String socks4ServiceStatus;
    private final String socks5ServiceStatus;
    private final String socks5ServiceAuthStatus;
    private final String multiplierServiceStatus;

    public ApplicationConfigImpl(String plainServiceStatus, String sslServiceStatus, String socks4ServiceStatus, String socks5ServiceStatus, String socks5ServiceAuthStatus, String multiplierServiceStatus) {
        this.plainServiceStatus = plainServiceStatus;
        this.sslServiceStatus = sslServiceStatus;
        this.socks4ServiceStatus = socks4ServiceStatus;
        this.socks5ServiceStatus = socks5ServiceStatus;
        this.socks5ServiceAuthStatus = socks5ServiceAuthStatus;
        this.multiplierServiceStatus = multiplierServiceStatus;
    }

    @Override
    public Boolean isPlainServiceEnabled() {
        return checkString(plainServiceStatus);
    }

    @Override
    public Boolean isSSLServiceEnabled() {
        return checkString(sslServiceStatus);
    }

    @Override
    public Boolean isSocks4ServiceEnabled() {
        return checkString(socks4ServiceStatus);
    }

    @Override
    public Boolean isSocks5ServiceEnabled() {
        return checkString(socks5ServiceStatus);
    }
    
    @Override
    public Boolean isMultiplierServiceEnabled() {
        return checkString(multiplierServiceStatus);
    }
    
    @Override
    public Boolean isSocks5ServiceAuthEnabled() {
        return checkString(socks5ServiceAuthStatus);
    }
    
    private Boolean checkString(String string) {
       return "true".equalsIgnoreCase(string);
    }
}
