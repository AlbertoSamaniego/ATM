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
public class DevolucionDolar extends JFrame{
     public DevolucionDolar(int cantidad) {
        setTitle("Devolución del cajero");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 5, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(1500, 1500));
        List<JLabel> billeteLabels = new ArrayList<>();
        int billete100 = 0;
        int billete50 = 0;
        int billete20 = 0;
        int billete10 = 0;
        int billete5 = 0;
        int billete2= 0;
        int billete1 = 0;

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

        if (cantidad >= 10) {
            billete10 = (int) (cantidad / 10);
            cantidad %= 10;
        }

        if (cantidad >= 5) {
            billete5 = (int) (cantidad / 5);
            cantidad %= 5;
        }
        
        if (cantidad >= 2) {
            billete5 = (int) (cantidad / 2);
            cantidad %= 2;
        }
        
        if (cantidad >= 1) {
            billete5 = (int) (cantidad / 1);
            cantidad %= 1;
        }
        
        for (int i = 0; i < billete100; i++) {
            ImageIcon billete100Icon = new ImageIcon("./src/ATM_Images/100Dolar.png");
            JLabel billete100Label = new JLabel(new ImageIcon(billete100Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete100Label);
        }
        
        for (int i = 0; i < billete50; i++) {
            ImageIcon billete50Icon = new ImageIcon("./src/ATM_Images/50Dolar.png");
            JLabel billete50Label = new JLabel(new ImageIcon(billete50Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete50Label);
        }

        for (int i = 0; i < billete20; i++) {
            ImageIcon billete20Icon = new ImageIcon("./src/ATM_Images/20Dolar.png");
            JLabel billete20Label = new JLabel(new ImageIcon(billete20Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete20Label);
        }

        for (int i = 0; i < billete10; i++) {
            ImageIcon billete10Icon = new ImageIcon("./src/ATM_Images/10Dolar.png");
            JLabel billete10Label = new JLabel(new ImageIcon(billete10Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete10Label);
        }

        for (int i = 0; i < billete5; i++) {
            ImageIcon billete5Icon = new ImageIcon("./src/ATM_Images/5Dolar.png");
            JLabel billete5Label = new JLabel(new ImageIcon(billete5Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete5Label);
        }
        
        for (int i = 0; i < billete2; i++) {
            ImageIcon billete2Icon = new ImageIcon("./src/ATM_Images/2Dolar.png"); // Ruta de la imagen del billete de 5
            JLabel billete2Label = new JLabel(new ImageIcon(billete2Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete2Label);
        }
        
        for (int i = 0; i < billete1; i++) {
            ImageIcon billete1Icon = new ImageIcon("./src/ATM_Images/1Dolar.png"); // Ruta de la imagen del billete de 5
            JLabel billete1Label = new JLabel(new ImageIcon(billete1Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete1Label);
        }

        for (JLabel billeteLabel : billeteLabels) {
            panel.add(billeteLabel);
     
        }

        add(panel);

        //para que la ventana no salga sin contenido (solamente con la cabecera)
        pack();
        //para que la ventana salga en el centro de la pantalla
        setLocationRelativeTo(null);
        
    }

    public static void main(String[] args) {
        int cantidad = 0;
        new DevolucionDolar(cantidad).setVisible(true);
    }
}
