/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Claves;

import com.google.gson.Gson;
import static com.sun.faces.util.CollectionsUtils.map;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import static java.time.Clock.system;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


/**
 * REST Web Service
 *
 * @author Sol
 */
@Path("claves")
public class ClavesResource {

    @Context
    private UriInfo context;
    private int[][] listaUsers = {{1,1000},{2,1000},{3,0},{4,2000},{5,2000}};
    //HashMap<Integer, String> mapHora = new HashMap<>();
    private HashMap<Integer, String[]> mapHora = new HashMap<>();    
    private int numDecimalesLat = 2;
    private int numDecimalesLon = 2;
    private boolean redondearLon = false;
    private boolean redondearLat = false;
    Random rnd = new Random();

    /**
     * Creates a new instance of ClavesResource
     */
    public ClavesResource() {
        String[] dep1000={"9:00","4"};
        String[] dep2000={"10:00","8"};
        mapHora.put(1000, dep1000);
        mapHora.put(2000, dep2000);
    }

    /**
     * Retrieves representation of an instance of Claves.ClavesResource
     * @return an instance of java.lang.String
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@FormParam("operador")String operador,@FormParam("idFile")int idFile, 
           @FormParam("idUser")int idUser,@FormParam("grupo")boolean grupo,@FormParam("lat")double lat, 
           @FormParam("lon")double lon, @FormParam("hora")String hora, @FormParam("fecha")String fecha,
           @FormParam("timeMask")String timeMask,@FormParam("dateMask")String dateMask){
        //TODO return proper representation object
        boolean userRegistrado=false;
        Fichero fichero = new Fichero(0,0,"hola","estoy aqui","a esta hora");
        
        Calendar ahora = Calendar.getInstance();
        int diaAhora = ahora.get(Calendar.DAY_OF_MONTH);
        int mesAhora = ahora.get(Calendar.MONTH)+1;
        int añoAhora = ahora.get(Calendar.YEAR);
        
        int diaFichero = Integer.parseInt(fecha.split("-")[0]);
        int mesFichero = Integer.parseInt(fecha.split("-")[1]);
        int añoFichero = Integer.parseInt(fecha.split("-")[2]);
        
        for (int i=0;i<listaUsers.length;i++){
            if(listaUsers[i][0]==idUser){
                userRegistrado = true;
                if(!grupo){
                    fichero = new Fichero(idFile,idUser,claveOperador(operador, idFile, idUser),claveGPS(lat,lon,idFile,idUser),claveHora(idFile, idUser,ahora,timeMask,hora));
                    break;
                }
                else{
                    if(listaUsers[i][1]==0){
                        fichero = new Fichero(idFile,idUser,claveOperador(getCadenaAlfanumAleatoria(10), idFile, idUser),claveGPS(rnd.nextDouble()*100,rnd.nextDouble()*100,idFile,idUser),hacerHash(getCadenaAlfanumAleatoria(100)));
                    }
                    else{
                        fichero = new Fichero(idFile,idUser,claveOperador(operador, idFile, listaUsers[i][1]),claveGPS(lat,lon,idFile,listaUsers[i][1]),claveHora(idFile, listaUsers[i][1],ahora,mapHora.get(listaUsers[i][1])[1],mapHora.get(listaUsers[i][1])[0]));
                    }
                    break;
                }
            }
        }
        if(!userRegistrado){
            fichero = new Fichero(idFile,idUser,claveOperador(getCadenaAlfanumAleatoria(10), idFile, idUser),claveGPS(rnd.nextDouble()*100,rnd.nextDouble()*100,idFile,idUser),hacerHash(getCadenaAlfanumAleatoria(100)));
        }
        
        Gson gson = new Gson();
        String ficheroJSON = gson.toJson(fichero);
        return ficheroJSON;
    }

    /**
     * PUT method for updating or creating an instance of ClavesResource
     * @param content representation for the resource
     
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }*/
    
    private String claveOperador(String operador, int idFile, int id){
        
        /*String hashOperador = "0x" + hacerHash(operador);
        String hashIdFile = "0x" + hacerHash(Integer.toString(idFile));
        String clave = Long.toHexString((Long.decode(hashOperador))^(Long.decode(hashIdFile)));*/
        return hacerHash(idFile+operador+id);
    }
    
    private String claveGPS(double lat, double lon, int idFile, int id){
        double latitud=lat;
        double longitud=lon;
        if(redondearLat){
            latitud= redondearDecimales(lat,numDecimalesLat);
        }else{
            for(int i=0;i<numDecimalesLat;i++){
                latitud=latitud*10;
            }
            latitud= (int)latitud;
        }
        if(redondearLon){
            longitud= redondearDecimales(lon,numDecimalesLon);
        }else{
            for(int i=0;i<numDecimalesLon;i++){
                longitud=longitud*10;
            }
            longitud= (int)longitud;
        }
        return hacerHash(latitud+""+idFile+""+(latitud*longitud)+""+id+""+longitud);
    }
    
    private String claveHora(int idFile, int id,Calendar ahora, String timeMask, String hora){
        
        int horaAhora = ahora.get(Calendar.HOUR_OF_DAY);
        int minutoAhora = ahora.get(Calendar.MINUTE);
        int horaInicio = Integer.parseInt(hora.split(":")[0]);
        int minutoInicio = Integer.parseInt(hora.split(":")[1]);
        
        int calculo = horaAhora-horaInicio+24;
        if(minutoAhora<minutoInicio){
            calculo=calculo-1;
        }
        
        String horaBinaria = Integer.toBinaryString((calculo)%24);
        String horasTrabajo = Integer.toBinaryString(Integer.parseInt(timeMask));

        if(horaBinaria.length()<5){
            while(horaBinaria.length()<5){
                horaBinaria = "0"+horaBinaria;
            }
        }
        
        if(horasTrabajo.length()<5){
            while(horasTrabajo.length()<5){
                horasTrabajo = "1"+horasTrabajo;
            }
        }
        
        String password = "";
        for(int i=0;i<5;i++){
            if((horaBinaria.substring(i,i+1)).equals(horasTrabajo.substring(i,i+1)) && (horaBinaria.substring(i,i+1)).equals("1")){
                password = password + "1";
            } else {
                password = password + "0";
            }
        }
        return hacerHash(idFile+horaBinaria+password+id+horasTrabajo);
    }
    
    private String hacerHash(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ClavesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
    }
    
    private String getCadenaAlfanumAleatoria(int longitud){
        String cadenaAleatoria = "";
        long milis = new java.util.GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while ( i < longitud){
            char c = (char)r.nextInt(255);
            if ( (c >= '0' && c <='9') || (c >='A' && c <='Z') ){
                cadenaAleatoria += c;
                i ++;
            }
        }
        return cadenaAleatoria;
    }
    
      public static double redondearDecimales(double valorInicial, int numeroDecimales) {
        double parteEntera, resultado;
        resultado = valorInicial;
        parteEntera = Math.floor(resultado);
        resultado=(resultado-parteEntera)*Math.pow(10, numeroDecimales);
        resultado=Math.round(resultado);
        resultado=(resultado/Math.pow(10, numeroDecimales))+parteEntera;
        return resultado;
    }
}
