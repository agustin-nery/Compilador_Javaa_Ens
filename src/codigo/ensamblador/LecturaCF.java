package codigo.ensamblador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */

public class LecturaCF {
    
    public String leerArchivo(String archivo) throws FileNotFoundException, IOException {
        String contenido = "";
        String cadena;
        FileReader f = new FileReader(archivo);
        BufferedReader b = new BufferedReader(f);
        while((cadena = b.readLine())!=null) {
            contenido += cadena;
            contenido += "\n";
        }
        b.close();
        return contenido;
    }
   
}
