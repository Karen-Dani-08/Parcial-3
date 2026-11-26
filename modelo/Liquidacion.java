package modelo;

import java.time.LocalDate;
import java.time.YearMonth;

public class Liquidacion {

    private Trabajador trabajador;
    private LocalDate fecha;
    private double pago;
    private double bono;
    private double total;

    public Liquidacion(Trabajador trabajador, LocalDate fecha) {

        if (fecha == null ||
                fecha.isBefore(trabajador.getFechaIngreso())) {
            throw new IllegalArgumentException(
                "Fecha de liquidacion invalida, por favor digite una valida");
        }

        this.trabajador = trabajador;
        this.fecha = fecha;

        this.pago = trabajador.calcularPago();
        this.bono = trabajador.calcularBono(fecha);
        this.total = pago + bono;
    }

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public LocalDate getFechaLiquidacion() {
        return fecha;
    }

    public YearMonth getPeriodo() {
        return YearMonth.from(fecha);
    }

    public double getPago() {
        return pago;
    }

    public double getBono() {
        return bono;
    }

    public double getTotal() {
        return total;
    }
}