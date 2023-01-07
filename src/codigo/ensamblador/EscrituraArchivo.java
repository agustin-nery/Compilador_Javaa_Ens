package codigo.ensamblador;

import java.io.*;
import java.util.Vector;
/**
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */
public class EscrituraArchivo {
    final String nombre_Archivo = "jasci.asm";
    
    public void crearArchivoASM(String contenido){
        File f = new File(nombre_Archivo);

        try{
            FileWriter w = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(w);
            PrintWriter wr = new PrintWriter(bw);	
            wr.write("; JASCI  ; \n");//escribimos en el archivo 
            wr.append(contenido); // CF
            wr.append("\n;   END JASCI ;"); //concatenamos en el archivo sin borrar lo existente
            wr.close();
            bw.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
