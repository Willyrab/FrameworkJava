/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generalbdd;

import connexion.Connect;
import connexion.utilisateur.*;
import utilitaires.Util;
import generalbdd.annotation.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.print.DocFlavor.STRING;
import javax.swing.text.AbstractDocument;

import java.lang.reflect.*;

public class BDDObject {
    public void insert(Object o, Connection connexion, String BDD) throws Exception {
        boolean isNewConnexion = false;
        if (connexion == null) {
            isNewConnexion = true;
            Connect c = new Connect(User.nom, User.password, User.base);
            connexion = c.getConnect(BDD);
        }
        Statement state = connexion.createStatement();
        try {
            String values = this.preparedInsert(o, connexion);
            // System.out.println("INSERT INTO " +
            // o.getClass().getAnnotation(Table.class).nom() + "
            // VALUES " + values);
            state.executeUpdate("INSERT INTO " + o.getClass().getAnnotation(Table.class).nom() + " VALUES " + values);
            ResultSet resultat = state
                    .executeQuery("SELECT id FROM " + o.getClass().getAnnotation(Table.class).nom()
                            + " order by id desc limit 1");
            resultat.next();
            Field champ = o.getClass().getDeclaredField("id");
            champ.setAccessible(true);
            champ.set(o, resultat.getInt(1));
            // System.out.println(champ.getName() + " " + champ.get(this));
            if (isNewConnexion == true)
                connexion.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connexion.rollback();
        } finally {
            state.close();
            if (isNewConnexion == true)
                connexion.close();
        }
    }

    public void update(Object o, String[] attributs, String filtre, Connection connexion, String BDD) throws Exception {
        boolean isNewConnexion = false;
        if (connexion == null) {
            isNewConnexion = true;
            Connect c = new Connect(User.nom, User.password, User.base);
            connexion = c.getConnect(BDD);
        }
        if (filtre == null) {
            Field attribut = Util.listAllAttributes(this.getClass(), PrimaryKey.class).get(0);
            attribut.setAccessible(true);
            filtre = attribut.getName() + "='" + attribut.get(o) + "'";
        }
        Statement state = connexion.createStatement();
        try {
            String modif = this.preparedUpdate(o, attributs);
            System.out.println(
                    "UPDATE " + o.getClass().getAnnotation(Table.class).nom() + " SET " + modif + " where " + filtre);
            state.executeUpdate(
                    "UPDATE " + o.getClass().getAnnotation(Table.class).nom() + " SET " + modif + " where " + filtre);
            if (isNewConnexion == true)
                connexion.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connexion.rollback();
        } finally {
            state.close();
            if (isNewConnexion == true)
                connexion.close();
        }
    }

    public void delete(Object o, String filtre, Connection connexion, String BDD) throws Exception {
        boolean isNewConnexion = false;
        if (connexion == null) {
            isNewConnexion = true;
            Connect c = new Connect(User.nom, User.password, User.base);
            connexion = c.getConnect(BDD);
        }
        if (filtre == null) {
            Field attribut = Util.listAllAttributes(this.getClass(), PrimaryKey.class).get(0);
            attribut.setAccessible(true);
            filtre = attribut.getName() + "=" + attribut.get(this);
        }
        Statement state = connexion.createStatement();
        try {
            state.executeUpdate("DELETE FROM " + o.getClass().getAnnotation(Table.class).nom() + " where " + filtre);
            if (isNewConnexion == true)
                connexion.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connexion.rollback();
        } finally {
            state.close();
            if (isNewConnexion == true)
                connexion.close();
        }
    }

    public Object[] find(Object o, String filtre, Connection connexion, String BDD) throws Exception {
        boolean isNewConnexion = false;
        if (connexion == null) {
            isNewConnexion = true;
            Connect c = new Connect(User.nom, User.password, User.base);
            connexion = c.getConnect(BDD);
        }
        if (filtre == null) {
            Field attribut = Util.listAllAttributes(this.getClass(), PrimaryKey.class).get(0);
            attribut.setAccessible(true);
            filtre = attribut.getName() + "=" + attribut.get(o);
        }
        Statement state = connexion.createStatement();
        System.out.println(
                "Filtre : " + "SELECT * FROM " + o.getClass().getAnnotation(Table.class).nom() + " where " + filtre);
        ResultSet result = state
                .executeQuery("SELECT * FROM " + o.getClass().getAnnotation(Table.class).nom() + " where " + filtre);
        List<Object> objets = new ArrayList<Object>();
        objets = turnIntoObjects(o, result, objets);
        result.close();
        state.close();
        Object[] lo = new Object[objets.size()];
        if (isNewConnexion == true)
            connexion.close();
        return objets.toArray(lo);
    }

