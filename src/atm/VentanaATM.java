/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package atm;

import bases_datos.Conexion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alberto
 */
public class VentanaATM extends javax.swing.JFrame {

    public static final int NUM_INTENTOS = 3;
    public static final int MAX_INGRESO_RETIRAR = 1500;
    public static final int MAX_CAMBIO_MONEDA = 1000;
    public static final int MAX_TRANSFERENCIA = 2000;
    public static final int MAX_BILLETE = 50;
    public static final int MAX_CAJERO = 10000;
    public static final int MAX_INACTIVIDAD = 60000;

    /**
     * Creates new form VentanaATM
     */
    public VentanaATM() {
        setPreferredSize(new Dimension(1800, 1100));
        setLocation(100, 0);
        getContentPane().setBackground(Color.black);
        initComponents();
        initBD();
        initReloj();
        contenedor.setSelectedIndex(0);
        pantalla = contenedor.getSelectedIndex();

    }

    private void initTransferencia() {
        modeloTabla = (DefaultTableModel) tablaTransferencia.getModel();
        tablaTransferencia.setModel(modeloTabla);
        tablaTransferencia.getColumnModel().getColumn(0).setPreferredWidth(150);
        tablaTransferencia.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaTransferencia.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaTransferencia.getColumnModel().getColumn(3).setPreferredWidth(310);
        modeloTabla.setRowCount(0);
        try {
            sentencia = conexion.createStatement();
            String selectCuentas = "select nombre, apellido1, apellido2, iban from cuenta_bancaria cb join cliente c on cb.dniCliente = c.dni where bloqueada = 0;";
            resultado = sentencia.executeQuery(selectCuentas);
            while (resultado.next()) {
                String nombre = resultado.getString("nombre");
                String apellido1 = resultado.getString("apellido1");
                String apellido2 = resultado.getString("apellido2");
                String iban = resultado.getString("iban");
                Object[] cuenta = {nombre, apellido1, apellido2, iban};
                modeloTabla.addRow(cuenta);
            }

        } catch (SQLException ex) {
            Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initMovimientos() {
        modeloTabla = (DefaultTableModel) tablaMovimientos.getModel();
        tablaMovimientos.setModel(modeloTabla);
        tablaMovimientos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tablaMovimientos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaMovimientos.getColumnModel().getColumn(2).setPreferredWidth(250);
        tablaMovimientos.getColumnModel().getColumn(3).setPreferredWidth(310);
        modeloTabla.setRowCount(0);
        try {
            sentencia = conexion.createStatement();
            String selectMovimientos = "select fecha_operacion, saldo_operacion, idOperacion, ibanReceptor from historico_operacion where ibanEmisor = \"" + ibanRegistrado + "\";";
            resultado = sentencia.executeQuery(selectMovimientos);
            while (resultado.next()) {
                String fecha = resultado.getString("fecha_operacion");
                double valor = resultado.getDouble("saldo_operacion");
                int num_operacion = resultado.getInt("idOperacion");
                String op = "";
                switch (num_operacion) {
                    case 1:
                        op = Operacion.RE.getNombre();
                        break;
                    case 2:
                        op = Operacion.DE.getNombre();
                        break;
                    case 3:
                        op = Operacion.RT.getNombre();
                        break;
                    case 4:
                        op = Operacion.CME.getNombre();
                        break;
                    case 5:
                        op = Operacion.PF.getNombre();
                }
                String ibanReceptor = resultado.getString("ibanReceptor");
                Object[] movBan = {fecha, valor, op, ibanReceptor};
                modeloTabla.addRow(movBan);
            }
            double saldo;
            String getSaldo = "select saldo from cuenta_bancaria where iban = \"" + ibanRegistrado + "\";";
            resultado = sentencia.executeQuery(getSaldo);
            resultado.next();
            saldo = resultado.getDouble("saldo");
            txfSaldoTotal.setText(String.valueOf(saldo));

        } catch (SQLException ex) {
            Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initBD() {
        conexion = Conexion.mySQL("atm", "root", "");
        if (conexion == null) {
            JOptionPane.showMessageDialog(this, "No se ha encontrado la base de datos", "ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void initReloj() {
        List<JButton> buttons = new ArrayList<>();

        // Obtener todos los componentes del JFrame
        Component[] components = getContentPane().getComponents();
        Component[] intermedios;
        Component[] jpanel;
        //Jframe
        for (Component component : components) {
            if (component instanceof JButton) {
                buttons.add((JButton) component);
            } else if (component instanceof JPanel) {
                intermedios = ((JPanel) component).getComponents();
                //Primer JPanel
                for (Component aux : intermedios) {
                    if (aux instanceof JButton) {
                        buttons.add((JButton) aux);
                    } else if (aux instanceof JPanel) {
                        //Segundo JPanel
                        jpanel = ((JPanel) aux).getComponents();
                        for (Component comp : jpanel) {
                            if (comp instanceof JButton) {
                                buttons.add((JButton) comp);
                            }
                        }
                    }
                }
            }
        }

        // Temporizador para cerrar el JFrame después de un minuto sin eventos
        reloj = new Timer(MAX_INACTIVIDAD, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Tu sesión a caducado por inactividad, por favor regístrese de nuevo.", "Tiempo de sesión agotado ", JOptionPane.INFORMATION_MESSAGE);
                ibanRegistrado = "";
                lblTarjeta.setBackground(Color.red);
                lblTicket.setBackground(Color.red);
                contenedor.setSelectedIndex(0);
            }
        });
        reloj.setRepeats(false);

        ActionListener actionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                reloj.stop();
                reloj.restart();
            }
        };

        for (JButton button : buttons) {
            button.addActionListener(actionListener);
        }

        if (lblTarjeta.getBackground().equals(Color.green)) {
            reloj.start();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        contenedor = new javax.swing.JTabbedPane();
        panelIdioma = new javax.swing.JPanel();
        lblIntroduccion = new javax.swing.JLabel();
        btnEspanol = new javax.swing.JButton();
        btnIngles = new javax.swing.JButton();
        menuPrincipal = new javax.swing.JPanel();
        lblIdioma = new javax.swing.JLabel();
        lblRetirar = new javax.swing.JLabel();
        lblDepositar = new javax.swing.JLabel();
        lblCambiarPIN = new javax.swing.JLabel();
        lblAgenda = new javax.swing.JLabel();
        lblTransferencia = new javax.swing.JLabel();
        lblMoneda = new javax.swing.JLabel();
        lblFactura = new javax.swing.JLabel();
        retirarSaldo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        depositarSaldo = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        cambiarPIN = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        passwd1 = new javax.swing.JPasswordField();
        jLabel20 = new javax.swing.JLabel();
        passwd2 = new javax.swing.JPasswordField();
        consultarAgenda = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMovimientos = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        txfSaldoTotal = new javax.swing.JTextField();
        realizarTransferencia = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaTransferencia = new javax.swing.JTable();
        cambiarMoneda = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        pagarFactura = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        txfFactura = new javax.swing.JTextField();
        confirmarOperacion = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        confirmarTicket = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        txfRetirar = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        txfIngresar = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        txfTransferencia = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnInzquierda1 = new javax.swing.JButton();
        btnInzquierda2 = new javax.swing.JButton();
        btnInzquierda3 = new javax.swing.JButton();
        btnInzquierda4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        btnDerecha1 = new javax.swing.JButton();
        btnDerecha2 = new javax.swing.JButton();
        btnDerecha3 = new javax.swing.JButton();
        btnDerecha4 = new javax.swing.JButton();
        btnDinero = new javax.swing.JButton();
        tecladoPrincipal = new javax.swing.JPanel();
        lblBtones = new javax.swing.JLabel();
        panelBotones = new javax.swing.JPanel();
        lbl1 = new javax.swing.JLabel();
        lbl2 = new javax.swing.JLabel();
        lbl3 = new javax.swing.JLabel();
        lbl4 = new javax.swing.JLabel();
        lbl5 = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        lbl7 = new javax.swing.JLabel();
        lbl8 = new javax.swing.JLabel();
        lbl9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        lbl0 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        panelAcciones = new javax.swing.JPanel();
        lblCancel = new javax.swing.JLabel();
        lblClear = new javax.swing.JLabel();
        lblEnter = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblTicket = new javax.swing.JLabel();
        lblTarjeta = new javax.swing.JLabel();
        btnTicket = new javax.swing.JButton();
        btnTarjeta = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ATM");
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        contenedor.setFocusable(false);

        lblIntroduccion.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblIntroduccion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblIntroduccion.setText("Bienvenido, por favor eliga el idioma con el que quiere realizar las operaciones");

        btnEspanol.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\src\\ATM_Images\\espana.png"));
        btnEspanol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEspanolActionPerformed(evt);
            }
        });

        btnIngles.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\src\\ATM_Images\\inglaterra.png"));
        btnIngles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInglesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelIdiomaLayout = new javax.swing.GroupLayout(panelIdioma);
        panelIdioma.setLayout(panelIdiomaLayout);
        panelIdiomaLayout.setHorizontalGroup(
            panelIdiomaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIdiomaLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addComponent(lblIntroduccion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(90, 90, 90))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIdiomaLayout.createSequentialGroup()
                .addGap(134, 134, 134)
                .addComponent(btnEspanol, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnIngles, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(135, 135, 135))
        );
        panelIdiomaLayout.setVerticalGroup(
            panelIdiomaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIdiomaLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblIntroduccion, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelIdiomaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnIngles, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(btnEspanol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        contenedor.addTab("tab1", panelIdioma);

        menuPrincipal.setBorder(new javax.swing.border.MatteBorder(null));

        lblIdioma.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblIdioma.setText("CAMBIAR IDIOMA");

        lblRetirar.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblRetirar.setText("RETIRAR EFECTIVO");

        lblDepositar.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblDepositar.setText("DEPOSITAR EFECTIVO");

        lblCambiarPIN.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblCambiarPIN.setText("CAMBIAR NÚMERO SECRETO");

        lblAgenda.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblAgenda.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblAgenda.setText("CONSULTAR AGENDA FINANCIERA");

        lblTransferencia.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblTransferencia.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTransferencia.setText("REALIZAR TRANSFERENCIA");

        lblMoneda.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblMoneda.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblMoneda.setText("CAMBIAR A MONEDA EXTRANJERA");

        lblFactura.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        lblFactura.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFactura.setText("PAGAR FACTURA");

        javax.swing.GroupLayout menuPrincipalLayout = new javax.swing.GroupLayout(menuPrincipal);
        menuPrincipal.setLayout(menuPrincipalLayout);
        menuPrincipalLayout.setHorizontalGroup(
            menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPrincipalLayout.createSequentialGroup()
                .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(menuPrincipalLayout.createSequentialGroup()
                        .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDepositar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(menuPrincipalLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblCambiarPIN, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)))
                        .addGap(206, 206, 206))
                    .addGroup(menuPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRetirar, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMoneda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblTransferencia, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(menuPrincipalLayout.createSequentialGroup()
                        .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFactura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        menuPrincipalLayout.setVerticalGroup(
            menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuPrincipalLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTransferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRetirar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDepositar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addGroup(menuPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCambiarPIN, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        contenedor.addTab("tab2", menuPrincipal);

        retirarSaldo.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("¿Cuánto saldo desea retirar?");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel2.setText("20");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("50");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel5.setText("75");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("100");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel7.setText("200");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Otra cantidad");

        javax.swing.GroupLayout retirarSaldoLayout = new javax.swing.GroupLayout(retirarSaldo);
        retirarSaldo.setLayout(retirarSaldoLayout);
        retirarSaldoLayout.setHorizontalGroup(
            retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, retirarSaldoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(retirarSaldoLayout.createSequentialGroup()
                .addGap(317, 317, 317)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 323, Short.MAX_VALUE))
        );
        retirarSaldoLayout.setVerticalGroup(
            retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(retirarSaldoLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(124, 124, 124)
                .addGroup(retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(retirarSaldoLayout.createSequentialGroup()
                        .addGroup(retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(88, 88, 88))
                    .addGroup(retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(retirarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        contenedor.addTab("tab3", retirarSaldo);

        depositarSaldo.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel11.setText("¿Cuánto saldo desea ingresar?");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel12.setText("20");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel13.setText("75");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel14.setText("200");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("50");

        jLabel16.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("100");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Otra cantidad");

        javax.swing.GroupLayout depositarSaldoLayout = new javax.swing.GroupLayout(depositarSaldo);
        depositarSaldo.setLayout(depositarSaldoLayout);
        depositarSaldoLayout.setHorizontalGroup(
            depositarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depositarSaldoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(depositarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(depositarSaldoLayout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(depositarSaldoLayout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, depositarSaldoLayout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, depositarSaldoLayout.createSequentialGroup()
                .addContainerGap(318, Short.MAX_VALUE)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(291, 291, 291))
        );
        depositarSaldoLayout.setVerticalGroup(
            depositarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(depositarSaldoLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(131, 131, 131)
                .addGroup(depositarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(depositarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addGroup(depositarSaldoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        contenedor.addTab("tab4", depositarSaldo);

        cambiarPIN.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel19.setText("Introduzca el nuevo PIN:");

        passwd1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        passwd1.setFocusable(false);

        jLabel20.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel20.setText("Introduzca otra vez el nuevo PIN:");

        passwd2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        passwd2.setFocusable(false);

        javax.swing.GroupLayout cambiarPINLayout = new javax.swing.GroupLayout(cambiarPIN);
        cambiarPIN.setLayout(cambiarPINLayout);
        cambiarPINLayout.setHorizontalGroup(
            cambiarPINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiarPINLayout.createSequentialGroup()
                .addGap(336, 336, 336)
                .addGroup(cambiarPINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwd2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(passwd1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap(362, Short.MAX_VALUE))
        );
        cambiarPINLayout.setVerticalGroup(
            cambiarPINLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiarPINLayout.createSequentialGroup()
                .addContainerGap(139, Short.MAX_VALUE)
                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwd1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwd2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65))
        );

        contenedor.addTab("tab5", cambiarPIN);

        consultarAgenda.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel22.setText("Movimientos de tu cuenta bancaria:");

        tablaMovimientos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Fecha operacion", "Saldo operacion", "Operacion", "Iban Receptor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaMovimientos);

        jLabel23.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel23.setText("Saldo de su cuenta bancaria:");

        txfSaldoTotal.setFocusable(false);

        javax.swing.GroupLayout consultarAgendaLayout = new javax.swing.GroupLayout(consultarAgenda);
        consultarAgenda.setLayout(consultarAgendaLayout);
        consultarAgendaLayout.setHorizontalGroup(
            consultarAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consultarAgendaLayout.createSequentialGroup()
                .addGroup(consultarAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(consultarAgendaLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 607, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, consultarAgendaLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(152, 152, 152)))
                .addGroup(consultarAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, consultarAgendaLayout.createSequentialGroup()
                        .addComponent(txfSaldoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, consultarAgendaLayout.createSequentialGroup()
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19))))
        );
        consultarAgendaLayout.setVerticalGroup(
            consultarAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(consultarAgendaLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(consultarAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(consultarAgendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txfSaldoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        contenedor.addTab("tab6", consultarAgenda);

        realizarTransferencia.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        jLabel24.setText("Indique a que cuenta bancaria quiere realizar la transferencia:");

        tablaTransferencia.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "Apellido 1", "Apellido 2", "Cuenta Bancaria"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tablaTransferencia);

        javax.swing.GroupLayout realizarTransferenciaLayout = new javax.swing.GroupLayout(realizarTransferencia);
        realizarTransferencia.setLayout(realizarTransferenciaLayout);
        realizarTransferenciaLayout.setHorizontalGroup(
            realizarTransferenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(realizarTransferenciaLayout.createSequentialGroup()
                .addGap(192, 192, 192)
                .addGroup(realizarTransferenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap(244, Short.MAX_VALUE))
        );
        realizarTransferenciaLayout.setVerticalGroup(
            realizarTransferenciaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(realizarTransferenciaLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
        );

        contenedor.addTab("tab7", realizarTransferencia);

        cambiarMoneda.setBorder(new javax.swing.border.MatteBorder(null));

        jLabel25.setText("Eliga la moneda extranjera");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel26.setText("20");

        jLabel27.setText("50");

        jLabel28.setText("75");

        jLabel29.setText("100");

        jLabel30.setText("200");

        jLabel31.setText("Otra cantidad");

        javax.swing.GroupLayout cambiarMonedaLayout = new javax.swing.GroupLayout(cambiarMoneda);
        cambiarMoneda.setLayout(cambiarMonedaLayout);
        cambiarMonedaLayout.setHorizontalGroup(
            cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiarMonedaLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(131, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cambiarMonedaLayout.createSequentialGroup()
                .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(cambiarMonedaLayout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, cambiarMonedaLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(79, 79, 79))
        );
        cambiarMonedaLayout.setVerticalGroup(
            cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cambiarMonedaLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                .addGap(114, 114, 114)
                .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(cambiarMonedaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );

        contenedor.addTab("tab8", cambiarMoneda);

        jLabel32.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel32.setText("Introduzca el valor de la factura a pagar:");

        javax.swing.GroupLayout pagarFacturaLayout = new javax.swing.GroupLayout(pagarFactura);
        pagarFactura.setLayout(pagarFacturaLayout);
        pagarFacturaLayout.setHorizontalGroup(
            pagarFacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pagarFacturaLayout.createSequentialGroup()
                .addGap(295, 295, 295)
                .addGroup(pagarFacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txfFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(211, Short.MAX_VALUE))
        );
        pagarFacturaLayout.setVerticalGroup(
            pagarFacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pagarFacturaLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(txfFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(261, Short.MAX_VALUE))
        );

        contenedor.addTab("tab9", pagarFactura);

        jLabel33.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setText("¿Está usted seguro de realizar esta operación?");

        javax.swing.GroupLayout confirmarOperacionLayout = new javax.swing.GroupLayout(confirmarOperacion);
        confirmarOperacion.setLayout(confirmarOperacionLayout);
        confirmarOperacionLayout.setHorizontalGroup(
            confirmarOperacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmarOperacionLayout.createSequentialGroup()
                .addGap(119, 119, 119)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 721, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(141, Short.MAX_VALUE))
        );
        confirmarOperacionLayout.setVerticalGroup(
            confirmarOperacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmarOperacionLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );

        contenedor.addTab("tab10", confirmarOperacion);

        jLabel34.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel34.setText("¿Desea obtener un ticket sobre la operación realizada?");

        javax.swing.GroupLayout confirmarTicketLayout = new javax.swing.GroupLayout(confirmarTicket);
        confirmarTicket.setLayout(confirmarTicketLayout);
        confirmarTicketLayout.setHorizontalGroup(
            confirmarTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, confirmarTicketLayout.createSequentialGroup()
                .addContainerGap(189, Short.MAX_VALUE)
                .addComponent(jLabel34)
                .addGap(179, 179, 179))
        );
        confirmarTicketLayout.setVerticalGroup(
            confirmarTicketLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmarTicketLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(379, Short.MAX_VALUE))
        );

        contenedor.addTab("tab11", confirmarTicket);

        jLabel35.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel35.setText("¿Desea realizar más operaciones?");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(284, 284, 284)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(245, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(393, Short.MAX_VALUE))
        );

        contenedor.addTab("tab12", jPanel3);

        jLabel36.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel36.setText("Introduzca la cantidad a retirar");

        txfRetirar.setEditable(false);
        txfRetirar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txfRetirar.setFocusable(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(393, 393, 393)
                .addComponent(txfRetirar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(428, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(235, 235, 235))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(txfRetirar, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(249, Short.MAX_VALUE))
        );

        contenedor.addTab("tab13", jPanel5);

        jLabel37.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel37.setText("Por favor, retire su dinero.");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(311, Short.MAX_VALUE)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(274, 274, 274))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        contenedor.addTab("tab14", jPanel6);

        jLabel38.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel38.setText("Introduce la cantidad a ingresar");

        txfIngresar.setFocusable(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(298, Short.MAX_VALUE)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(294, 294, 294))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(394, 394, 394)
                .addComponent(txfIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(txfIngresar, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(275, Short.MAX_VALUE))
        );

        contenedor.addTab("tab15", jPanel7);

        jLabel39.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel39.setText("Indique la cantidad a transferir");

        txfTransferencia.setFocusable(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(298, 298, 298)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(383, 383, 383)
                        .addComponent(txfTransferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(281, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(txfTransferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(237, Short.MAX_VALUE))
        );

        contenedor.addTab("tab16", jPanel8);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new java.awt.GridLayout(4, 1, 0, 20));

        btnInzquierda1.setBackground(new java.awt.Color(153, 153, 153));
        btnInzquierda1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInzquierda1ActionPerformed(evt);
            }
        });
        jPanel2.add(btnInzquierda1);

        btnInzquierda2.setBackground(new java.awt.Color(153, 153, 153));
        btnInzquierda2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInzquierda2ActionPerformed(evt);
            }
        });
        jPanel2.add(btnInzquierda2);

        btnInzquierda3.setBackground(new java.awt.Color(153, 153, 153));
        btnInzquierda3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInzquierda3ActionPerformed(evt);
            }
        });
        jPanel2.add(btnInzquierda3);

        btnInzquierda4.setBackground(new java.awt.Color(153, 153, 153));
        btnInzquierda4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInzquierda4ActionPerformed(evt);
            }
        });
        jPanel2.add(btnInzquierda4);

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new java.awt.GridLayout(4, 1, 0, 20));

        btnDerecha1.setBackground(new java.awt.Color(153, 153, 153));
        btnDerecha1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDerecha1ActionPerformed(evt);
            }
        });
        jPanel4.add(btnDerecha1);

        btnDerecha2.setBackground(new java.awt.Color(153, 153, 153));
        btnDerecha2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDerecha2ActionPerformed(evt);
            }
        });
        jPanel4.add(btnDerecha2);

        btnDerecha3.setBackground(new java.awt.Color(153, 153, 153));
        btnDerecha3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDerecha3ActionPerformed(evt);
            }
        });
        jPanel4.add(btnDerecha3);

        btnDerecha4.setBackground(new java.awt.Color(153, 153, 153));
        btnDerecha4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDerecha4ActionPerformed(evt);
            }
        });
        jPanel4.add(btnDerecha4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contenedor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(contenedor))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        btnDinero.setBackground(new java.awt.Color(153, 153, 153));
        btnDinero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDineroActionPerformed(evt);
            }
        });

        tecladoPrincipal.setBackground(new java.awt.Color(0, 0, 0));
        tecladoPrincipal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tecladoPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBtones.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\src\\ATM_Images\\teclado.png"));
        tecladoPrincipal.add(lblBtones, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 680, 430));

        panelBotones.setLayout(new java.awt.GridLayout(4, 3, 30, 30));

        lbl1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl1MouseClicked(evt);
            }
        });
        panelBotones.add(lbl1);

        lbl2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl2MouseClicked(evt);
            }
        });
        panelBotones.add(lbl2);

        lbl3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl3MouseClicked(evt);
            }
        });
        panelBotones.add(lbl3);

        lbl4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl4MouseClicked(evt);
            }
        });
        panelBotones.add(lbl4);

        lbl5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl5MouseClicked(evt);
            }
        });
        panelBotones.add(lbl5);

        lbl6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl6MouseClicked(evt);
            }
        });
        panelBotones.add(lbl6);

        lbl7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl7MouseClicked(evt);
            }
        });
        panelBotones.add(lbl7);

        lbl8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl8MouseClicked(evt);
            }
        });
        panelBotones.add(lbl8);

        lbl9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl9MouseClicked(evt);
            }
        });
        panelBotones.add(lbl9);
        panelBotones.add(jLabel10);

        lbl0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl0MouseClicked(evt);
            }
        });
        panelBotones.add(lbl0);
        panelBotones.add(jLabel8);

        tecladoPrincipal.add(panelBotones, new org.netbeans.lib.awtextra.AbsoluteConstraints(83, 30, 350, 380));

        panelAcciones.setLayout(new java.awt.GridLayout(4, 1, 0, 20));

        lblCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCancelMouseClicked(evt);
            }
        });
        panelAcciones.add(lblCancel);
        panelAcciones.add(lblClear);

        lblEnter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEnterMouseClicked(evt);
            }
        });
        panelAcciones.add(lblEnter);
        panelAcciones.add(jLabel17);

        tecladoPrincipal.add(panelAcciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, 120, 361));

        lblTicket.setBackground(new java.awt.Color(255, 0, 0));
        lblTicket.setOpaque(true);

        lblTarjeta.setBackground(new java.awt.Color(255, 0, 0));
        lblTarjeta.setFocusable(false);
        lblTarjeta.setOpaque(true);

        btnTicket.setBackground(new java.awt.Color(153, 153, 153));
        btnTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTicketActionPerformed(evt);
            }
        });

        btnTarjeta.setBackground(new java.awt.Color(153, 153, 153));
        btnTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTarjetaActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\src\\ATM_Images\\insertarTarjeta.png"));

        jLabel21.setIcon(new javax.swing.ImageIcon(System.getProperty("user.dir")+"\\src\\ATM_Images\\ticket.png"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(262, 262, 262)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tecladoPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(btnDinero, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 515, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(62, 62, 62))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(125, 125, 125))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(btnTicket, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnDinero, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(btnTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jLabel3))
                    .addComponent(tecladoPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lbl1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl1MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "1");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "1");
                    } else {
                        evt.consume();
                    }

                }
                break;

            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "1");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "1");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "1");
                } else {
                    evt.consume();
                }
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "1");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl1MouseClicked

    private void btnTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTarjetaActionPerformed
        if (lblTarjeta.getBackground().equals(Color.red)) {
            boolean dialogoCerrado = false;
            while (fallos < 3 && !dialogoCerrado) {
                dialogoCerrado = getCuentaBancaria();
            }

            fallos = 0;
        }

    }//GEN-LAST:event_btnTarjetaActionPerformed

    private void btnDineroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDineroActionPerformed
        switch (pantalla) {
            case 13:
                txfRetirar.setText("");
                contenedor.setSelectedIndex(10);
                pantalla = contenedor.getSelectedIndex();
                break;

        }
    }//GEN-LAST:event_btnDineroActionPerformed

    private void btnTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketActionPerformed
        if (lblTicket.getBackground().equals(Color.green)) {
            ticket = new Ticket(efectivo, operacion, tarjetaIngresada);
            ticket.setVisible(true);

            lblTicket.setBackground(Color.red);
        }
    }//GEN-LAST:event_btnTicketActionPerformed

    private void btnEspanolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEspanolActionPerformed
        idioma = new Idioma("Español");
        lblIntroduccion.setText(idioma.getProperty("lblIdioma"));
        if (lblTarjeta.getBackground().equals(Color.green)) {
            contenedor.setSelectedIndex(1);
            pantalla = contenedor.getSelectedIndex();
        }
    }//GEN-LAST:event_btnEspanolActionPerformed

    private void btnInglesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInglesActionPerformed
        idioma = new Idioma("Ingles");
        lblIntroduccion.setText(idioma.getProperty("lblIdioma"));
        if (lblTarjeta.getBackground().equals(Color.green)) {
            contenedor.setSelectedIndex(1);
            pantalla = contenedor.getSelectedIndex();
        }
    }//GEN-LAST:event_btnInglesActionPerformed

    private void btnInzquierda1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInzquierda1ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(0);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_btnInzquierda1ActionPerformed

    private void btnInzquierda2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInzquierda2ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(2);
                pantalla = contenedor.getSelectedIndex();
                operacion = Operacion.RE;
                break;
            case 2:
                efectivo = 20;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 3:
                efectivo = 20;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_btnInzquierda2ActionPerformed

    private void btnInzquierda3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInzquierda3ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(3);
                pantalla = contenedor.getSelectedIndex();
                operacion = Operacion.DE;
                break;
            case 2:
                efectivo = 75;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 3:
                efectivo = 75;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_btnInzquierda3ActionPerformed

    private void btnInzquierda4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInzquierda4ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(4);
                pantalla = contenedor.getSelectedIndex();
                passwd1.requestFocus();
                break;
            case 2:
                efectivo = 200;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 3:
                efectivo = 200;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_btnInzquierda4ActionPerformed

    private void btnDerecha1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDerecha1ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(5);
                pantalla = contenedor.getSelectedIndex();
                initMovimientos();
                break;

        }
    }//GEN-LAST:event_btnDerecha1ActionPerformed

    private void btnDerecha2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDerecha2ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(6);
                pantalla = contenedor.getSelectedIndex();
                operacion = Operacion.RT;
                initTransferencia();
                break;
            case 2:
                efectivo = 50;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 3:
                efectivo = 50;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_btnDerecha2ActionPerformed

    private void btnDerecha3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDerecha3ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(7);
                pantalla = contenedor.getSelectedIndex();
                operacion = Operacion.CME;
                break;
            case 2:
                efectivo = 100;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 3:
                efectivo = 100;
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_btnDerecha3ActionPerformed

    private void btnDerecha4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDerecha4ActionPerformed
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(8);
                pantalla = contenedor.getSelectedIndex();
                operacion = Operacion.PF;
                break;
            case 2:
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(12);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 3:
                pantallaAnterior = contenedor.getSelectedIndex();
                contenedor.setSelectedIndex(14);
                pantalla = contenedor.getSelectedIndex();
                break;

        }
    }//GEN-LAST:event_btnDerecha4ActionPerformed

    private void lblCancelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCancelMouseClicked
        switch (pantalla) {
            case 1:
                contenedor.setSelectedIndex(0);
                pantalla = contenedor.getSelectedIndex();
                lblTarjeta.setBackground(Color.red);
                break;
            case 2,3,4,5,6,7,8:
                contenedor.setSelectedIndex(1);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 9:
                contenedor.setSelectedIndex(pantallaAnterior);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 10:
                contenedor.setSelectedIndex(11);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 11:
                contenedor.setSelectedIndex(0);
                pantalla = contenedor.getSelectedIndex();
                lblTarjeta.setBackground(Color.red);
                //VACIAR TODOS LOS ELEMENTOS----------------------------------------------
                break;
            case 12:
                txfRetirar.setText("");
                contenedor.setSelectedIndex(2);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 14:
                txfIngresar.setText("");
                contenedor.setSelectedIndex(3);
                pantalla = contenedor.getSelectedIndex();
                break;
        }
    }//GEN-LAST:event_lblCancelMouseClicked

    private void lblEnterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEnterMouseClicked
        String contr1, contr2;
        switch (pantalla) {
            case 4:
                contr1 = String.valueOf(passwd1.getPassword());
                contr2 = String.valueOf(passwd1.getPassword());
                if (contr1.length() == 0) {
                    JOptionPane.showMessageDialog(this, "Por favor, introduzca una contraseña", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else if (contr1.length() < 4) {
                    JOptionPane.showMessageDialog(this, "La contraseña debe de tener 4 caracteres", "ERROR", JOptionPane.ERROR_MESSAGE);
                } else if (!contr1.equals(contr2)) {
                    JOptionPane.showMessageDialog(this, "Las contraseñas son diferentes, por favor intentenlo de nuevo", "ERROR", JOptionPane.ERROR_MESSAGE);
                    passwd1.setText("");
                    passwd2.setText("");
                } else {
                    contenedor.setSelectedIndex(9);
                    pantalla = contenedor.getSelectedIndex();
                }
                break;
            case 5:
                contenedor.setSelectedIndex(11);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 6:
                if (tablaTransferencia.getSelectedRow() != -1) {
                    ibanReceptor = (String) modeloTabla.getValueAt(tablaTransferencia.getSelectedRow(), 3);
                    contenedor.setSelectedIndex(15);
                    pantalla = contenedor.getSelectedIndex();
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, seleccione la cuenta a la que quiere realizar la transferencia", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 8:
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                efectivo = Integer.parseInt(txfFactura.getText());
                break;
            case 9:
                if (operacion == Operacion.RE) {
                    if (comprobarCantidad(efectivo)) {
                        insertarHistoricoOperacion();
                        contenedor.setSelectedIndex(13);
                        pantalla = contenedor.getSelectedIndex();
                    }

                } else if (operacion == Operacion.DE) {

                    if (comprobarCantidad(efectivo)) {
                        insertarHistoricoOperacion();
                        contenedor.setSelectedIndex(10);
                        pantalla = contenedor.getSelectedIndex();
                    }

                } else if (operacion == Operacion.RT) {
                    if (comprobarCantidad(efectivo)) {
                        insertarHistoricoOperacion();
                        contenedor.setSelectedIndex(10);
                        pantalla = contenedor.getSelectedIndex();
                    }

                } else if (operacion == Operacion.PF) {
                    if (comprobarCantidad(efectivo)) {
                        insertarHistoricoOperacion();
                        contenedor.setSelectedIndex(10);
                        pantalla = contenedor.getSelectedIndex();
                    }

                } else {
                    if (operacion == null) {
                        contenedor.setSelectedIndex(11);
                        pantalla = contenedor.getSelectedIndex();
                        contr1 = String.valueOf(passwd1.getPassword());
                        contr2 = String.valueOf(passwd1.getPassword());
                        cambiarContrasena(contr1, contr2);
                    } else {
                        contenedor.setSelectedIndex(10);
                        pantalla = contenedor.getSelectedIndex();
                    }

                }
                break;

            case 10:
                lblTicket.setBackground(Color.green);
                contenedor.setSelectedIndex(11);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 11:
                contenedor.setSelectedIndex(1);
                pantalla = contenedor.getSelectedIndex();
                break;
            case 12:
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                efectivo = Integer.parseInt(txfRetirar.getText());
                break;
            case 14:
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                efectivo = Integer.parseInt(txfIngresar.getText());
                break;
            case 15:
                contenedor.setSelectedIndex(9);
                pantalla = contenedor.getSelectedIndex();
                efectivo = Integer.parseInt(txfTransferencia.getText());
                break;

        }
    }//GEN-LAST:event_lblEnterMouseClicked

    private void lbl3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl3MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "3");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "3");
                    } else {
                        evt.consume();
                    }

                }
                break;

            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "3");
                } else {
                    evt.consume();
                }
                break;

            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "3");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "3");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "3");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl3MouseClicked

    private void lbl4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl4MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "4");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "4");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "4");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "4");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "4");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "4");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl4MouseClicked

    private void lbl5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl5MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "5");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "5");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "5");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "5");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "5");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "5");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl5MouseClicked

    private void lbl2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl2MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "2");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "2");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "2");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "2");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "2");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "2");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl2MouseClicked

    private void lbl6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl6MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "6");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "6");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "6");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "6");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "6");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "6");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl6MouseClicked

    private void lbl7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl7MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "7");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "7");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "7");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "7");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "7");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "7");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl7MouseClicked

    private void lbl8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl8MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "8");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "8");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "8");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "8");
                } else {
                    evt.consume();
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "8");
                } else {
                    evt.consume();
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "8");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl8MouseClicked

    private void lbl9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl9MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "9");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "9");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (factura.length() != 4) {
                    txfFactura.setText(factura + "9");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.length() != 4) {
                    txfRetirar.setText(retirar + "9");
                } else {
                    evt.consume();
                }
                break;
            case 14:

                String ingresar = txfIngresar.getText();
                if (ingresar.length() != 4) {
                    txfIngresar.setText(ingresar + "9");
                } else {
                    evt.consume();
                }

                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "9");
                } else {
                    evt.consume();
                }
                break;
        }
    }//GEN-LAST:event_lbl9MouseClicked

    private void lbl0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl0MouseClicked
        switch (pantalla) {
            case 4:
                String contr1 = String.valueOf(passwd1.getPassword());
                String contr2 = String.valueOf(passwd2.getPassword());
                if (contr1.length() != 4) {
                    passwd1.setText(contr1 + "0");
                } else {
                    if (contr2.length() != 4) {
                        passwd2.setText(contr2 + "0");
                    } else {
                        evt.consume();
                    }

                }
                break;
            case 8:
                String factura = txfFactura.getText();
                if (!factura.equals("") && factura.length() != 4) {
                    txfFactura.setText(factura + "0");
                } else {
                    evt.consume();
                }
                break;
            case 12:
                String retirar = txfRetirar.getText();
                if (retirar.equals("") || retirar.length() == 4) {
                    evt.consume();
                } else {
                    txfRetirar.setText(retirar + "0");
                }
                break;
            case 14:
                String ingresar = txfIngresar.getText();
                if (ingresar.equals("") || ingresar.length() == 4) {
                    evt.consume();
                } else {
                    txfIngresar.setText(ingresar + "0");
                }
                break;
            case 15:
                String transferir = txfTransferencia.getText();
                if (transferir.length() != 4) {
                    txfTransferencia.setText(transferir + "0");
                } else {
                    evt.consume();
                }
                break;

        }
    }//GEN-LAST:event_lbl0MouseClicked

    private void cambiarContrasena(String contra1, String contra2) {
        try {
            sentencia = conexion.createStatement();
            String updateContra = "update tarjeta_bancaria set pin = " + Integer.parseInt(contra2) + " where numero_tarjeta = \"" + tarjetaIngresada + "\";";
            sentencia.executeUpdate(updateContra);
        } catch (SQLException ex) {
            Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Para el resto de operaciones meter tmb el tipo de operacion --> para saber el limite legal
    private boolean comprobarCantidad(int cantidad) {
        if (operacion == Operacion.RT) {
            if (cantidad > MAX_TRANSFERENCIA) {
                JOptionPane.showMessageDialog(this, "La cantidad a retirar es mayor que el limite de seguridad", "ERROR", JOptionPane.ERROR_MESSAGE);
                contenedor.setSelectedIndex(15);
                pantalla = contenedor.getSelectedIndex();
                txfTransferencia.setText("");
                return false;
            } else {
                try {
                    String selectSaldo = "select saldo from cuenta_bancaria where iban =\"" + ibanRegistrado + "\";";
                    resultado = sentencia.executeQuery(selectSaldo);
                    resultado.next();
                    double saldo = resultado.getDouble("saldo");
                    if (cantidad > saldo) {
                        JOptionPane.showMessageDialog(this, "La cantidad a transferir es mayor que el saldo de su cuenta", "ERROR", JOptionPane.ERROR_MESSAGE);
                        contenedor.setSelectedIndex(15);
                        pantalla = contenedor.getSelectedIndex();
                        txfTransferencia.setText("");
                        return false;
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else if (operacion == Operacion.PF) {
            String selectSaldo = "select saldo from cuenta_bancaria where iban =\"" + ibanRegistrado + "\";";
            double saldo = 0;
            try {
                resultado = sentencia.executeQuery(selectSaldo);
                resultado.next();
                saldo = resultado.getDouble("saldo");
            } catch (SQLException ex) {
                Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (cantidad > saldo) {
                JOptionPane.showMessageDialog(this, "No hay saldo suficiente en la cuenta", "ERROR", JOptionPane.ERROR_MESSAGE);
                contenedor.setSelectedIndex(8);
                pantalla = contenedor.getSelectedIndex();
                txfFactura.setText("");
                return false;
            } else if (cantidad > MAX_CAJERO) {
                JOptionPane.showMessageDialog(this, "No se puede pagar facturas de más de: " + MAX_CAJERO, "ERROR", JOptionPane.ERROR_MESSAGE);
                contenedor.setSelectedIndex(8);
                pantalla = contenedor.getSelectedIndex();
                txfFactura.setText("");
                return false;
            }
        }
        if (cantidad % 5 != 0) {
            JOptionPane.showMessageDialog(this, "La cantidad debe de ser multiplo de 5", "ERROR", JOptionPane.ERROR_MESSAGE);
            contenedor.setSelectedIndex(12);
            pantalla = contenedor.getSelectedIndex();
            txfRetirar.setText("");
            return false;
        } else if (cantidad > MAX_INGRESO_RETIRAR) {
            if (operacion == Operacion.RE) {
                JOptionPane.showMessageDialog(this, "La cantidad a retirar es mayor que el limite de seguridad", "ERROR", JOptionPane.ERROR_MESSAGE);
                contenedor.setSelectedIndex(12);
                pantalla = contenedor.getSelectedIndex();
                txfRetirar.setText("");
                return false;
            } else if (operacion == Operacion.DE) {
                JOptionPane.showMessageDialog(this, "La cantidad a ingresar es mayor que el limite de seguridad", "ERROR", JOptionPane.ERROR_MESSAGE);
                contenedor.setSelectedIndex(14);
                pantalla = contenedor.getSelectedIndex();
                txfIngresar.setText("");
                return false;
            }

        } else {
            try {
                sentencia = conexion.createStatement();
                String selectSaldo = "select saldo from cuenta_bancaria where iban =\"" + ibanRegistrado + "\";";
                resultado = sentencia.executeQuery(selectSaldo);
                resultado.next();
                double saldo = resultado.getDouble("saldo");
                if (cantidad > saldo) {
                    JOptionPane.showMessageDialog(this, "La cantidad a retirar es mayor que el saldo de su cuenta", "ERROR", JOptionPane.ERROR_MESSAGE);
                    contenedor.setSelectedIndex(12);
                    pantalla = contenedor.getSelectedIndex();
                    txfRetirar.setText("");
                    return false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return true;
    }

    private void insertarHistoricoOperacion() {
        String selectDni = "select dniCliente from cuenta_bancaria where iban=\"" + ibanRegistrado + "\";";
        try {
            sentencia = conexion.createStatement();
            resultado = sentencia.executeQuery(selectDni);
            if (resultado.next()) {
                String dni = resultado.getString("dniCliente");
                LocalDateTime ldtFecha = LocalDateTime.now();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String fecha = ldtFecha.format(formato);
                int idOperacion = operacion.getIdentificador();
                //Comprobamos si la operacion es una transferencia o no:
                String insert;
                if (operacion == Operacion.RT) {
                    insert = "insert into historico_operacion (fecha_operacion, saldo_operacion, dniCliente, idOperacion, ibanEmisor, ibanReceptor)"
                            + " values ('" + fecha + "', " + efectivo + ", \"" + dni + "\", " + idOperacion + ", \"" + ibanRegistrado + "\", \"" + ibanReceptor + "\");";
                    sentencia.executeUpdate(insert);
                }else{
                    insert = "insert into historico_operacion (fecha_operacion, saldo_operacion, dniCliente, idOperacion, ibanEmisor, ibanReceptor)"
                            + " values ('" + fecha + "', " + efectivo + ", \"" + dni + "\", " + idOperacion + ", \"" + ibanRegistrado + "\", NULL);";
                    sentencia.executeUpdate(insert);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean getCuentaBancaria() {
        JTextField numeroTajeta = new JTextField();
        JPasswordField contrasena = new JPasswordField();
        Object[] campos = {"Número de la tarjeta:", numeroTajeta, "Número pin:", contrasena};
        int respuesta = JOptionPane.showConfirmDialog(this, campos, "Login", JOptionPane.OK_CANCEL_OPTION);
        String numTarjeta = numeroTajeta.getText().trim();
        String contr = String.valueOf(contrasena.getPassword());

        //Si falta por meter alguna credencial:
        if (numTarjeta.equals("") && !contr.equals("") && respuesta == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Por favor, introduzca su numero de tarjeta", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (!numTarjeta.equals("") && contr.equals("") && respuesta == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Por favor, introduzca su contraseña", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (numTarjeta.equals("") && contr.equals("") && respuesta == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(this, "Por favor, introduzca sus credenciales", "ERROR", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!numTarjeta.equals("") && !contr.equals("") && respuesta == JOptionPane.OK_OPTION) {
            //En caso de que haya introducido todas sus credenciales:
            try {
                sentencia = conexion.createStatement();
                String consultaGetIban = "SELECT numero_tarjeta FROM tarjeta_bancaria where numero_tarjeta =\"" + numTarjeta + "\";";
                resultado = sentencia.executeQuery(consultaGetIban);
                //Comprobamos si el numero de tarjeta existe
                if (!resultado.next()) {
                    JOptionPane.showMessageDialog(this, "Número de tarjeta incorrecto", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return false;
                } else {
                    //Comprobamos que no esta bloqueada
                    String consultaGetBloqueada = "SELECT bloqueada FROM tarjeta_bancaria where numero_tarjeta = \"" + numTarjeta + "\";";
                    resultado = sentencia.executeQuery(consultaGetBloqueada);
                    resultado.next();
                    int bloqueada = resultado.getInt("bloqueada");
                    if (bloqueada == 1) {
                        JOptionPane.showMessageDialog(this, "Tarjeta bloqueada, contacte con un administrador para su desbloqueo", "ERROR", JOptionPane.ERROR_MESSAGE);
                        return false;
                    } else {
                        //En caso de que exista y no este bloqueada
                        String consultaGetPin = "select pin from tarjeta_bancaria where numero_tarjeta = \"" + numTarjeta + "\" and pin = \"" + contr + "\";";
                        resultado = sentencia.executeQuery(consultaGetPin);
                        //Si no encuentra el iban correspondiente a la contraseña introducida:
                        if (!resultado.next()) {
                            fallos++;
                            //Si ha gastado los 3 intentos se bloquea
                            if (fallos == 3) {
                                JOptionPane.showMessageDialog(this, "Su tarjeta ha sido bloqueada debido a razones de seguridad", "ERROR", JOptionPane.ERROR_MESSAGE);
                                String bloquear = "update tarjeta_bancaria set bloqueada = 1 where numero_tarjeta = \"" + numTarjeta + "\";";
                                sentencia.executeUpdate(bloquear);
                                //En caso contrario se da otra oportunidad
                            } else {
                                JOptionPane.showMessageDialog(this, "Contraseña incorrecta. Intentos restantes: " + (NUM_INTENTOS - fallos), "ERROR", JOptionPane.ERROR_MESSAGE);
                                numeroTajeta.setText(numTarjeta);
                            }
                            return false;
                            //Si la contraseña corresponde al iban introducido:
                        } else {
                            //Comprobamos que la cuenta bancaria no este bloqueada:
                            String selectBloquearIban = "select cb.bloqueada from cuenta_bancaria cb join tarjeta_bancaria tb using(iban) where numero_tarjeta=\"" + numTarjeta + "\";";
                            resultado = sentencia.executeQuery(selectBloquearIban);
                            resultado.next();
                            int bloqueadaIban = resultado.getInt("cb.bloqueada");
                            if (bloqueadaIban == 1) {
                                JOptionPane.showMessageDialog(this, "Cuenta bancaria bloqueada, contacte con un administrador para su desbloqueo", "ERROR", JOptionPane.ERROR_MESSAGE);
                                return false;
                            } else {
                                String consultaGetIbanRegistrado = "select iban from tarjeta_bancaria where numero_tarjeta = \"" + numTarjeta + "\" and pin = \"" + contr + "\";";
                                resultado = sentencia.executeQuery(consultaGetIbanRegistrado);
                                resultado.next();
                                ibanRegistrado = resultado.getString("iban");
                                tarjetaIngresada = numTarjeta;
                                lblTarjeta.setBackground(Color.green);
                                lblTicket.setBackground(Color.red);
                                contenedor.setSelectedIndex(1);
                                pantalla = contenedor.getSelectedIndex();
                                return true;
                            }
                            //En caso contrario accedemos:

                        }
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(VentanaATM.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return respuesta != JOptionPane.CLOSED_OPTION || respuesta != JOptionPane.CANCEL_OPTION;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaATM.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaATM.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaATM.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaATM.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaATM().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDerecha1;
    private javax.swing.JButton btnDerecha2;
    private javax.swing.JButton btnDerecha3;
    private javax.swing.JButton btnDerecha4;
    private javax.swing.JButton btnDinero;
    private javax.swing.JButton btnEspanol;
    private javax.swing.JButton btnIngles;
    private javax.swing.JButton btnInzquierda1;
    private javax.swing.JButton btnInzquierda2;
    private javax.swing.JButton btnInzquierda3;
    private javax.swing.JButton btnInzquierda4;
    private javax.swing.JButton btnTarjeta;
    private javax.swing.JButton btnTicket;
    private javax.swing.JPanel cambiarMoneda;
    private javax.swing.JPanel cambiarPIN;
    private javax.swing.JPanel confirmarOperacion;
    private javax.swing.JPanel confirmarTicket;
    private javax.swing.JPanel consultarAgenda;
    private javax.swing.JTabbedPane contenedor;
    private javax.swing.JPanel depositarSaldo;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl0;
    private javax.swing.JLabel lbl1;
    private javax.swing.JLabel lbl2;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lbl9;
    private javax.swing.JLabel lblAgenda;
    private javax.swing.JLabel lblBtones;
    private javax.swing.JLabel lblCambiarPIN;
    private javax.swing.JLabel lblCancel;
    private javax.swing.JLabel lblClear;
    private javax.swing.JLabel lblDepositar;
    private javax.swing.JLabel lblEnter;
    private javax.swing.JLabel lblFactura;
    private javax.swing.JLabel lblIdioma;
    private javax.swing.JLabel lblIntroduccion;
    private javax.swing.JLabel lblMoneda;
    private javax.swing.JLabel lblRetirar;
    private javax.swing.JLabel lblTarjeta;
    private javax.swing.JLabel lblTicket;
    private javax.swing.JLabel lblTransferencia;
    private javax.swing.JPanel menuPrincipal;
    private javax.swing.JPanel pagarFactura;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelIdioma;
    private javax.swing.JPasswordField passwd1;
    private javax.swing.JPasswordField passwd2;
    private javax.swing.JPanel realizarTransferencia;
    private javax.swing.JPanel retirarSaldo;
    private javax.swing.JTable tablaMovimientos;
    private javax.swing.JTable tablaTransferencia;
    private javax.swing.JPanel tecladoPrincipal;
    private javax.swing.JTextField txfFactura;
    private javax.swing.JTextField txfIngresar;
    private javax.swing.JTextField txfRetirar;
    private javax.swing.JTextField txfSaldoTotal;
    private javax.swing.JTextField txfTransferencia;
    // End of variables declaration//GEN-END:variables
    Connection conexion;
    Statement sentencia;
    ResultSet resultado;
    private String ibanRegistrado;
    private String ibanReceptor = null;
    private String tarjetaIngresada;
    private int fallos;
    private Timer reloj;
    Idioma idioma;
    int pantalla;
    int pantallaAnterior;
    int efectivo;
    Operacion operacion;
    Ticket ticket;
    private DefaultTableModel modeloTabla;
}
