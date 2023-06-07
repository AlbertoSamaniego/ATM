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
    
    EURO("Euro",1),
    DOLAR("Dólar Americano", 1.07),
    LIBRA("Libra Esterlina", 0.87),
    YEN("Yen Japonés", 150.36),
    FRANCO("Franco Suizo", 0.97);
    
    private String nombre;
    private double valor;

    /**
     * Constructor de enum MonedaExtranjera
     * @param nombre nombre de la moneda
     * @param valor valor de la moneda en Euros
     */
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
