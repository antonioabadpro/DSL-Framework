/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;

/**
 *
 * @author AAHG-PORTATIL
 * Entradas = 1
 * Salidas = n
 */
public class Replicator
{
    private ArrayList<Slot> slotSalidas;
    private Slot slotEntrada;
    
    public Replicator(Slot slotEntrada, ArrayList<Slot> slotSalidas)
    {
        this.slotEntrada=slotEntrada;
        
        for(Slot s: slotSalidas)
        {
            this.slotSalidas.add(s);
        }
    }
    
    public void ejecutar() throws Exception
    {
        
    }
}
