package modelo;

import java.time.LocalDate;
import excepciones.DocumentoInvalidoException;
import excepciones.SalarioInvalidoException;

public class Contratista extends Trabajador {

    private double valorContrato;

    public Contratista(String documento, String nombre,
            LocalDate fechaIngreso, double valorContrato)
            throws DocumentoInvalidoException,
            SalarioInvalidoException {

        super(documento, nombre, fechaIngreso);
        setValorContrato(valorContrato);
    }

    public double getValorContrato() {
        return valorContrato;
    }

    public void setValorContrato(double valorContrato)
            throws SalarioInvalidoException {

        if (!(valorContrato >= 1400000) ||
                Double.isInfinite(valorContrato)) {
            throw new SalarioInvalidoException(
                "Por favor digite un contrato valido y con salario mayor a 1400000");
        }

        this.valorContrato = valorContrato;
    }

    @Override
    public double calcularPago() {

        double retencion = valorContrato * 0.11;

        return valorContrato - retencion;
    }

    @Override
    public String getTipo() {
        return "Contratista";
    }
}