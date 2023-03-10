package codigo.ensamblador;

import static codigo.ensamblador.CompiladorAnalizador.simbolos;
import java.util.Vector;

/**
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */
public class TraductorASM { 
    
    Vector<Simbolo> tablaSimbolos;
    final String i_stack = "pila segment para stack 'stack'\n";
    final String f_stack = "pila ends\n";
    
    final String i_data = "datos segment para public 'data'\n";
    final String f_data = "datos ends\n";
    final String i_extra = "extra segment para public 'data'\n";
    final String f_extra = "extra ends\n";
    final String i_code = "assume cs:codigo, ds:datos, ss:pila, es:extra   ; segmentos\n" +
                        "public p0                                       ; funcion principal\n" +
                        "codigo segment para public 'code'\n" +
                        "  p0 proc far\n"+
                        "\n"+
                        "\n";
    final String obligatorias = "    push ds             ; obligatoria\n" +
                                "    mov ax,0            ; obligatoria\n" +
                                "    push ax             ; obligatoria\n" +
                                "    mov ax,datos        ; obligatoria\n" +
                                "    mov ds,ax           ; obligatoria\n" +
                                "    mov ax,extra        ; obligatoria\n" +
                                "    mov es,ax           ; obligatoria\n";
    
    final String libs = "    extern implec:far ; imprimir cadena leida de teclado\n" +
                        "    extern impnumde:far ; imprime numero decimal\n" +
                        "    extern slinea:far ; salto de linea\n" +
                        "    extern impcad:far  ; imprime cadena\n" +
                        "    extern leercad:far ; lee cadena desde teclado\n";
            
    final String f_code = " ret\n"+
                            "  p0 endp\n" +
                            "  codigo ends\n" +
                            "end p0\n";
    
    
    // code generate
    String code = "", varsTmps = "";
    int idAux = 0;
    int idcSalto = 0; // controla el id del salto para ciclos
    int idcCond = 0; // controla el id de los saltos para condicion
    
    public String clearScreen(){
        
        return "    ; Inicia limpiar pantalla\n"
                + " MOV AH, 6\n"
                + " MOV AL, 0\n"
                + " MOV BH, 7\n"
                + " MOV CH, 0\n"
                + " MOV CL, 0\n"
                + " MOV DH, 24\n"
                + " MOV DL, 79\n"
                + " INT 10H\n"
                + " ; Termina limpiar pantalla\n";
    }
    
    
    public String convertirASM (String c, Vector simbolos){
        Tools tools = new Tools(); 
        String content = "";
        tablaSimbolos = simbolos;
        String vars = "";
        for(int i = 0; i < tablaSimbolos.size(); i++){
           Simbolo simbolo = tablaSimbolos.elementAt(i);
           if(simbolo.isLectura()){
               vars += ""+simbolo.getId()+" DB 61, ?, 61 DUP( ? )  ; variable de lectura";
           }else{
               if(simbolo.getTipo().equals("entero")){
                    if(simbolo.getValor() != null)
                        vars += "    " +simbolo.getId()+" DB " +  simbolo.getValor(); //nombre de variable
                    else 
                        vars += "    " +simbolo.getId()+" DB 0"; //nombre de variable
                } else if(simbolo.getTipo().equals("cadena")){
                    if(simbolo.getValor() != null){
                        String var_tmp = tools.cleanVarString(simbolo.getValor());
                        String res = "";
                        int indice = var_tmp.length()-1;
                        while(var_tmp.charAt(indice) != '"'){
                            indice--;
                        }
                        for(int tm = 0; tm < indice; tm++){
                            res += ""+var_tmp.charAt(tm);
                        } 
                        //res+="$\"";
                        res+="\", 10, 13, 24H";
                        //txtVars += "    " +vars[i][1]+" DB " +  tools.cleanVarString(simbolo.getValor())+"$"; //nombre de variable   
                        vars += "    " +simbolo.getId()+" DB " +  res; //nombre de variable   
                        //txtVars +=res;
                    }
                    else 
                        vars += "    " +simbolo.getId()+" DB \"$\""; //nombre de variable
                } else if(simbolo.getTipo().equals("booleano")){
                    if(simbolo.getValor() != null)
                        if ( simbolo.getValor().equals("verdadero"))
                            vars += "    " +simbolo.getId()+" DB 1 " ; // VERDADERO
                        else 
                             vars += "    " +simbolo.getId()+" DB 0 " ; // FALSO    
                    else
                        vars += "    " +simbolo.getId()+" DB 0 " ; // FALSO    
                }
           }
           vars += "\n";
        }
        filterCode(c);
        content =   i_stack +
                    // agregar en pila
                    f_stack + 
                    i_data +
                    // agregar en seg de datos
                    vars + // variables de la tabla de simbolo
                    varsTmps + //variables para runtime
                    f_data +
                    i_extra +
                    // agregar en seg extr datos
                    f_extra +
                    i_code +
                    // codigo asm
                    obligatorias + //instrucciones obligatorias
                    clearScreen() + // limpia pantalla
                    code + // codigo filtrado
                    libs + // referencia a metodos
                    //end codigo asm
                    f_code
                ;
        return content;
    }
    
