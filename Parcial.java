import modelo.*;
import persistencia.*;
import patron.*;
import java.time.LocalDate;

public class Parcial {

    public static void main(String[] args) {

        try {

            Conexion.crearTablas();

            TrabajadorDAO dao = new TrabajadorDAOI();

            LiquidacionDAO daoL = new LiquidacionDAOI();

            Liquidador liquidador = new Liquidador(daoL);

            ContabilidadObserver contabilidad =
                new ContabilidadObserver();

            AlertaObserver alerta = new AlertaObserver();

            liquidador.agregarObservador(contabilidad);
            liquidador.agregarObservador(alerta);

            Trabajador t = new Contratista(
                "80765432",
                "Carlos",
                LocalDate.of(2025, 1, 10),
                9500000
            );

            if (dao.buscar(t.getDocumento()) == null) {

                dao.guardar(t);
            }

            Liquidacion l = liquidador.liquidar(
                t, LocalDate.of(2026, 11, 30));

            System.out.println("Nombre: " + t.getNombre());
            System.out.println("Pago: " + l.getPago());
            System.out.println("Bono: " + l.getBono());
            System.out.println("Total: " + l.getTotal());

            System.out.println(alerta.getMensaje());

            System.out.println("-------------------");

            System.out.println("Total de la sesion: "
                + contabilidad.getTotalSesion());

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }
    }
}