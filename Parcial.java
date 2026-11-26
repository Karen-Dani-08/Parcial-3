import persistencia.Conexion;
import vista.Visual;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Parcial {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                Conexion.crearTablas();

                Visual ventana =
                    new Visual();

                ventana.setVisible(true);

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}