    public void filterCode(String content){
        boolean ciclo = false;
        for(int i = 0; i < content.length(); i++){
            String renglon = "";
            while(content.charAt(i) != '\n'){
                renglon += content.charAt(i) ;
                i++;
            }
            if(renglon.split(" ")[0].equals("crea") && !ciclo){
                
            } else if(renglon.split(" ")[0].equals("asigna")  && !ciclo){
                //new_content += ""+translateAsigna(renglon);
            } else if(renglon.split(" ")[0].equals("imprime")  && !ciclo){
                translateImpresion(renglon);
            } else if(renglon.split(" ")[0].equals("lea")  && !ciclo){
               translateLea(renglon);
            } else if(renglon.split(" ")[0].equals("ciclo")  && !ciclo){
                //new_content += ""+translateAsigna(renglon);
                translateCiclo(i, content, renglon);
                ciclo = true;
            } else if(renglon.split(" ")[0].equals("compara_si")  && !ciclo){
                //new_content += ""+translateAsigna(renglon);
                translateCompara(i, content, renglon);
                ciclo = true;
            } else{
                if(renglon.split(" ")[0].equals("fin_compara") || renglon.split(" ")[0].equals("fin_ciclo")){
                    //new_content += "";//salto en ensamblador
                     ciclo = false;
                }else{
                 //System.out.println(CustomColors.RED+"ERROR 500 ::"+renglon.split(" ")[0]+"::");   
                }
            }
        }
    }
    
    public void translateAsigna(String content){
    }
    
