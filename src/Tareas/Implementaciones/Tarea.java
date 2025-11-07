/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;

/**
 *
 * @author agustinrodriguez
 */
public abstract class Tarea {
    
    private ArrayList<Slot> entradas;
    private ArrayList<Slot> salidas;
    private TipoTarea tipo;
    
    public abstract void ejecutar();

    public ArrayList<Slot> getEntradas() {
        return entradas;
    }

    public void setEntradas(ArrayList<Slot> entradas) {
        this.entradas = entradas;
    }

    public ArrayList<Slot> getSalidas() {
        return salidas;
    }

    public void setSalidas(ArrayList<Slot> salidas) {
        this.salidas = salidas;
    }

    public TipoTarea getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarea tipo) {
        this.tipo = tipo;
    }    
}
