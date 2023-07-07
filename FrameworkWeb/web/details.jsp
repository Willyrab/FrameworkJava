<%-- 
    Document   : details
    Created on : 7 juil. 2023, 01:56:36
    Author     : Daniella
--%>

<%@ page import="modele.Employe,java.util.*" %>
<%
Employe emp = (Employe) request.getAttribute("employe");
%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Details</h1>
        <p>Nom : <% out.println(emp.getNom()); %></p>
        <p>Date de naissance : <% out.println(emp.getDateNaissance()); %></p>
        <p>Salaire : <% out.println(emp.getSalaire()); %></p>
    </body>
</html>
