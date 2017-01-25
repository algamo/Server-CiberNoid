/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Claves;

import javax.xml.bind.annotation.*;

/**
 *
 * @author rcanales
 */

public class Fichero {

    public int idFile;
    public int idUser;
    public String claveOperador;
    public String claveGPS;
    public String claveHora;

    public int getIdFile() {
        return idFile;
    }

    public void setIdfile(int id) {
        idFile = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int id) {
        idUser = id;
    }

    public String getClaveOperador() {
        return claveOperador;
    }

    public void setClaveOperador(String operador) {
        claveOperador = operador;
    }
    
    public String getClaveGPS() {
        return claveGPS;
    }

    public void setClaveGPS(String gps) {
        claveGPS = gps;
    }
    
    public String getClaveHora() {
        return claveHora;
    }

    public void setClaveHora(String hora) {
        claveHora = hora;
    }
    
    public Fichero(int id, int user, String operador, String gps, String hora){
        idFile = id;
        idUser = user;
        claveOperador = operador;
        claveGPS = gps;
        claveHora = hora;
    }
            
   /*@Override
    public String toString() {
        return new StringBuffer(" file: ").
                append(idFile).
                append(" user: ").
                append(idUser).
                append(" operador ").
                append(claveOperador).toString();
    }*/
}
