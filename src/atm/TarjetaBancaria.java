/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

import java.util.Objects;

/**
 *
 * @author Alberto
 */
public class TarjetaBancaria {
    private String numeroTarjeta;
    private int pin;
    private boolean bloqueada;
    private String dniCliente;
    private String iban;
    private int numeroAdministrador;

    public TarjetaBancaria(String numeroTarjeta, int pin, boolean bloqueada, String dniCliente, String iban, int numeroAdministrador) {
        this.numeroTarjeta = numeroTarjeta;
        this.pin = pin;
        this.bloqueada = bloqueada;
        this.dniCliente = dniCliente;
        this.iban = iban;
        this.numeroAdministrador = numeroAdministrador;
    }

    public TarjetaBancaria(String dniCliente, String numeroTarjeta, int pin, String iban) {
        this.dniCliente = dniCliente;
        this.numeroTarjeta = numeroTarjeta;
        this.pin = pin;
        this.iban = iban;
    }

    
    
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public int getPin() {
        return pin;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public String getIban() {
        return iban;
    }

    public int getNumeroAdministrador() {
        return numeroAdministrador;
    }

    public void setNumeroAdministrador(int numeroAdministrador) {
        this.numeroAdministrador = numeroAdministrador;
    }

    public void setBloqueada(boolean bloqueada) {
        this.bloqueada = bloqueada;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.numeroTarjeta);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TarjetaBancaria other = (TarjetaBancaria) obj;
        return Objects.equals(this.numeroTarjeta, other.numeroTarjeta);
    }

    @Override
    public String toString() {
        return numeroTarjeta;
    }

    
    
}
