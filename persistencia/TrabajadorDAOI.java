package persistencia;

import modelo.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import excepciones.DocumentoInvalidoException;

public class TrabajadorDAOI implements TrabajadorDAO {

    @Override
    public void guardar(Trabajador trabajador)
            throws SQLException, DocumentoInvalidoException {

        if (buscar(trabajador.getDocumento()) != null) {
            throw new DocumentoInvalidoException(
                "El documento ya se encuentra registrado");
        }

        String sql = "INSERT INTO trabajadores VALUES "
                   + "(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trabajador.getDocumento());
            ps.setString(2, trabajador.getNombre());
            ps.setString(3, trabajador.getFechaIngreso().toString());
            ps.setString(4, trabajador.getTipo());

            ps.setNull(5, Types.REAL);
            ps.setNull(6, Types.REAL);
            ps.setNull(7, Types.INTEGER);
            ps.setNull(8, Types.REAL);

            if (trabajador instanceof EmpleadoPlanta) {

                EmpleadoPlanta empleado = (EmpleadoPlanta) trabajador;
                ps.setDouble(5, empleado.getSalarioBase());

            } else if (trabajador instanceof Contratista) {

                Contratista empleado = (Contratista) trabajador;
                ps.setDouble(6, empleado.getValorContrato());

            } else if (trabajador instanceof EmpleadoHoras) {

                EmpleadoHoras empleado = (EmpleadoHoras) trabajador;
                ps.setInt(7, empleado.getHorasTrabajadas());
                ps.setDouble(8, empleado.getValorHora());
            }

            ps.executeUpdate();
        }
    }

    @Override
    public void editar(Trabajador trabajador)
            throws SQLException {

        String sql = "UPDATE trabajadores SET nombre = ?, "
                   + "fecha_ingreso = ?, tipo = ?, "
                   + "salario_base = ?, valor_contrato = ?, "
                   + "horas_trabajadas = ?, valor_hora = ? "
                   + "WHERE documento = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trabajador.getNombre());
            ps.setString(2, trabajador.getFechaIngreso().toString());
            ps.setString(3, trabajador.getTipo());

            ps.setNull(4, Types.REAL);
            ps.setNull(5, Types.REAL);
            ps.setNull(6, Types.INTEGER);
            ps.setNull(7, Types.REAL);

            if (trabajador instanceof EmpleadoPlanta) {

                EmpleadoPlanta empleado = (EmpleadoPlanta) trabajador;
                ps.setDouble(4, empleado.getSalarioBase());

            } else if (trabajador instanceof Contratista) {

                Contratista empleado = (Contratista) trabajador;
                ps.setDouble(5, empleado.getValorContrato());

            } else if (trabajador instanceof EmpleadoHoras) {

                EmpleadoHoras empleado = (EmpleadoHoras) trabajador;
                ps.setInt(6, empleado.getHorasTrabajadas());
                ps.setDouble(7, empleado.getValorHora());
            }

            ps.setString(8, trabajador.getDocumento());

            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(String documento)
            throws SQLException {

        String sql = "DELETE FROM trabajadores WHERE documento = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);
            ps.executeUpdate();
        }
    }

    @Override
    public ArrayList<Trabajador> listar()
            throws SQLException {

        ArrayList<Trabajador> trabajadores = new ArrayList<>();

        String sql = "SELECT * FROM trabajadores";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Trabajador trabajador = crearTrabajador(rs);

                trabajadores.add(trabajador);
            }
        }

        return trabajadores;
    }

    @Override
    public Trabajador buscar(String documento)
            throws SQLException {

        String sql = "SELECT * FROM trabajadores WHERE documento = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return crearTrabajador(rs);
                }
            }
        }

        return null;
    }

    private Trabajador crearTrabajador(ResultSet rs)
            throws SQLException {

        String documento = rs.getString("documento");
        String nombre = rs.getString("nombre");
        String tipo = rs.getString("tipo");

        LocalDate fecha = LocalDate.parse(
            rs.getString("fecha_ingreso"));

        try {

            if (tipo.equals("Planta")) {

                return new EmpleadoPlanta(
                    documento, nombre, fecha,
                    rs.getDouble("salario_base"));

            } else if (tipo.equals("Contratista")) {

                return new Contratista(
                    documento, nombre, fecha,
                    rs.getDouble("valor_contrato"));

            } else if (tipo.equals("Por horas")) {

                return new EmpleadoHoras(
                    documento, nombre, fecha,
                    rs.getInt("horas_trabajadas"),
                    rs.getDouble("valor_hora"));
            }

        } catch (Exception e) {

            throw new SQLException(
                "Error al crear el trabajador: " + e.getMessage());
        }

        throw new SQLException("Tipo de trabajador desconocido");
    }
}