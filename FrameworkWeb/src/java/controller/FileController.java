/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import etu1799.framework.FileUpload;
import etu1799.framework.ModelView;
import etu1799.framework.annotation.RequestMapping;

public class FileController {
    FileUpload fichier;

    public FileUpload getFichier() {
        return fichier;
    }

    public void setFichier(FileUpload fichier) {
        this.fichier = fichier;
    }

    @RequestMapping(path = "/file-add")
    public ModelView getForm() {
        return new ModelView("file.jsp");
    }

    @RequestMapping(path = "/file-upload")
    public ModelView setFile() {
        // System.out.println(this.fichier.getName());
        return this.getForm();
    }
}
