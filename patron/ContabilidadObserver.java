package patron;

import modelo.Liquidacion;

public class ContabilidadObserver
        implements LiquidacionObserver {

    private double totalSesion = 0;

    @Override
    public void actualizar(Liquidacion liquidacion) {

        totalSesion = totalSesion + liquidacion.getTotal();
    }

    public double getTotalSesion() {

        return totalSesion;
    }
}