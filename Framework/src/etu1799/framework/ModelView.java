/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1799.framework;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    String vue;
    HashMap<String, Object> data;
    HashMap<String, Object> session;
    boolean JSON;

    public String getVue() {
        return vue;
    }

    public void setVue(String vue) {
        this.vue = vue;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

    public boolean isJSON() {
        return JSON;
    }

    public void setJSON(boolean jSON) {
        JSON = jSON;
    }

    public ModelView(String vue) {
        this.vue = vue;
        this.data = new HashMap<>();
        this.session = new HashMap<>();
    }

    public ModelView(String vue, HashMap<String, Object> data) {
        this.vue = vue;
        this.data = data;
    }

    public void addItemData(String key, Object value) {
        this.data.put(key, value);
    }

    public void addItemSession(String key, Object value) {
        this.session.put(key, value);
    }
}
