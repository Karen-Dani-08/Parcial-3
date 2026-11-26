package patron;

import modelo.Liquidacion;

public interface LiquidacionObserver {

    void actualizar(Liquidacion liquidacion);
}