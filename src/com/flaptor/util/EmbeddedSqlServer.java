package com.flaptor.util;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;

import org.hsqldb.Server;


/**
 * Embedded SQL Server provides a in-memory database for storing and querying relational data.
 * It is also capable of reading a table definition text file containing sql sentences such as
 * "create table", and execute them on the embedded server.
 * The database server is an hsql server.
 */
public class EmbeddedSqlServer {

    Server server = null;

    /**
     * Constructor, creates and starts the server.
     */
    public EmbeddedSqlServer() {
        server = new Server();
        server.setLogWriter(null);
        server.setErrWriter(null);
        server.setDatabaseName(0,"test");
        server.setDatabasePath(0,"mem:test;sql.enforce_strict_size=true");
        server.start();
    }

    /**
     * Constructor, creates and starts the server, and creates tables from a sql file.
     * @param tableDefinitions a text file containing sql statements creating sql objects (tables, indexes, etc).
     */
    public EmbeddedSqlServer(File tableDefinitions, String engine) throws Exception {
        this();
        HashSet<String> set = new HashSet<String>();
        set.add(engine);
        ConditionalInputStream input = new ConditionalInputStream(new FileInputStream(tableDefinitions), set);
        String[] tables = IOUtil.readAll(input).split(";");
        input.close();

        Connection con = getConnection();
        PreparedStatement prep;
        Statement stmt = con.createStatement();
        for (String sentence : tables) {
            if (sentence.replaceAll("\\n","").trim().matches("^(create|drop|alter|insert|update|delete) .*")) {
                stmt.execute(sentence);
            }
        }
        con.close();
    }

    /**
     * Stop the server.
     */
    public void stop() {
        server.stop();
    }

    /**
     * Get a sql connection to the server.
     */
    public Connection getConnection() throws Exception {
        DriverManager.registerDriver((Driver)Class.forName(getDriverSpec()).newInstance());
        return DriverManager.getConnection(getDBUrl(), getUser(), getPass());
    }
    
    /**
     * Get the driver string so other parts of the system can connect to this server.
     */
    public String getDriverSpec() {
        return "org.hsqldb.jdbcDriver";
    }

    /**
     * Get the database url so other parts of the system can connect to this server.
     */
    public String getDBUrl() {
        return "jdbc:hsqldb:hsql://localhost/test";
    }

    /**
     * Get the user name so other parts of the system can connect to this server.
     */
    public String getUser() {
        return "sa";
    }

    /**
     * Get the password so other parts of the system can connect to this server.
     */
    public String getPass() {
        return "";
    }

}
