/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Alberto
 */
public class DevolucionFranco extends JFrame{
    public DevolucionFranco(int cantidad) {
        setTitle("Devolución del cajero");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 5, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(1500, 1500));
        List<JLabel> billeteLabels = new ArrayList<>();
        int billete500 = 0;
        int billete200 = 0;
        int billete100 = 0;
        int billete50 = 0;
        int billete20 = 0;
        

        if (cantidad >= 500) {
            billete500 = (int) (cantidad / 500);
            cantidad %= 500;
        }

        if (cantidad >= 200) {
            billete200 = (int) (cantidad / 200);
            cantidad %= 200;
        }

        if (cantidad >= 100) {
            billete100 = (int) (cantidad / 100);
            cantidad %= 100;
        }

        if (cantidad >= 50) {
            billete50 = (int) (cantidad / 50);
            cantidad %= 50;
        }
         if (cantidad >= 20) {
            billete20 = (int) (cantidad / 20);
            cantidad %= 20;
        }
        
        for (int i = 0; i < billete500; i++) {
            ImageIcon billete500Icon = new ImageIcon("./src/ATM_Images/500Franco.png");
            JLabel billete500Label = new JLabel(new ImageIcon(billete500Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete500Label);
        }

        for (int i = 0; i < billete200; i++) {
            ImageIcon billete200Icon = new ImageIcon("./src/ATM_Images/200EFranco.png");
            JLabel billete200Label = new JLabel(new ImageIcon(billete200Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete200Label);
        }

        for (int i = 0; i < billete100; i++) {
            ImageIcon billete100Icon = new ImageIcon("./src/ATM_Images/100Franco.png");
            JLabel billete100Label = new JLabel(new ImageIcon(billete100Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete100Label);
        }

        for (int i = 0; i < billete50; i++) {
            ImageIcon billete50Icon = new ImageIcon("./src/ATM_Images/50Franco.png"); // Ruta de la imagen del billete de 5
            JLabel billete50Label = new JLabel(new ImageIcon(billete50Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete50Label);
        }
        
        for (int i = 0; i < billete20; i++) {
            ImageIcon billete20Icon = new ImageIcon("./src/ATM_Images/20Franco.png"); // Ruta de la imagen del billete de 5
            JLabel billete20Label = new JLabel(new ImageIcon(billete20Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete20Label);
        }

        for (JLabel billeteLabel : billeteLabels) {
            panel.add(billeteLabel);
     
        }

        add(panel);

        pack();
        setLocationRelativeTo(null);
        
    }

    public static void main(String[] args) {
        int cantidad = 0;
        new DevolucionFranco(cantidad).setVisible(true);
    }
}
