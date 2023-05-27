/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package atm;

/**
 *
 * @author Alberto
 */
public enum Operacion {
    RE("Retirar efectivo",1),
    DE("Depositar efectivo",2),
    RT("Realizar transferencia",3),
    CME("Cambiar a moneda extranjera",4),
    PF("Pagar factura",5);
    
    private String nombre;
    private int identificador;
    
    private Operacion(String nombre, int identificador){
        this.nombre = nombre;
        this.identificador = identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public int getIdentificador() {
        return identificador;
    }
    
}
