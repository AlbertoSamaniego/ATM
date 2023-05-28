/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

/**
 *
 * @author Alberto
 */
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket extends JFrame {
    
    public Ticket(int efectivo, Operacion operacion, String tarjeta) {
        setTitle("Ticket obtenido");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocation(1400,350);
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Panel de encabezado
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.LIGHT_GRAY);
        JLabel titleLabel = new JLabel("BANCO GALILEO");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel);
        
        // Panel de contenido
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(5, 2));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        LocalDateTime ldtFecha = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String fecha= ldtFecha.format(formato);
        // Agregar elementos al panel de contenido
        contentPanel.add(new JLabel("Fecha de la operacion: "));
        contentPanel.add(new JLabel(fecha));
        contentPanel.add(new JLabel("Tipo de transaccion:"));
        contentPanel.add(new JLabel(operacion.getNombre()));
        contentPanel.add(new JLabel("Importe en euros"));
        contentPanel.add(new JLabel(String.valueOf(efectivo)));
        contentPanel.add(new JLabel("Numero de la tarjeta: "));
        contentPanel.add(new JLabel(String.valueOf(tarjeta)));
        
        // Agregar paneles al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Agregar panel principal a la ventana
        add(mainPanel);
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Operacion operacion = null;
                int efectivo = 0;
                String tarjeta = "";
                new Ticket(efectivo, operacion, tarjeta).setVisible(true);
            }
        });
    }

   
}