    public void translateCompara(int indice, String content, String condicion){
        
        
        Tools tools = new Tools();
        String partsCondicion[] = condicion.split(" ");
        //int val1 = 0, val2 = 0;
        
        if(tools.isCorrectNameVariable(partsCondicion[2]) && tools.isCorrectNameVariable(partsCondicion[4])){
            if(isDataReading(partsCondicion[2]) && isDataReading(partsCondicion[4])){
                String next_content = "";
                for(int i = indice; i < content.length(); i++){
                    next_content += content.charAt(i);
                }
                String instrucciones = "";
                String rowsNext[] = next_content.split("\n");


                for(int i = 0; i < rowsNext.length; i++){
                    String partsNext[] = rowsNext[i].split(" ");   
                    if(partsNext[0].equals("fin_compara") ){
                        //System.out.println("PARA!");
                        break;
                    }else{
                        instrucciones += rowsNext[i] +"\n" ;
                    }
                }
                code += ";;;;;;;;;;;;;;;;\n" +
                        "	lea bx,"+getIDVar(partsCondicion[2])+"\n" +
                        "	inc bx\n" +
                        "	mov cl,[bx]\n" +
                        "	mov CH,0\n" +
                        "	PUSH CX\n" +
                        "   \n" +
                        "	CDR"+idcCond+":\n" +
                        "		INC BX\n" +
                        "		SUB[BX],BYTE PTR 30H\n" +
                        "	LOOP CDR"+idcCond+"	\n" +
                        "\n" +
                        "	LEA BX,"+getIDVar(partsCondicion[2])+"+2;AQUI SE GUARDA EL 2\n" +
                        "	MOV AL,[BX]\n" +
                        "	MOV DI,0AH ;NO PUEDE SER DX\n" +
                        "	MOV AH,0; PARA QUITAR EL 09 DE AH, PUESTO EN LA FUNCION 9 DE LA INT 21 \n" +
                        "	POP CX\n" +
                        "	DEC CX\n" +
                        "\n" +
                        "	MYS"+idcCond+":\n" +
                        "		INC BX\n" +
                        "		MUL DI\n" +
                        "		MOV DL,[BX]\n" +
                        "		MOV DH,0\n" +
                        "		ADD AX,DX	\n" +
                        "	LOOP MYS"+idcCond+"\n" +
                        "	;; AX tiene el decimal leido\n" +
                        "	;;;;;;;;;;;;;;;;\n" +
                        "\n" +
                        "	MOV BX, AX ; en bx primer numero\n" +
                        "	PUSH BX\n" +
                        "	;;;;;;;;;;;;;;;;\n" +
                        "	lea bx,"+getIDVar(partsCondicion[4])+"\n" +
                        "	inc bx\n" +
                        "	mov cl,[bx]\n" +
                        "	mov CH,0\n" +
                        "	PUSH CX\n" +
                        "   \n" +
                        "	CDR"+(idcCond+1)+":\n" +
                        "		INC BX\n" +
                        "		SUB[BX],BYTE PTR 30H\n" +
                        "	LOOP CDR"+(idcCond+1)+"\n" +
                        "\n" +
                        "	LEA BX,"+getIDVar(partsCondicion[4])+"+2;AQUI SE GUARDA EL 2\n" +
                        "	MOV AL,[BX]\n" +
                        "	MOV DI,0AH ;NO PUEDE SER DX\n" +
                        "	MOV AH,0; PARA QUITAR EL 09 DE AH, PUESTO EN LA FUNCION 9 DE LA INT 21 \n" +
                        "	POP CX\n" +
                        "	DEC CX\n" +
                        "\n" +
                        "	MYS"+(idcCond+1)+":\n" +
                        "		INC BX\n" +
                        "		MUL DI\n" +
                        "		MOV DL,[BX]\n" +
                        "		MOV DH,0\n" +
                        "		ADD AX,DX	\n" +
                        "	LOOP MYS"+(idcCond+1)+"\n" +
                        "	;; AX tiene el decimal leido\n" +
                        "	;;;;;;;;;;;;;;;;\n" +
                        "\n" +
                        "	POP BX\n" +
                        "	;;; ejemplo de uso del numero ingresado como comparacion para un if ;;;\n" +
                        "	CMP BX,AX ;\n" ;
                        if(partsCondicion[3].equals("==")){
                            code += "JE EQL"+idcCond+"\n";
                            code += "JMP FIN"+idcCond+"\n";
                            code += "EQL"+idcCond+": \n" +
                                    "		; code ;\n";
                            filterCode(instrucciones);
                            code +="		JMP FIN"+idcCond+"\n";
                        }else if(partsCondicion[3].equals("<")){
                             code += "JL MENOR"+idcCond+"\n";
                            code += "JMP FIN"+idcCond+"\n";
                            code += "MENOR"+idcCond+": \n" +
                                    "		; code ;\n";
                            filterCode(instrucciones);
                            code +=        "		JMP FIN"+idcCond+"\n";
                        } else if(partsCondicion[3].equals(">")){
                            code += "JG MAYOR"+idcCond+"\n";
                            code += "JMP FIN"+idcCond+"\n";
                            code += "MAYOR"+idcCond+": \n" +
                                    "		; code ;\n";
                                   filterCode(instrucciones);
                            code +="		JMP FIN"+idcCond+"\n";
                        }
                        code +="FIN"+idcCond+":\n";
                idcCond+=3;
            }else if( (!isDataReading(partsCondicion[2]) && !isDataReading(partsCondicion[4]) )  ){
                int val1, val2;
                boolean cumple = false;
        
                // a < b
                // a = val1
                // b = val2

                if(tools.isCorrectNameVariable(partsCondicion[2])){
                    // es variable, traer el valor con el metodo getValVar
                    if(!getValVar(partsCondicion[2]).equals( "null")){
                     val1 = Integer.parseInt(getValVar(partsCondicion[2]).replace(" ",""));   
                    }else {
                        val1 = 0;
                    }
                } else {
                    // es valor
                    val1 = Integer.parseInt(partsCondicion[2]);
                }
                if(tools.isCorrectNameVariable(partsCondicion[4])){
                    // es variable, traer el valor con el metodo getValVar
                    if(!getValVar(partsCondicion[4]).equals( "null")){
                     val2 = Integer.parseInt(getValVar(partsCondicion[4]).replace(" ",""));   
                    }else {
                        val2 = 0;
                    }
                } else {
                    // es valor
                    val2 = Integer.parseInt(partsCondicion[4]);
                }

                // calcular iteraciones con base al operador relacional
                switch(partsCondicion[3]){
                    case ">":
                        if(val1 > val2 )
                            cumple = true;
                        break;
                    case "<":
                        if(val1 < val2 )
                            cumple = true;
                        break;
                    case "==":
                        if(val1 == val2 )
                            cumple = true;
                        break;
                }
                if(cumple){
                    //System.out.println("PASA ");
                    String next_content = "";
                    for(int i = indice; i < content.length(); i++){
                        next_content += content.charAt(i);
                    }
                    String instrucciones = "";
                    String rowsNext[] = next_content.split("\n");


                    for(int i = 0; i < rowsNext.length; i++){
                        String partsNext[] = rowsNext[i].split(" ");   
                        if(partsNext[0].equals("fin_ciclo") ){
                            //System.out.println("PARA!");
                            break;
                        }else{
                            instrucciones += rowsNext[i] +"\n" ;
                        }
                    }
                    System.out.println(instrucciones);
                    // en 'instrucciones' se quedan guardadas las instrucciones dentro de esta condici??n
                    filterCode(instrucciones);
                }
            }else{
                //caso especial, variable de lectura con variable definida
                System.out.println("variable de lectura con variable definida");
            }
        } else {
            // caso especial #2 variable de lectura con numero
            System.out.println("variable de lectura con numero");
        }
        
    }
    
