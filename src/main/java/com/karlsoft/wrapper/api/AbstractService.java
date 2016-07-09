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
package com.karlsoft.wrapper.api;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vlad
 */
public abstract class AbstractService implements Service {
    
    private String serviceName;
    
    @Override
    public void start() {
        beforeStart();
        try {
            startService();
        } catch (InterruptedException ex) {
            Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Start service problem", ex);
        }
        afterStart();
    }
    
    protected void beforeStart() {
        System.out.println("Starting "+serviceName);
    }
    
    protected void afterStart() {
        System.out.println("Started "+serviceName);
    }
    
    protected abstract void startService() throws InterruptedException;
}
