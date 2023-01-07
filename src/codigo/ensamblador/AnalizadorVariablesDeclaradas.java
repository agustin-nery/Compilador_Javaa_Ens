package codigo.ensamblador;

import java.util.Vector;

/**
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */
public class AnalizadorVariablesDeclaradas {
    
    static Vector variables_globales = new Vector(), variables_type_globales = new Vector();
    
    
    // Busca entre todas las variables para determinar si ya esta declarada
    // retorna [0] estado, [1] tipo de dato
    public static String[] isRegisterVariable(String nom_var){
        String result[] = new String[]{"false", "it is not a data type"};
        for(int i = 0; i < variables_globales.size(); i++){
           if(variables_globales.elementAt(i).equals(nom_var)){
               result[0] = "verdadero";
               result[1]= ""+variables_type_globales.elementAt(i);
           }
        }
        return result;
    }
    
    // cambia de cadena a valor booleano
    public static boolean convertStringToBoolean(String val){
        return val.equals("verdadero");
    }
    
    // comprueba que el nombre de variable no este en uso
    // regresa verdadero si ya esta en uso, falso si esta disponible
    public static boolean isNameRegistered(String name){
        boolean find = false;
        for(int i = 0; i < variables_globales.size(); i++){
           if(variables_globales.elementAt(i).equals(name)){
               find = true;
               break;
           }
        }
        return find;
    }
    
    
    public static void registerNewVariable(String idVar,  String dataType){
        variables_globales.addElement(idVar);
        variables_type_globales.addElement(dataType);
    }
    
    public static void viewAllVarsDefined(){
        for(int i=0; i<variables_globales.size(); i++){
            System.out.println(variables_globales.elementAt(i) +"  ["+ variables_type_globales.elementAt(i)+"]");
        }
    }
}
