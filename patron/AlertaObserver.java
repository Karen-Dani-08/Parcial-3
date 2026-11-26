package patron;

import modelo.Liquidacion;

public class AlertaObserver implements LiquidacionObserver {

    private String mensaje = "";

    @Override
    public void actualizar(Liquidacion liquidacion) {

        mensaje = "";

        if (liquidacion.getPago() > 8000000) {

            mensaje = "ALERTA: El pago de "
                + liquidacion.getTrabajador().getNombre()
                + " supera los $8.000.000";
        }
    }

    public String getMensaje() {

        return mensaje;
    }
}