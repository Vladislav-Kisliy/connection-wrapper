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
 * First simple implementation of Service interface.
 * Prints messages to log before and after service starting.
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public abstract class AbstractService implements Service {
    
    protected String serviceName = "UKNOWN";
    private static final Logger LOG = Logger.getLogger(AbstractService.class.getName());
    
    @Override
    public void start() {
        beforeStart();
        try {
            startService();
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "Start service problem", ex);
        }
        afterStart();
    }
    
    @Override
    public void stop() {
        beforeStop();
        try {
            stopService();
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "Start service problem", ex);
        }
        afterStop();
    }
    
    
    private void beforeStart() {
        LOG.log(Level.INFO, "Starting {0} service", serviceName);
    }
    
    private void afterStart() {
        LOG.log(Level.INFO, "Started {0} service", serviceName);
    }
    
    protected void beforeStop() {
        LOG.log(Level.INFO, "Stoping {0} service", serviceName);
    }
    
    private void afterStop() {
        LOG.log(Level.INFO, "Stopped {0} service", serviceName);
    }
    
    protected abstract void startService() throws InterruptedException;
    
    protected abstract void stopService() throws InterruptedException;
}
