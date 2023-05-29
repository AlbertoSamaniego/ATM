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
public class DevolucionYen extends JFrame{
    public DevolucionYen(int cantidad) {
        setTitle("Devolución del cajero");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 5, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setPreferredSize(new Dimension(1500, 1500));
        List<JLabel> billeteLabels = new ArrayList<>();
        int billete10000 = 0;
        int billete5000 = 0;
        int billete2000 = 0;
        int billete1000 = 0;

        if (cantidad >= 10000) {
            billete10000 = (int) (cantidad / 10000);
            cantidad %= 10000;
        }

        if (cantidad >= 5000) {
            billete5000 = (int) (cantidad / 5000);
            cantidad %= 5000;
        }

        if (cantidad >= 2000) {
            billete2000 = (int) (cantidad / 2000);
            cantidad %= 2000;
        }

        if (cantidad >= 1000) {
            billete1000 = (int) (cantidad /1000);
            cantidad %= 1000;
        }
        
        for (int i = 0; i < billete10000; i++) {
            ImageIcon billete10000Icon = new ImageIcon("./src/ATM_Images/10000Yen.png");
            JLabel billete10000Label = new JLabel(new ImageIcon(billete10000Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete10000Label);
        }

        for (int i = 0; i < billete5000; i++) {
            ImageIcon billete5000Icon = new ImageIcon("./src/ATM_Images/5000Yen.png");
            JLabel billete5000Label = new JLabel(new ImageIcon(billete5000Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete5000Label);
        }

        for (int i = 0; i < billete2000; i++) {
            ImageIcon billete2000Icon = new ImageIcon("./src/ATM_Images/2000Yen.png");
            JLabel billete2000Label = new JLabel(new ImageIcon(billete2000Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete2000Label);
        }

        for (int i = 0; i < billete1000; i++) {
            ImageIcon billete1000Icon = new ImageIcon("./src/ATM_Images/1000Yen.png");
            JLabel billete1000Label = new JLabel(new ImageIcon(billete1000Icon.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            billeteLabels.add(billete1000Label);
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
        new DevolucionYen(cantidad).setVisible(true);
    }
}