    public void translateCiclo(int indice, String content, String condicion){
        Tools tools = new Tools(); 
        //System.out.println(condicion);
        String partsCondicion[] = condicion.split(" ");
        
        //System.out.println(partsCondicion[2]); // iteracion
        //System.out.println(partsCondicion[3]); // operador relacional
        //System.out.println(partsCondicion[4]); // limite
        
        int val1 = 0, val2 = 0, iteraciones = 0;
        boolean withRead = false;
        
        // a < b
        // a = val1
        // b = val2
        
        if(tools.isCorrectNameVariable(partsCondicion[2])){
            // es variable, traer el valor con el metodo getValVar
            if(isDataReading(partsCondicion[2])){
                //code += getIDVar(partsCondicion[2]) + " ES LECTURA!";
                withRead = true;
                code += ";;;;;;;;;;;;;;;;\n" +
                        "	lea bx," +getIDVar(partsCondicion[2])+"\n" +
                        "	inc bx\n" +
                        "	mov cl,[bx]\n" +
                        "	mov CH,0\n" +
                        "	PUSH CX\n" +
                        "   \n" +
                        "	CDR:\n" +
                        "		INC BX\n" +
                        "		SUB[BX],BYTE PTR 30H\n" +
                        "	LOOP CDR	\n" +
                        "\n" +
                        "	LEA BX,"+getIDVar(partsCondicion[2])+"+2;AQUI SE GUARDA EL 2\n" +
                        "	MOV AL,[BX]\n" +
                        "	MOV DI,0AH ;NO PUEDE SER DX\n" +
                        "	MOV AH,0; PARA QUITAR EL 09 DE AH, PUESTO EN LA FUNCION 9 DE LA INT 21 \n" +
                        "	POP CX\n" +
                        "	DEC CX\n" +
                        "\n" +
                        "	MYS:\n" +
                        "		INC BX\n" +
                        "		MUL DI\n" +
                        "		MOV DL,[BX]\n" +
                        "		MOV DH,0\n" +
                        "		ADD AX,DX	\n" +
                        "	LOOP MYS\n" +
                        "	;; AX tiene el decimal leido\n" +
                        "	;;;;;;;;;;;;;;;;";
            }else{
                if(!getValVar(partsCondicion[2]).equals( "null")){
                    val1 = Integer.parseInt(getValVar(partsCondicion[2]).replace(" ",""));   
                }else {
                   val1 = 0;
                }
            }
            
        } else {
            // es valor
            val1 = Integer.parseInt(partsCondicion[2]);
        }
        if(partsCondicion.length >= 5){
            if(tools.isCorrectNameVariable(partsCondicion[4])){
                // es variable, traer el valor con el metodo getValVar
                if(isDataReading(partsCondicion[2])){
                    //code += getIDVar(partsCondicion[2]) + " ES LECTURA!";
                    withRead = true;
                    code += ";;;;;;;;;;;;;;;;\n" +
                            "	lea bx," +getIDVar(partsCondicion[2])+"\n" +
                            "	inc bx\n" +
                            "	mov cl,[bx]\n" +
                            "	mov CH,0\n" +
                            "	PUSH CX\n" +
                            "   \n" +
                            "	CDR:\n" +
                            "		INC BX\n" +
                            "		SUB[BX],BYTE PTR 30H\n" +
                            "	LOOP CDR	\n" +
                            "\n" +
                            "	LEA BX,"+getIDVar(partsCondicion[2])+"+2;AQUI SE GUARDA EL 2\n" +
                            "	MOV AL,[BX]\n" +
                            "	MOV DI,0AH ;NO PUEDE SER DX\n" +
                            "	MOV AH,0; PARA QUITAR EL 09 DE AH, PUESTO EN LA FUNCION 9 DE LA INT 21 \n" +
                            "	POP CX\n" +
                            "	DEC CX\n" +
                            "\n" +
                            "	MYS:\n" +
                            "		INC BX\n" +
                            "		MUL DI\n" +
                            "		MOV DL,[BX]\n" +
                            "		MOV DH,0\n" +
                            "		ADD AX,DX	\n" +
                            "	LOOP MYS\n" +
                            "	;; AX tiene el decimal leido\n" +
                            "	;;;;;;;;;;;;;;;;";
                }else{
                    if(!getValVar(partsCondicion[4]).equals( "null")){
                        val2 = Integer.parseInt(getValVar(partsCondicion[4]).replace(" ",""));   
                    } else {
                       val2 = 0;
                    }
                }

            } else {
                // es valor
                val2 = Integer.parseInt(partsCondicion[4]);
            }

            // calcular iteraciones con base al operador relacional
            switch(partsCondicion[3]){
                case ">":
                    iteraciones = val1 - val2;
                    break;
                case "<":
                    iteraciones = val2 - val1;
                    //add case ==
                    break;
            }


            // calcular diferencia entre valor 1 y valor 2, si el resultado es positivo entonces continuar con el codigo
            if(iteraciones > 0){
                String next_content = "";
                for(int i = indice; i < content.length(); i++){
                    next_content += content.charAt(i);
                }
                String instrucciones = "";
                String rowsNext[] = next_content.split("\n");


                for(int i = 0; i < rowsNext.length; i++){
                    String partsNext[] = rowsNext[i].split(" ");   
                    if(partsNext[0].equals("fin_ciclo") ){
                        //System.out.println("PARA!");
                        break;
                    }else{
                        instrucciones += rowsNext[i] +"\n" ;
                    }
                }
                if(withRead){

                }else{
                    code += " ; inicia ciclo \n"+
                        " MOV CX,"+iteraciones+" \n"+
                        " C"+idcSalto+": \n";
                    filterCode(instrucciones);
                    code += " loop C"+idcSalto+" \n"+
                            " ; fin ciclo \n";
                }
                idcSalto++;
                /*endhere*/
            }else{
                // significa que la condicion no se cumple, por tanto no es necesario hacer el ciclo
                ///System.out.println(CustomColors.BLUE+" NOpasa " );
            }
        }else{
            code += " ;;;;;;;   EL CICLO REQUIERE QUE EN CX MANDE AX   ;;;;\n";
            String next_content = "";
            for(int i = indice; i < content.length(); i++){
                next_content += content.charAt(i);
            }
            String instrucciones = "";
            String rowsNext[] = next_content.split("\n");


            for(int i = 0; i < rowsNext.length; i++){
                String partsNext[] = rowsNext[i].split(" ");   
                if(partsNext[0].equals("fin_ciclo") ){
                    //System.out.println("PARA!");
                    break;
                }else{
                    instrucciones += rowsNext[i] +"\n" ;
                }
            }
            code += " ; inicia ciclo \n"+
                        " MOV CX,AX \n"+
                        " C"+idcSalto+": \n";
                    filterCode(instrucciones);
                    code += " loop C"+idcSalto+" \n"+
                            " ; fin ciclo \n";
            
        }
        // sino, no 
        
        //System.out.println(CustomColors.PURPLE+instrucciones);
        //System.out.println(indice+" "+content.charAt(indice+2)+" "+content.charAt(indice+1));
    }
    
