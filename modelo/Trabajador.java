package modelo;

import java.time.LocalDate;
import excepciones.DocumentoInvalidoException;

public abstract class Trabajador {

    private String documento;
    private String nombre;
    private LocalDate fechaIngreso;

    public Trabajador(String documento, String nombre,
            LocalDate fechaIngreso)
            throws DocumentoInvalidoException {

        setDocumento(documento);
        setNombre(nombre);
        setFechaIngreso(fechaIngreso);
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento)
            throws DocumentoInvalidoException {

        if (documento == null || !documento.matches("[0-9]{6,10}")) {
            throw new DocumentoInvalidoException(
                "El documento debe tener entre 6 y 10 digitos");
        }

        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingrese el nombre del trabajador");
        }

        this.nombre = nombre;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("Ingrese una fecha");
        }

        this.fechaIngreso = fechaIngreso;
    }

    public abstract double calcularPago();

    public abstract String getTipo();

    public double calcularBono(LocalDate fecha) {
        return 0;
    }
}