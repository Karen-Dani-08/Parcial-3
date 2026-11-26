package modelo;

import java.time.LocalDate;
import java.time.Period;
import excepciones.DocumentoInvalidoException;
import excepciones.HorasInvalidasException;

public class EmpleadoHoras extends Trabajador
        implements Bonos {

    private int horasTrabajadas;
    private double valorHora;

    public EmpleadoHoras(String documento, String nombre,
            LocalDate fechaIngreso, int horasTrabajadas,
            double valorHora)
            throws DocumentoInvalidoException,
            HorasInvalidasException {

        super(documento, nombre, fechaIngreso);
        setHorasTrabajadas(horasTrabajadas);
        setValorHora(valorHora);
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(int horasTrabajadas)
            throws HorasInvalidasException {

        if (horasTrabajadas < 0 || horasTrabajadas > 300) {
            throw new HorasInvalidasException(
                "Por favor digite horas validas. Entre 0 a 300");
        }

        this.horasTrabajadas = horasTrabajadas;
    }

    public double getValorHora() {
        return valorHora;
    }

    public void setValorHora(double valorHora)
            throws HorasInvalidasException {

        if (!(valorHora > 0) || Double.isInfinite(valorHora)) {
            throw new HorasInvalidasException(
                "Por favor digite un valor de horas valido, mayor a cero");
        }

        this.valorHora = valorHora;
    }

    @Override
    public double calcularPago() {

        double pago;

        if (horasTrabajadas <= 160) {

            pago = horasTrabajadas * valorHora;

        } else {

            int horasExtras = horasTrabajadas - 160;

            double pagoNormal = 160 * valorHora;
            double pagoExtra = horasExtras * valorHora * 1.25;

            pago = pagoNormal + pagoExtra;
        }

        return pago;
    }

    @Override
    public double calcularBono(LocalDate fecha) {

        int anios = Period.between(
            getFechaIngreso(), fecha).getYears();

        if (anios > 10) {
            anios = 10;
        }

        if (anios < 0) {
            anios = 0;
        }

        return calcularPago() * anios / 100;
    }

    @Override
    public String getTipo() {
        return "Por horas";
    }
}