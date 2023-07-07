/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;


import connexion.*;
import java.sql.*;
import connexion.utilisateur.*;
import java.util.Arrays;
import etu1799.framework.ModelView;
import etu1799.framework.annotation.*;
import generalbdd.BDDObject;
import generalbdd.annotation.*;

@Table(nom = "login")
public class Login {
    @PrimaryKey
    @Column(name = "id")
    int id;
    @Column(name = "username")
    String username;
    @Column(name = "password")
    String password;
    @Column(name = "profile")
    String profile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Login() {
    }

    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @RequestMapping(path = "/emp-start")
    public ModelView getLogin() {
        return new ModelView("login.jsp");
    }

    @RequestMapping(path = "/emp-log")
    public ModelView seConnecter() throws Exception {
        Login user = this.findUser(null);
        if (user == null) {
            throw new Exception("Utilisateur inexistant");
        } else {
            ModelView mv = new ModelView("home.jsp");
            mv.addItemSession("isConnected", true);
            mv.addItemSession("role", user.profile);
            return mv;
        }
    }

    public Login findUser(Connection con) throws Exception {
        boolean isNewConnexion = false;
        if (con == null) {
            isNewConnexion = true;
            Connect c = new Connect(User.nom, User.password, User.base);
            con = c.getConnectPostgre();
        }
        try {
            String where = "username='" + this.username + "' and password='" + this.password + "'";
            Object[] o = new BDDObject().find(this, where, con, null);
            return (Login) o[0];
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (isNewConnexion == true) {
                con.close();
            }
        }
    }
}
