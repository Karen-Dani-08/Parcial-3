package persistencia;

import modelo.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class LiquidacionDAOI implements LiquidacionDAO {

    @Override
    public void guardar(Liquidacion liquidacion)
            throws SQLException {

        Trabajador t = liquidacion.getTrabajador();

        String sql = "INSERT INTO liquidaciones "
                + "(documento, nombre, fecha_ingreso, tipo, "
                + "periodo, fecha_liquidacion, salario_base, "
                + "valor_contrato, horas_trabajadas, valor_hora, "
                + "pago, bono, total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, t.getDocumento());
            ps.setString(2, t.getNombre());
            ps.setString(3, t.getFechaIngreso().toString());
            ps.setString(4, t.getTipo());
            ps.setString(5, liquidacion.getPeriodo().toString());
            ps.setString(6,
                liquidacion.getFechaLiquidacion().toString());

            ps.setNull(7, Types.REAL);
            ps.setNull(8, Types.REAL);
            ps.setNull(9, Types.INTEGER);
            ps.setNull(10, Types.REAL);

            if (t instanceof EmpleadoPlanta) {

                EmpleadoPlanta empleado = (EmpleadoPlanta) t;

                ps.setDouble(7, empleado.getSalarioBase());

            } else if (t instanceof Contratista) {

                Contratista empleado = (Contratista) t;

                ps.setDouble(8, empleado.getValorContrato());

            } else if (t instanceof EmpleadoHoras) {

                EmpleadoHoras empleado = (EmpleadoHoras) t;

                ps.setInt(9, empleado.getHorasTrabajadas());
                ps.setDouble(10, empleado.getValorHora());
            }

            ps.setDouble(11, liquidacion.getPago());
            ps.setDouble(12, liquidacion.getBono());
            ps.setDouble(13, liquidacion.getTotal());

            ps.executeUpdate();
        }
    }

    @Override
    public ArrayList<Liquidacion> listar()
            throws SQLException {

        ArrayList<Liquidacion> lista = new ArrayList<>();

        String sql = "SELECT * FROM liquidaciones ORDER BY id";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                try {

                    String documento = rs.getString("documento");
                    String nombre = rs.getString("nombre");
                    String tipo = rs.getString("tipo");

                    LocalDate ingreso = LocalDate.parse(
                        rs.getString("fecha_ingreso"));

                    LocalDate fecha = LocalDate.parse(
                        rs.getString("fecha_liquidacion"));

                    Trabajador t;

                    if (tipo.equals("Planta")) {

                        t = new EmpleadoPlanta(
                            documento, nombre, ingreso,
                            rs.getDouble("salario_base"));

                    } else if (tipo.equals("Contratista")) {

                        t = new Contratista(
                            documento, nombre, ingreso,
                            rs.getDouble("valor_contrato"));

                    } else if (tipo.equals("Por horas")) {

                        t = new EmpleadoHoras(
                            documento, nombre, ingreso,
                            rs.getInt("horas_trabajadas"),
                            rs.getDouble("valor_hora"));

                    } else {

                        throw new SQLException(
                            "Tipo de trabajador desconocido");
                    }

                    Liquidacion l = new Liquidacion(t, fecha);

                    lista.add(l);

                } catch (Exception e) {

                    throw new SQLException(
                        "Error al recuperar liquidacion: "
                        + e.getMessage());
                }
            }
        }

        return lista;
    }

    @Override
    public double totalPorTipo(String periodo, String tipo)
            throws SQLException {

        double total = 0;

        String sql = "SELECT SUM(total) AS suma "
                   + "FROM liquidaciones "
                   + "WHERE periodo = ? AND tipo = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, periodo);
            ps.setString(2, tipo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    total = rs.getDouble("suma");
                }
            }
        }

        return total;
    }

    @Override
    public ArrayList<Trabajador> sinPago(String periodo)
            throws SQLException {

        ArrayList<Trabajador> lista = new ArrayList<>();

        TrabajadorDAO dao = new TrabajadorDAOI();

        String sql = "SELECT documento FROM trabajadores "
                   + "WHERE documento NOT IN "
                   + "(SELECT documento FROM liquidaciones "
                   + "WHERE periodo = ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, periodo);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    String documento = rs.getString("documento");

                    Trabajador t = dao.buscar(documento);

                    lista.add(t);
                }
            }
        }

        return lista;
    }
}
