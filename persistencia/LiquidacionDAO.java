package persistencia;

import modelo.Liquidacion;
import modelo.Trabajador;
import java.sql.SQLException;
import java.util.ArrayList;

public interface LiquidacionDAO {

    void guardar(Liquidacion liquidacion)
        throws SQLException;

    ArrayList<Liquidacion> listar()
        throws SQLException;

    double totalPorTipo(String periodo, String tipo)
        throws SQLException;

    ArrayList<Trabajador> sinPago(String periodo)
        throws SQLException;
}