    public void translateImpresion(String content){
        String new_content = "";
        //System.out.println(CustomColors.YELLOW+content.split(" ")[1].toString());
        if(isDataReading(content.split(" ")[1])){
            new_content += " ; inicia impresion de cadena leida desde teclado \n" +
                    "   lea bx, "+getIDVar(content.split(" ")[1]) +" + 1 \n"+
                    "   call implec\n"+
                    "   call slinea\n"
                    ;
        }else{
            if(getIDVar(content.split(" ")[1]).equals("")){
                //System.out.println(CustomColors.YELLOW+ getIDVar( content.split(" ")[1]));
                Tools herramient = new Tools();
                //System.out.println(CustomColors.YELLOW+ );
                varsTmps += "tmp"+idAux + " DB " + herramient.cleanVarString(content.split(" ")[1])  +" , 10, 13, 24H\n";
                Simbolo simbolo = new Simbolo(); 
                simbolo.setNombre("tmp"+idAux);
                simbolo.setId("tmp"+idAux);
                simbolo.setLectura(false);
                //simbolo.setValor(herramient.retireQuotes( herramient.cleanVarString(content.split(" ")[1])));
                simbolo.setValor( herramient.cleanVarString(content.split(" ")[1]) );
                simbolo.setTipo("cadena");
                simbolos.add((simbolo));
                tablaSimbolos.add(simbolo);
                new_content = "; Impresion de cadena\n" +
                    "LEA DX, tmp"+ idAux + "\n"+
                    "MOV AH,09\n" +
                    "INT 21H";
                idAux++;
            }else{
                new_content = "; Impresion de cadena\n" +
                    "LEA DX, "+ getIDVar(content.split(" ")[1]) + "\n"+
                    "MOV AH,09\n" +
                    "INT 21H";
            }
        }
        
        code += new_content+"\n";
    }
    
