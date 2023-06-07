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
public class CuentaBancaria {
    private String iban;
    private double saldo;
    private boolean bloqueada;
    private String dniCliente;
    private int numeroAdministrador;

    /**
     * Primer constructor de la clase CuentaBancaria
     * @param iban
     * @param saldo
     * @param bloqueada
     * @param dniCliente
     * @param numeroAdministrador 
     */
    public CuentaBancaria(String iban, double saldo, boolean bloqueada, String dniCliente, int numeroAdministrador) {
        this.iban = iban;
        this.saldo = saldo;
        this.bloqueada = bloqueada;
        this.dniCliente = dniCliente;
        this.numeroAdministrador = numeroAdministrador;
    }

    /**
     * Segundo constructor de la clase CuentaBancaria
     * @param iban
     * @param dniCliente 
     */
    public CuentaBancaria(String iban, String dniCliente) {
        this.iban = iban;
        this.dniCliente = dniCliente;
    }

    public String getIban() {
        return iban;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public String getDniCliente() {
        return dniCliente;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.iban);
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
        final CuentaBancaria other = (CuentaBancaria) obj;
        return Objects.equals(this.iban.toUpperCase(), other.iban.toUpperCase());
    }
}
