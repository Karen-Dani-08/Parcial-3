package persistencia;

import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class Conexion {

    public static Connection conectar() throws SQLException {

        Connection con = DriverManager.getConnection(
            "jdbc:sqlite:nomina.db");

        try (PreparedStatement ps = con.prepareStatement(
                "PRAGMA foreign_keys = ON")) {

            ps.execute();

        } catch (SQLException e) {
            con.close();
            throw e;
        }

        return con;
    }

    public static void crearTablas()
            throws SQLException, IOException {

        String archivo = Files.readString(
            Paths.get("database.sql"));

        String[] consultas = archivo.split(";");

        try (Connection con = conectar()) {

            for (String sql : consultas) {

                if (!sql.trim().isEmpty()) {

                    try (PreparedStatement ps =
                            con.prepareStatement(sql)) {

                        ps.execute();
                    }
                }
            }
        }
    }
}