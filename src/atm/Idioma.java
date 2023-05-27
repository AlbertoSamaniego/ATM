/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

import java.io.IOException;
import java.util.Properties;

public class Idioma extends Properties{

private static final long serialVersionUID = 1L;

public Idioma(String idioma){

    //Modificar si quieres añadir mas idiomas
    //Cambia el nombre de los ficheros o añade los necesarios
    switch(idioma){
        case "Español":
                getProperties("espanol.properties");
                break;
        case "Ingles":
                getProperties("ingles.properties");
                break;
        default:
                getProperties("espanol.properties");
    }

}

private void getProperties(String idioma) {
    try {
        this.load( getClass().getResourceAsStream(idioma) );
    } catch (IOException ex) {

    }
}
}  
