/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package atm;

/**
 *
 * @author Alberto
 */
public enum MonedaExtranjera {
    
    DOLAR("Dólar Americano", 0.93),
    LIBRA("Libra Esterlina", 1.15),
    YEN("Yen Japonés", 0.0067),
    FRANCO("Franco Suizo", 1.05);
    
    private String nombre;
    private double valor;

    private MonedaExtranjera(String nombre, double valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    public String getNombre() {
        return nombre;
    }

    public double getValor() {
        return valor;
    }
    
}
