/*
 * Copyright (C) 2016 Vladislav Kislyi <vladislav.kisliy@gmail.com>
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class ApplicationConfigImplTest {

    public ApplicationConfigImplTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of ALL methods, of class ApplicationConfigImpl.
     */
    @Test
    public void testAllServiceEnabled() {
        ApplicationConfigImpl instance = new ApplicationConfigImpl("true", "true", "true",
                "true", "true");
        assertTrue(instance.isPlainServiceEnabled());
        assertTrue(instance.isSSLServiceEnabled());
        assertTrue(instance.isSocks4ServiceEnabled());
        assertTrue(instance.isSocks5ServiceEnabled());
        assertTrue(instance.isMultiplierServiceEnabled());

        instance = new ApplicationConfigImpl("true1", "true", "true", "true",
                "true");
        assertFalse(instance.isPlainServiceEnabled());
        assertTrue(instance.isSSLServiceEnabled());
        assertTrue(instance.isSocks4ServiceEnabled());
        assertTrue(instance.isSocks5ServiceEnabled());
        assertTrue(instance.isMultiplierServiceEnabled());

        instance = new ApplicationConfigImpl("", "", null, null, null);
        assertFalse(instance.isPlainServiceEnabled());
        assertFalse(instance.isSSLServiceEnabled());
        assertFalse(instance.isSocks4ServiceEnabled());
        assertFalse(instance.isSocks5ServiceEnabled());
        assertFalse(instance.isMultiplierServiceEnabled());

        instance = new ApplicationConfigImpl("false", "false", "false", "false",
                "false");
        assertFalse(instance.isPlainServiceEnabled());
        assertFalse(instance.isSSLServiceEnabled());
        assertFalse(instance.isSocks4ServiceEnabled());
        assertFalse(instance.isSocks5ServiceEnabled());
        assertFalse(instance.isMultiplierServiceEnabled());
    }
}
