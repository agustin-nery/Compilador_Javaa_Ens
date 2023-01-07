package utils;

/**
 *
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */
 
public class Simbolo {

    private String identificador;
    private String tipoDato;
    private int posicion;
    private String valor;

    public Simbolo(String identificador, String tipoDato, int posicion, String valor) {
        this.identificador = identificador;
        this.tipoDato = tipoDato;
        this.posicion = posicion;
        this.valor = valor;
    }

    public Simbolo() {
        this.identificador = "";
        this.tipoDato = "";
        this.posicion = 0;
        this.valor = "";
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
