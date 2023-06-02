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
public class Cliente implements Comparable<Cliente>{
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private int numeroAdministrador;

    public Cliente(String dni, String nombre, String apellido1, String apellido2, int numeroAdministrador) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.numeroAdministrador = numeroAdministrador;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public int getNumeroAdministrador() {
        return numeroAdministrador;
    }

    public void setNumeroAdministrador(int numeroAdministrador) {
        this.numeroAdministrador = numeroAdministrador;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.dni);
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
        final Cliente other = (Cliente) obj;
        return Objects.equals(this.dni, other.dni);
    }

    @Override
    public String toString() {
        return nombre + " " + apellido1 + " " + apellido2;
    }

    @Override
    public int compareTo(Cliente o) {
        return this.dni.compareToIgnoreCase(o.getDni());
    }
    
    
}
