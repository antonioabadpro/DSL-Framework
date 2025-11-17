package Tareas;

import Slots.Slot;
import java.util.ArrayList;

/**
 * Clase Abstracta que contiene los metodos que deben implementar todas las Tareas de nuestro Framework/Libreria
 */
public abstract class Tarea {
    
    // Atributos
    protected ArrayList<Slot> listaEntradas;
    protected ArrayList<Slot> listaSalidas;
    protected Tipo tipoTarea;
    
    // Constructor
    public Tarea(ArrayList<Slot> entradas, ArrayList<Slot> salidas, Tipo tipoTarea) {
        this.listaEntradas = entradas;
        this.listaSalidas = salidas;
        this.tipoTarea = tipoTarea;
    }
    
    // Metodos Abstractos
    public abstract void ejecutar();
    
    // Getters y Setters
    public ArrayList<Slot> getListaEntradas() {
        return listaEntradas;
    }

    public void setListaEntradas(ArrayList<Slot> listaEntradas) {
        this.listaEntradas = listaEntradas;
    }

    public ArrayList<Slot> getListaSalidas() {
        return listaSalidas;
    }

    public void setListaSalidas(ArrayList<Slot> listaSalidas) {
        this.listaSalidas = listaSalidas;
    }

    public Tipo getTipoTarea() {
        return tipoTarea;
    }    
    
}
