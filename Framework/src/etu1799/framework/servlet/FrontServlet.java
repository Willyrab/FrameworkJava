/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1799.framework.servlet;


import etu1799.framework.FileUpload;
import etu1799.framework.Mapping;
import etu1799.framework.ModelView;
import etu1799.framework.annotation.Authentification;
import etu1799.framework.annotation.ResponseBody;
import etu1799.framework.annotation.SessionAttribute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;
import utilitaires.Util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


@MultipartConfig
public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> MappingUrls = new HashMap<>();
    HashMap<String, Object> singleton = new HashMap<>();

    public HashMap<String, Object> getSingleton() {
        return singleton;
    }

    public void setSingleton(HashMap<String, Object> singleton) {
        this.singleton = singleton;
    }

    public HashMap<String, Mapping> getMappingUrls() {
        return MappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> MappingUrls) {
        this.MappingUrls = MappingUrls;
    }

    @Override
    public void init() {
        try {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            path = path.replaceAll("%20", " ");
            Util.setFieldsFrontServlet(this.getMappingUrls(), this.getSingleton(), path,
                    Util.getAllPackages(null, path.substring(1, path.length()), null));
            System.out.println("HashMap URL");
            for (Map.Entry<String, Mapping> entry : this.getMappingUrls().entrySet()) {
                System.out.println(entry.getKey() + "|" + entry.getValue().getMethod());
            }
            System.out.println("HashMap Singleton");
            for (Map.Entry<String, Object> entry : this.getSingleton().entrySet()) {
                System.out.println(entry.getKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sessionFromMVToHttp(Object mv, HttpSession session) {
        if (mv instanceof ModelView) {
            if (((ModelView) mv).getSession() == null || ((ModelView) mv).getSession().isEmpty()) {
                return;
            }
            for (Map.Entry<String, Object> entry : ((ModelView) mv).getSession().entrySet()) {
                if (entry.getKey() == "profile") {
                    session.setAttribute(this.getInitParameter(entry.getKey()),
                            ((ModelView) mv).getSession().get(this.getInitParameter(entry.getKey())));
                } else {
                    session.setAttribute(entry.getKey(), ((ModelView) mv).getSession().get(entry.getKey()));
                }
            }
        }
    }

    public byte[] partToByte(Part part) throws Exception {
        InputStream is = part.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int byteReader;
        while ((byteReader = is.read(buffer)) != -1) {
            baos.write(buffer, 0, byteReader);
        }
        byte[] byteArray = baos.toByteArray();
        baos.close();
        is.close();
        return byteArray;
    }

    public void manageFileUpload(Object o, Field attribut, Part part) throws Exception {
        attribut.setAccessible(true);
        System.out.println(part.getSubmittedFileName());
        if (part.getSize() > 0) {
            byte[] b = this.partToByte(part);
            attribut.set(o, new FileUpload(part.getSubmittedFileName(), b));
        }
    }

    public void setAttributeRequest(HttpServletRequest request, Object mv) {
        if (mv instanceof ModelView) {
            if (((ModelView) mv).getData() == null || ((ModelView) mv).getData().size() == 0) {
                return;
            }
            for (Map.Entry<String, Object> entry : ((ModelView) mv).getData().entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }
    }

    public Mapping getMapping(String url) throws Exception {
        if (this.MappingUrls.containsKey("/" + url)) {
            return this.MappingUrls.get("/" + url);
        }
        throw new Exception("Cette URL n' est associee a aucune classe");
    }

    public void fillAttributeOfObject(Object o, HttpServletRequest request) throws Exception {
        Field[] attributs = o.getClass().getDeclaredFields();
        for (Field field : attributs) {
            field.setAccessible(true);
            if (field.getType() == FileUpload.class) {
                try {
                    this.manageFileUpload(o, field, request.getPart(field.getName()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String value = request.getParameter(field.getName());
                if (value != null) {
                    field.set(o, Util.castString(value, field.getType()));
                }
            }
        }
    }

    public Object instanceObject(Mapping map) throws Exception {
        if (this.singleton.containsKey(map.getClassName())) {
            if (this.singleton.get(map.getClassName()) != null) {
                Util.resetObject(this.singleton.get(map.getClassName()));
                return this.singleton.get(map.getClassName());
            } else {
                this.singleton.replace(map.getClassName(), Class.forName(map.getClassName()).newInstance());
                return this.singleton.get(map.getClassName());
            }
        }
        Class<?> c = Class.forName(map.getClassName());
        Object o = c.newInstance();
        return o;
    }

    public Object callModelAndFunction(Object o, Method fonction, Object[] params, Mapping map) throws Exception {
        System.out.println("Fonction " + fonction.getName());
        Object resultat = fonction.invoke(o, params);
        if (resultat instanceof ModelView) {
            System.out.println("Vue " + ((ModelView) resultat).getVue());
        }
        return resultat;
    }

    public void sendResponse(HttpServletRequest request, HttpServletResponse response, Object mv,
            boolean isResponseBody)
            throws ServletException, IOException, Exception {
        if (mv instanceof ModelView) {
            if (((ModelView) mv).isJSON() == true) {
                response.setContentType("application/json");
                String json = Util.convertObjectToJSON(((ModelView) mv).getData());
                PrintWriter out = response.getWriter();
                out.println(json);
                out.flush();
            } else {
                this.dispatch(request, response, ((ModelView) mv).getVue());
            }
        } else if (isResponseBody == true) {
            System.out.println("Response Body");
            response.setContentType("application/json");
            String json = Util.convertObjectToJSON(mv);
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        }
    }

    public Object[] fillArgumentsOfFonction(Method fonction, HttpServletRequest request) throws Exception {
        Parameter[] arguments = fonction.getParameters();
        Object[] params = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            String value = null;
            if (arguments[i].getAnnotation(SessionAttribute.class) != null) {
                value = request.getSession().getAttribute(arguments[i].getAnnotation(SessionAttribute.class).value())
                        .toString();
            } else {
                value = request.getParameter(arguments[i].getName());
            }
            System.out.println(arguments[i].getName());
            if (value != null) {
                params[i] = Util.castString(value, arguments[i].getType());
            }
        }
        return params;
    }

    public void authentificate(HttpServletRequest req, Method fonction) throws Exception {
        if (fonction.getAnnotation(Authentification.class) != null) {
            System.out.println(fonction.getAnnotation(Authentification.class)
                    .profile());
            System.out.println(req.getSession().getAttribute(this.getInitParameter("profile")));
            if (req.getSession().getAttribute(this.getInitParameter("profile")) == null || fonction
                    .getAnnotation(Authentification.class)
                    .profile()
                    .equalsIgnoreCase(
                            req.getSession().getAttribute(this.getInitParameter("profile")).toString()) == false) {
                throw new Exception("Acces interdit");
            }
        }
    }

    public boolean isResponseBody(Method fonction) {
        return fonction.getAnnotation(ResponseBody.class) != null;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] data = Util.retrieveDataFromURL(request.getRequestURI());
        System.out.println("URL du Client");
        for (int i = 0; i < data.length; i++) {
            String string = data[i];
            System.out.println(string);
        }
        try {
            if (data[data.length - 1].equalsIgnoreCase("")) {
                this.dispatch(request, response, "index.html");
                return;
            }
            Mapping map = getMapping(data[data.length - 1]);
            Object o = this.instanceObject(map);
            Method fonction = o.getClass().getDeclaredMethod(map.getMethod(), map.getParamsType());
            this.authentificate(request, fonction);
            this.fillAttributeOfObject(o, request);
            Object[] params = this.fillArgumentsOfFonction(fonction, request);
            Object reponse = callModelAndFunction(o, fonction, params, map);
            this.sessionFromMVToHttp(reponse, request.getSession());
            this.setAttributeRequest(request, reponse);
            if (reponse instanceof ModelView) {
                System.out.println(((ModelView) reponse).getVue());
            }
            this.sendResponse(request, response, reponse, this.isResponseBody(fonction));
            return;
        } catch (Exception e) {
            e.printStackTrace();
            ModelView error = new ModelView("error.jsp");
            error.addItemData("message", e.getMessage());
            this.setAttributeRequest(request, error);
            this.dispatch(request, response, error.getVue());
            return;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    public void dispatch(HttpServletRequest request, HttpServletResponse response, String vue)
            throws ServletException, IOException {
        RequestDispatcher dispa = request.getRequestDispatcher("/WEB-INF/vues/" + vue);
        dispa.forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
