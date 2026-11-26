package modelo;

import java.time.LocalDate;
import java.time.Period;
import excepciones.DocumentoInvalidoException;
import excepciones.SalarioInvalidoException;

public class EmpleadoPlanta extends Trabajador
        implements Bonos {

    private double salarioBase;

    public EmpleadoPlanta(String documento, String nombre,
            LocalDate fechaIngreso, double salarioBase)
            throws DocumentoInvalidoException,
            SalarioInvalidoException {

        super(documento, nombre, fechaIngreso);
        setSalarioBase(salarioBase);
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase)
            throws SalarioInvalidoException {

        if (!(salarioBase >= 1400000) ||
                Double.isInfinite(salarioBase)) {
            throw new SalarioInvalidoException(
                "Por favor coloque un salario valido y mayor a 1400000");
        }

        this.salarioBase = salarioBase;
    }

    @Override
    public double calcularPago() {

        if (salarioBase <= 2800000) {
            return salarioBase + 200000;
        } else {
            return salarioBase;
        }
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
        return "Planta";
    }
}