    public void translateLea(String content){
        String new_content = "";
        new_content = "; inicia lectura de cadena\n"+
                    "       lea  bx, " + getIDVar(content.split(" ")[1])+"\n" +
                    "	push 	bx \n" +
                    "	call	leercad \n" +
                    "	add 	sp, 2\n" +
                    "       call	slinea\n"+
                    "       ; fin lectura";
        code += new_content+"\n";
    }
    
    
    public String getIDVar(String name){
        // recorrer vector de todas las variables
        String id ="";
        for(int i = 0; i < tablaSimbolos.size(); i++){
            Simbolo simbolo = tablaSimbolos.elementAt(i);
            if(simbolo.getNombre().equals(name)){
                id =  ""+simbolo.getId();
                break;
            }
        }
        //System.out.println(CustomColors.YELLOW+id);
        return id; // retornar id de la variable declarada en la tabla de simbolos
    }
    
    public boolean isDataReading(String name){
        // recorrer vector de todas las variables
        boolean read = false;
        for(int i = 0; i < tablaSimbolos.size(); i++){
            Simbolo simbolo = tablaSimbolos.elementAt(i);
            if(simbolo.getNombre().equals(name)){
                read = simbolo.isLectura();
                break;
            }
        }
        //System.out.println(CustomColors.YELLOW+id);
        return read; // retornar id de la variable declarada en la tabla de simbolos
        
    }
    
    public String getTypeVar(String name){
        // recorrer vector de todas las variables
        String type ="";
        for(int i = 0; i < tablaSimbolos.size(); i++){
            Simbolo simbolo = tablaSimbolos.elementAt(i);
            if(simbolo.getNombre().equals(name)){
                type =  ""+simbolo.getTipo();
                break;
            }
        }
        //System.out.println(CustomColors.YELLOW+id);
        return type; // retornar id de la variable declarada en la tabla de simbolos
        
    }
    
    public String getValVar(String name){
        // recorrer vector de todas las variables
        String type ="";
        for(int i = 0; i < tablaSimbolos.size(); i++){
            Simbolo simbolo = tablaSimbolos.elementAt(i);
            if(simbolo.getNombre().equals(name)){
                type =  ""+simbolo.getValor();
                break;
            }
        }
        //System.out.println(CustomColors.YELLOW+id);
        return type; // retornar id de la variable declarada en la tabla de simbolos
        
    }
     
}
