/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connexion;

import java.sql.*;

public class Connect {
    String user;
    String password;
    String base;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        try {
            this.base = base;
        } catch (Exception e) {

        }
    }

    public Connect(String user, String password, String base) {
        this.setUser(user);
        this.setPassword(password);
        this.setBase(base);
    }

    public Connection getConnect(String base) {
        if (base.equalsIgnoreCase("postgres"))
            return getConnectPostgre();
        else
            return getConnectOracle();
    }

    public Connection getConnectOracle() {
        try {
            Connection connection;
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@localhost:1521:DBCOURS";
            connection = DriverManager.getConnection(url, this.user, this.password);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnectPostgre() {
        try {
            Connection connection;
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/" + this.base;
            connection = DriverManager.getConnection(url, this.user, this.password);
            connection.setAutoCommit(false);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
