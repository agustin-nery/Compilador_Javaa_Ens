package codigo.ensamblador;

import java.io.IOException;
import java.util.Vector;

/**
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */
public class Principal {
    
    static LecturaCF archivo = new LecturaCF();
    static Depurador depurador = new Depurador();
    static CompiladorAnalizador analizador = new CompiladorAnalizador();
    static EscrituraArchivo escritura = new EscrituraArchivo();
    static TraductorASM asm = new TraductorASM(); 
    static Logs msj = new Logs();
    
    
    public static void main(String[] args)  throws IOException {
        
        correr();
        
        
    }    
    
    private static void correr() throws IOException{
        boolean success = true; // compilacion exitosa
        String rutaABS = "C:\\Users\\Nery_Agustin\\Documents\\NetBeansProjects\\Compilador\\";
        String extIN = ".txt", extOut = ".sllm";
        String content = archivo.leerArchivo(rutaABS+"fuente"+extIN);
        //depurador.depurar(content);
        content = depurador.depurar(content);   
        System.out.println(content);
        System.out.println("---");
        System.out.println();
        /*
        System.out.print(archivo.leerArchivo(rutaABS+"fuente"+extIN));
        System.out.println("---");*/
        String renglon = "";
        int n_renglon = 1;
        for(int i = 0; i < content.length(); i++){
            while(content.charAt(i) != '\n'){
                renglon += content.charAt(i) ;
                i++;
            }
            //System.out.println("Renglon construido: "+renglon);
            if(!analizador.analizar(renglon)){
                System.out.print(CustomColors.RED+"in row "+n_renglon);
                System.out.println();
                success = false;
            }
            System.out.println();
            
            
            renglon = "";
            n_renglon++;
                
        }
        if(!analizador.isConditionsCorrects()){
            System.out.println(CustomColors.RED+" problema al cerrar ciclo o condicion");
            success = false;
        }
        
        System.out.println("---");
        System.out.println("");
        
        Vector <Simbolo> vector = analizador.getVars();
        System.out.println("Nombre | ID  | Tipo | Valor | Lectura");
        for(int i = 0; i < vector.size(); i++){
            
            Simbolo simbolo = vector.elementAt(i);
            simbolo.setId("id"+i);
            System.out.print(simbolo.getNombre()+"      " + simbolo.getId() +"   " + simbolo.getTipo() + "  " + simbolo.getValor() + "  "+simbolo.isLectura());
            System.out.println("");
            
        }
        
        
        if(success){
            escritura.crearArchivoASM(asm.convertirASM(content, vector)); //escribir archivo lenguaje ENSAMBLADOR
            System.out.println("");
            System.out.println(CustomColors.GREEN+msj.SUCCESS_BUILD);
            // el siguiente codigo comentado es para verificar que si se hayan almacenado correctamente las variables
            // descomentar cuando se requiera visualizar que id corresponde a que nombre de variable en codigo ASM
            /*String[][] vars =  asm.getDiccionarioASM();
            for(int i = 0; i < vars.length; i++){
                System.out.println(vars[i][0]);
                System.out.println("--");
            }*/
        }
        for(int i = 0; i < 10; i++)
            System.out.println("");
    }
}