    public List<Object> turnIntoObjects(Object ob, ResultSet rs, List<Object> objets) throws Exception {
        ResultSetMetaData resultMeta = rs.getMetaData();
        Class c = ob.getClass();
        Object o = c.newInstance();
        Object[] args = new Object[1];
        while (rs.next()) {
            o = (Object) c.newInstance();
            for (int n = 0; n < resultMeta.getColumnCount(); n++) {
                Method setter = this.getSetter(ob, resultMeta.getColumnName(n + 1));
                args[0] = rs.getObject(n + 1);
                if (args[0] != null && this.getAttribut(ob, resultMeta.getColumnName(n + 1)) != null) {
                    setter.invoke(o, args);
                }
            }
            objets.add(o);
        }
        return objets;
    }

    public String preparedInsert(Object o, Connection connexion) {
        try {
            Statement state = connexion.createStatement();
            ResultSet result = state
                    .executeQuery("SELECT * FROM " + o.getClass().getAnnotation(Table.class).nom() + " LIMIT 1");
            ResultSetMetaData metadata = result.getMetaData();
            String values = "(";
            Object val = null;
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                Field champ = this.getAttribut(o, metadata.getColumnName(i + 1));
                champ.setAccessible(true);
                val = champ.get(o);
                this.putOnValue(values, val, champ);
            }
            System.out.println(values.substring(0, values.length() - 1).concat(")"));
            state.close();
            return values.substring(0, values.length() - 1).concat(")");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void putOnValue(String s, Object o, Field attribut) {
        if ((this.isNumber(o) || o == null) && attribut.getAnnotation(PrimaryKey.class) == null)
            s = s.concat(o + ",");
        else {
            if (attribut.getAnnotation(PrimaryKey.class) != null)
                s = s.concat("default,");
            else
                s = s.concat("'" + o + "',");
        }
    }

    public String preparedUpdate(Object o, String[] attributs) {
        try {
            String modif = "";
            for (int i = 0; i < attributs.length; i++) {
                System.out.println(attributs[i]);
                Field champ = o.getClass().getDeclaredField(attributs[i]);
                System.out.println(champ.getName());
                champ.setAccessible(true);
                Object val = champ.get(o);
                System.out.println(val);
                modif = modif.concat(champ.getName() + "='" + val + "',");
                System.out.println("Modif " + champ.getName() + "='" + val + "',");
            }
            return modif.substring(0, modif.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Field getAttribut(Object o, String nom) {
        Field[] attributs = o.getClass().getDeclaredFields();
        for (int i = 0; i < attributs.length; i++) {
            if (attributs[i].getAnnotation(Column.class).name().equalsIgnoreCase(nom)
                    || attributs[i].getName().equalsIgnoreCase(nom))
                return attributs[i];
        }
        return null;
    }

    public Method getSetter(Object o, String field) {
        Method[] fonctions = o.getClass().getDeclaredMethods();
        for (int i = 0; i < fonctions.length; i++) {
            if (isSetter(fonctions[i]) && fonctions[i].getName().equalsIgnoreCase("set" + field))
                return fonctions[i];
        }
        return null;
    }

    public boolean isNumber(Object o) {
        try {
            Double.parseDouble(o.toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isGetter(Method m) {
        boolean result = m.getName().startsWith("get")
                && (m.getParameterTypes().length == 0)
                && (!Void.class.equals(m.getReturnType()));
        return result;
    }

    public static boolean isSetter(Method m) {
        boolean result = m.getName().startsWith("set") &&
                (m.getParameterTypes().length == 1);
        return result;
    }

    public static Date stringToDate(String date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        if (date.indexOf('T') != -1) {
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        }
        Date d = sdf.parse(date);
        return d;
    }  
}
