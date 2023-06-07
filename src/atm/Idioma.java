/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Idioma extends Properties{

public Idioma(String idioma){

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
    try (FileInputStream input = new FileInputStream(new File(System.getProperty("user.dir")+"\\src\\atm\\"+idioma))){
        this.load(input);
    } catch (IOException ex) {

    }
}
}  
