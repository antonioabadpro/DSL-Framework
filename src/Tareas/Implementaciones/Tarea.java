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
public abstract class Tarea
{
    private ArrayList<Slot> slotEntradas;
    private ArrayList<Slot> slotSalidas;
    private TipoTarea tipo;

    public Tarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas, TipoTarea tipo)
    {
        this.slotEntradas = slotEntradas;
        this.slotSalidas = slotSalidas;
        this.tipo = tipo;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    public ArrayList<Slot> getEntradas()
    {
        return slotEntradas;
    }

    public void setEntradas(ArrayList<Slot> entradas)
    {
        this.slotEntradas = entradas;
    }

    public ArrayList<Slot> getSalidas()
    {
        return slotSalidas;
    }

    public void setSalidas(ArrayList<Slot> salidas)
    {
        this.slotSalidas = salidas;
    }

    public TipoTarea getTipo()
    {
        return tipo;
    }

    public void setTipo(TipoTarea tipo)
    {
        this.tipo = tipo;
    }
    // </editor-fold>

    public abstract void ejecutar();
}