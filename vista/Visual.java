
package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;

import modelo.*;
import persistencia.*;
import patron.*;

public class Visual extends JFrame {

    // DAO y Observer

    private TrabajadorDAO dao = new TrabajadorDAOI();
    private LiquidacionDAO daoL = new LiquidacionDAOI();

    private Liquidador liquidador = new Liquidador(daoL);

    private ContabilidadObserver contabilidad =
        new ContabilidadObserver();

    private AlertaObserver alerta = new AlertaObserver();

    // Campos de trabajadores

    private JTextField txtDocumento = new JTextField();
    private JTextField txtNombre = new JTextField();
    private JTextField txtFecha = new JTextField();
    private JTextField txtSalario = new JTextField();
    private JTextField txtContrato = new JTextField();
    private JTextField txtHoras = new JTextField();
    private JTextField txtValorHora = new JTextField();

    private JComboBox<String> cmbTipo = new JComboBox<>(
        new String[]{"Planta", "Contratista", "Por horas"}
    );

    private DefaultTableModel modeloTabla = crearModelo(
        new String[]{"Documento", "Nombre", "Tipo", "Pago"}
    );

    private JTable tabla = new JTable(modeloTabla);

    private boolean editando = false;

    // Campos de liquidaciones

    private JComboBox<String> cmbTrabajadores =
        new JComboBox<>();

    private JTextField txtFechaLiquidacion =
        new JTextField("2026-11-30");

    private JCheckBox chkAlerta =
        new JCheckBox("Activar alertas", true);

    private JLabel lblTotalSesion =
        new JLabel("Total de la sesion: $0");

    private DefaultTableModel modeloLiquidaciones = crearModelo(
        new String[]{
            "Documento", "Nombre", "Tipo", "Periodo",
            "Pago", "Bono", "Total"
        }
    );

    private JTable tablaLiquidaciones =
        new JTable(modeloLiquidaciones);

    // Campos de consultas

    private JTextField txtPeriodo =
        new JTextField("2026-11");

    private JComboBox<String> cmbTipoConsulta =
        new JComboBox<>(
            new String[]{"Planta", "Contratista", "Por horas"}
        );

    private JTextArea txtResultados = new JTextArea();

    // Constructor

