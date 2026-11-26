package patron;

import modelo.Trabajador;
import modelo.Liquidacion;
import persistencia.LiquidacionDAO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.SQLException;

public class Liquidador {

    private ArrayList<LiquidacionObserver> observadores;
    private LiquidacionDAO dao;

    public Liquidador(LiquidacionDAO dao) {

        this.dao = dao;

        observadores = new ArrayList<>();
    }

    public void agregarObservador(LiquidacionObserver observador) {

        if (!observadores.contains(observador)) {

            observadores.add(observador);
        }
    }

    public void eliminarObservador(LiquidacionObserver observador) {

        observadores.remove(observador);
    }

    public void notificarObservadores(Liquidacion liquidacion) {

        for (LiquidacionObserver observador : observadores) {

            observador.actualizar(liquidacion);
        }
    }

    public Liquidacion liquidar(Trabajador trabajador,
            LocalDate fecha) throws SQLException {

        Liquidacion liquidacion =
            new Liquidacion(trabajador, fecha);

        dao.guardar(liquidacion);

        notificarObservadores(liquidacion);

        return liquidacion;
    }
}