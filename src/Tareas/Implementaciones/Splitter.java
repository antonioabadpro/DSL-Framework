/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author agustinrodriguez
 */
public class Splitter extends Tarea {

    private Slot entrada;
    private Slot salida;
    private NodeList nodos;
    
    public Splitter(Slot entrada, Slot salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    public Slot getEntrada() {
        return entrada;
    }

    public void setEntrada(Slot entrada) {
        this.entrada = entrada;
    }

    public Slot getSalida() {
        return salida;
    }

    public void setSalida(Slot salida) {
        this.salida = salida;
    }

    @Override
    public void ejecutar() {

    }
        
    
    
}
