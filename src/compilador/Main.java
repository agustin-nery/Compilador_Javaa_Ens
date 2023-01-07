package compilador;

import views.CompiladorFrame;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/**
 *
 * @author Rodrigo Garcia
 * @author Alvaro Evangelista
 * @author Mauricio Salamanca
 * @author Nery Segundo
 */
public class Main {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        new CompiladorFrame();
    }
}