    public Visual() {

        setTitle("NominaLibre");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        liquidador.agregarObservador(contabilidad);
        liquidador.agregarObservador(alerta);

        JTabbedPane pestanas = new JTabbedPane();

        pestanas.addTab("Trabajadores", panelTrabajadores());
        pestanas.addTab("Liquidaciones", panelLiquidaciones());
        pestanas.addTab("Consultas", panelConsultas());

        add(pestanas);

        cambiarCampos();
        cargarTabla();
        cargarTrabajadores();
        cargarLiquidaciones();

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                cerrarPrograma();
            }
        });
    }

    // Crear modelos de tablas

    private DefaultTableModel crearModelo(String[] columnas) {

        return new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {

                return false;
            }
        };
    }


    private JPanel panelTrabajadores() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel formulario = new JPanel(
            new GridLayout(8, 2, 8, 8)
        );

        formulario.setBorder(
            BorderFactory.createTitledBorder("Datos del trabajador")
        );

        formulario.add(new JLabel("Documento:"));
        formulario.add(txtDocumento);

        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);

        formulario.add(new JLabel("Fecha de ingreso (AAAA-MM-DD):"));
        formulario.add(txtFecha);

        formulario.add(new JLabel("Tipo de trabajador:"));
        formulario.add(cmbTipo);

        formulario.add(new JLabel("Salario base:"));
        formulario.add(txtSalario);

        formulario.add(new JLabel("Valor contrato:"));
        formulario.add(txtContrato);

        formulario.add(new JLabel("Horas trabajadas:"));
        formulario.add(txtHoras);

        formulario.add(new JLabel("Valor hora:"));
        formulario.add(txtValorHora);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        JPanel botones = new JPanel();

        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEditar);
        botones.add(btnEliminar);

        JPanel superior = new JPanel(new BorderLayout());

        superior.add(formulario, BorderLayout.CENTER);
        superior.add(botones, BorderLayout.SOUTH);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        cmbTipo.addActionListener(e -> cambiarCampos());

        btnNuevo.addActionListener(e -> limpiarCampos());

        btnGuardar.addActionListener(e -> guardarTrabajador());

        btnEditar.addActionListener(e -> editarTrabajador());

        btnEliminar.addActionListener(e -> eliminarTrabajador());

        return panel;
    }

    private void cambiarCampos() {

        String tipo = cmbTipo.getSelectedItem().toString();

        txtSalario.setEnabled(tipo.equals("Planta"));

        txtContrato.setEnabled(tipo.equals("Contratista"));

        txtHoras.setEnabled(tipo.equals("Por horas"));

        txtValorHora.setEnabled(tipo.equals("Por horas"));
    }

    private Trabajador crearTrabajador() throws Exception {

        String documento = txtDocumento.getText();
        String nombre = txtNombre.getText();

        LocalDate fecha = LocalDate.parse(txtFecha.getText());

        String tipo = cmbTipo.getSelectedItem().toString();

        if (tipo.equals("Planta")) {

            double salario = Double.parseDouble(
                txtSalario.getText());

            return new EmpleadoPlanta(
                documento, nombre, fecha, salario);

        } else if (tipo.equals("Contratista")) {

            double contrato = Double.parseDouble(
                txtContrato.getText());

            return new Contratista(
                documento, nombre, fecha, contrato);

        } else {

            int horas = Integer.parseInt(txtHoras.getText());

            double valorHora = Double.parseDouble(
                txtValorHora.getText());

            return new EmpleadoHoras(
                documento, nombre, fecha, horas, valorHora);
        }
    }

    private void guardarTrabajador() {

        try {

            Trabajador t = crearTrabajador();

            if (editando) {

                dao.editar(t);

                JOptionPane.showMessageDialog(
                    this, "Trabajador actualizado");

            } else {

                dao.guardar(t);

                JOptionPane.showMessageDialog(
                    this, "Trabajador guardado");
            }

            cargarTabla();
            cargarTrabajadores();
            limpiarCampos();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void cargarTabla() {

        try {

            modeloTabla.setRowCount(0);

            for (Trabajador t : dao.listar()) {

                Object[] fila = {
                    t.getDocumento(),
                    t.getNombre(),
                    t.getTipo(),
                    moneda(t.calcularPago())
                };

                modeloTabla.addRow(fila);
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void editarTrabajador() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            mostrarError("Seleccione un trabajador");
            return;
        }

        try {

            String documento = tabla.getValueAt(
                fila, 0).toString();

            Trabajador t = dao.buscar(documento);

            limpiarCampos();

            txtDocumento.setText(t.getDocumento());
            txtNombre.setText(t.getNombre());

            txtFecha.setText(t.getFechaIngreso().toString());

            cmbTipo.setSelectedItem(t.getTipo());

            if (t instanceof EmpleadoPlanta) {

                EmpleadoPlanta empleado = (EmpleadoPlanta) t;

                txtSalario.setText(
                    String.valueOf(empleado.getSalarioBase()));

            } else if (t instanceof Contratista) {

                Contratista empleado = (Contratista) t;

                txtContrato.setText(
                    String.valueOf(empleado.getValorContrato()));

            } else if (t instanceof EmpleadoHoras) {

                EmpleadoHoras empleado = (EmpleadoHoras) t;

                txtHoras.setText(
                    String.valueOf(empleado.getHorasTrabajadas()));

                txtValorHora.setText(
                    String.valueOf(empleado.getValorHora()));
            }

            editando = true;
            txtDocumento.setEnabled(false);

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void eliminarTrabajador() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {

            mostrarError("Seleccione un trabajador");
            return;
        }

        String documento = tabla.getValueAt(
            fila, 0).toString();

        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "Desea eliminar este trabajador?",
            "Confirmar eliminacion",
            JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            try {

                dao.eliminar(documento);

                cargarTabla();
                cargarTrabajadores();
                limpiarCampos();

                JOptionPane.showMessageDialog(
                    this, "Trabajador eliminado");

            } catch (Exception e) {

                mostrarError(e.getMessage());
            }
        }
    }

    private void limpiarCampos() {

        txtDocumento.setText("");
        txtNombre.setText("");
        txtFecha.setText("");
        txtSalario.setText("");
        txtContrato.setText("");
        txtHoras.setText("");
        txtValorHora.setText("");

        cmbTipo.setSelectedIndex(0);

        txtDocumento.setEnabled(true);

        editando = false;

        tabla.clearSelection();

        cambiarCampos();
    }

    private JPanel panelLiquidaciones() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel formulario = new JPanel(
            new GridLayout(3, 2, 10, 10)
        );

        formulario.setBorder(
            BorderFactory.createTitledBorder("Liquidar trabajador")
        );

        formulario.add(new JLabel("Trabajador:"));
        formulario.add(cmbTrabajadores);

        formulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        formulario.add(txtFechaLiquidacion);

        JButton btnLiquidar = new JButton("Liquidar");

        formulario.add(chkAlerta);
        formulario.add(btnLiquidar);

        panel.add(formulario, BorderLayout.NORTH);

        panel.add(
            new JScrollPane(tablaLiquidaciones),
            BorderLayout.CENTER
        );

        panel.add(lblTotalSesion, BorderLayout.SOUTH);

        btnLiquidar.addActionListener(e -> liquidarTrabajador());

        chkAlerta.addActionListener(e -> cambiarAlerta());

        return panel;
    }

    private void cargarTrabajadores() {

        try {

            cmbTrabajadores.removeAllItems();

            for (Trabajador t : dao.listar()) {

                cmbTrabajadores.addItem(
                    t.getDocumento() + " - " + t.getNombre()
                );
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void liquidarTrabajador() {

        try {

            if (cmbTrabajadores.getSelectedItem() == null) {

                mostrarError("No hay trabajadores registrados");
                return;
            }

            String seleccionado =
                cmbTrabajadores.getSelectedItem().toString();

            String documento = seleccionado.split(" - ", 2)[0];

            Trabajador t = dao.buscar(documento);

            if (t == null) {

                mostrarError("Trabajador no encontrado");
                return;
            }

            LocalDate fecha = LocalDate.parse(
                txtFechaLiquidacion.getText());

            Liquidacion l = liquidador.liquidar(t, fecha);

            JOptionPane.showMessageDialog(
                this,
                "Liquidacion realizada correctamente\n\n"
                + "Trabajador: " + t.getNombre()
                + "\nPago: " + moneda(l.getPago())
                + "\nBono: " + moneda(l.getBono())
                + "\nTotal: " + moneda(l.getTotal())
            );

            if (chkAlerta.isSelected()) {

                if (!alerta.getMensaje().isEmpty()) {

                    JOptionPane.showMessageDialog(
                        this,
                        alerta.getMensaje(),
                        "Alerta de pago",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }

            lblTotalSesion.setText(
                "Total de la sesion: "
                + moneda(contabilidad.getTotalSesion())
            );

            cargarLiquidaciones();

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void cambiarAlerta() {

        if (chkAlerta.isSelected()) {

            liquidador.agregarObservador(alerta);

        } else {

            liquidador.eliminarObservador(alerta);
        }
    }

    private void cargarLiquidaciones() {

        try {

            modeloLiquidaciones.setRowCount(0);

            for (Liquidacion l : daoL.listar()) {

                Object[] fila = {

                    l.getTrabajador().getDocumento(),
                    l.getTrabajador().getNombre(),
                    l.getTrabajador().getTipo(),
                    l.getPeriodo().toString(),
                    moneda(l.getPago()),
                    moneda(l.getBono()),
                    moneda(l.getTotal())
                };

                modeloLiquidaciones.addRow(fila);
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private JPanel panelConsultas() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel formulario = new JPanel(
            new GridLayout(3, 2, 10, 10)
        );

        formulario.setBorder(
            BorderFactory.createTitledBorder("Consultas de nomina")
        );

        formulario.add(new JLabel("Periodo (AAAA-MM):"));
        formulario.add(txtPeriodo);

        formulario.add(new JLabel("Tipo de trabajador:"));
        formulario.add(cmbTipoConsulta);

        JButton btnTotal = new JButton("Total por tipo");

        JButton btnSinPago = new JButton("Trabajadores sin pago");

        formulario.add(btnTotal);
        formulario.add(btnSinPago);

        txtResultados.setEditable(false);

        txtResultados.setFont(
            new Font("Monospaced", Font.PLAIN, 15)
        );

        panel.add(formulario, BorderLayout.NORTH);

        panel.add(
            new JScrollPane(txtResultados),
            BorderLayout.CENTER
        );

        btnTotal.addActionListener(e -> consultarTotal());

        btnSinPago.addActionListener(e -> consultarSinPago());

        return panel;
    }

    private void consultarTotal() {

        try {

            String periodo = YearMonth.parse(
                txtPeriodo.getText()).toString();

            String tipo = cmbTipoConsulta.getSelectedItem()
                .toString();

            double total = daoL.totalPorTipo(periodo, tipo);

            txtResultados.setText(
                "TOTAL DE NOMINA POR TIPO\n"
                + "-----------------------------\n\n"
                + "Periodo: " + periodo
                + "\nTipo: " + tipo
                + "\nTotal: " + moneda(total)
            );

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }

    private void consultarSinPago() {

        try {

            String periodo = YearMonth.parse(
                txtPeriodo.getText()).toString();

            txtResultados.setText(
                "TRABAJADORES SIN PAGO\n"
                + "Periodo: " + periodo
                + "\n-----------------------------\n\n"
            );

            int cantidad = 0;

            for (Trabajador t : daoL.sinPago(periodo)) {

                txtResultados.append(
                    "Documento: " + t.getDocumento()
                    + "\nNombre: " + t.getNombre()
                    + "\nTipo: " + t.getTipo()
                    + "\n-----------------------------\n"
                );

                cantidad++;
            }

            if (cantidad == 0) {

                txtResultados.append(
                    "No hay trabajadores pendientes de pago."
                );

            } else {

                txtResultados.append(
                    "\nCantidad de trabajadores: " + cantidad
                );
            }

        } catch (Exception e) {

            mostrarError(e.getMessage());
        }
    }


    private String moneda(double valor) {

        return String.format(
            java.util.Locale.forLanguageTag("es-CO"),
            "$%,.2f", valor
        );
    }

    private void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void cerrarPrograma() {

        JOptionPane.showMessageDialog(
            this,
            "Resumen de contabilidad\n\n"
            + "Total liquidado durante la sesion: "
            + moneda(contabilidad.getTotalSesion()),
            "Contabilidad",
            JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
    }
}
