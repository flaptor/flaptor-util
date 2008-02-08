/*
Copyright 2008 Flaptor (flaptor.com) 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License.
*/
package com.flaptor.util;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;


/**
 * Database helper for log analysis
 * TODO: use a connection pool instead of creating a new connection each time.
 * 
 * @author Martin Massera
 */
public class Database {

	private static final Logger logger = Logger.getLogger(Execute.whoAmI());
	
    private static String driver;
    private static String dburl;
    private static String user;
    private static String pass;
    
    public Database(Config config) {
        driver = config.getString("database.driver");
        dburl = config.getString("database.url");
        user = config.getString("database.user");
        pass = config.getString("database.pass");
    }
    
    public Database(String configfile) {
    	this(Config.getConfig(configfile));
    }
    
    private Connection con = null;

    public void connect() throws Exception {
        Class.forName(driver).newInstance();
        con = DriverManager.getConnection(dburl, user, pass);
    }

    public void disconnect() {
        Execute.close(con);
    }
    
    public Connection connection() {
    	return con;
    }

}
