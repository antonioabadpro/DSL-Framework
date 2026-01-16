package Tareas;

import Slots.Slot;
import java.util.ArrayList;

// AHORA IMPLEMENTA RUNNABLE
public abstract class Tarea implements Runnable {

    protected ArrayList<Slot> listaEntradas;
    protected ArrayList<Slot> listaSalidas;
    protected Tipo tipoTarea;

    // Bandera para poder apagar el hilo suavemente si hace falta
    protected volatile boolean activo = true;

    public Tarea(ArrayList<Slot> entradas, ArrayList<Slot> salidas, Tipo tipoTarea) {
        this.listaEntradas = entradas;
        this.listaSalidas = salidas;
        this.tipoTarea = tipoTarea;
    }

    // Este método es el corazón del Hilo. Lo llamará Thread.start()
    @Override
    public void run() {
        // Bucle infinito: la tarea siempre está viva esperando mensajes en el Slot
        while (activo) {
            procesarMensaje();
        }
    }

    // ELIMINAMOS 'ejecutar()' y creamos 'procesarMensaje()'
    // Este método debe hacer SOLO UNA unidad de trabajo (leer 1 mensaje -> procesar -> escribir)
    public abstract void procesarMensaje();

    public void detener() {
        this.activo = false;
    }

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
