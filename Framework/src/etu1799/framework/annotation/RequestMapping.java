/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package etu1799.framework.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String path();
}
