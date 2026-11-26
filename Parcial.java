import modelo.*;
import java.time.LocalDate;

public class Parcial {

    public static void main(String[] args) {

        try {

            LocalDate fecha = LocalDate.of(2026, 11, 30);

            Trabajador t1 = new EmpleadoPlanta(
                "1012345678", "Juan",
                LocalDate.of(2019, 3, 15), 2700000);

            Trabajador t2 = new EmpleadoPlanta(
                "52123456", "Maria",
                LocalDate.of(2024, 12, 1), 2900000);

            Trabajador t3 = new Contratista(
                "80765432", "Carlos",
                LocalDate.of(2025, 1, 10), 9500000);

            Trabajador t4 = new EmpleadoHoras(
                "1033221100", "Ana",
                LocalDate.of(2014, 6, 30), 182, 22000);

            Trabajador[] trabajadores = {t1, t2, t3, t4};

            double totalNomina = 0;

            for (Trabajador t : trabajadores) {

                Liquidacion l = new Liquidacion(t, fecha);

                System.out.println("Nombre: " + t.getNombre());
                System.out.println("Tipo: " + t.getTipo());
                System.out.println("Pago: " + l.getPago());
                System.out.println("Bono: " + l.getBono());
                System.out.println("Total: " + l.getTotal());
                System.out.println("-------------------");

                totalNomina = totalNomina + l.getTotal();
            }

            System.out.println("Total nomina: " + totalNomina);

        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
    }
}