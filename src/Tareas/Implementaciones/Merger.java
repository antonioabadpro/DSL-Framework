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
 * Entradas = n
 * Salidas = 1
 */
public class Merger
{
    private ArrayList<Slot> slotEntradas;
    private Slot slotSalida;
    
    public Merger(ArrayList<Slot> slotEntradas, Slot slotSalida)
    {
        for(Slot s: slotEntradas)
        {
            this.slotEntradas.add(s);
        }
        
        this.slotSalida=slotSalida;
    }
    
    public void ejecutar() throws Exception
    {
        // Obtenemos el numero de mensajes que quieren escribir en cada Slot de Entrada
        for(Slot s: this.slotEntradas)
        {
            int numMsjEntrada=s.getQueue().size();
            
            for(int i=0; i<numMsjEntrada; i++)
            {
                this.slotSalida.escribir(s.leer());
            }
        }
    }
    
}
