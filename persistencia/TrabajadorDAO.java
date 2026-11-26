package persistencia;

import modelo.Trabajador;
import java.util.ArrayList;
import java.sql.SQLException;
import excepciones.DocumentoInvalidoException;

public interface TrabajadorDAO {

    void guardar(Trabajador trabajador)
        throws SQLException, DocumentoInvalidoException;

    void editar(Trabajador trabajador)
        throws SQLException;

    void eliminar(String documento)
        throws SQLException;

    ArrayList<Trabajador> listar()
        throws SQLException;

    Trabajador buscar(String documento)
        throws SQLException;
}