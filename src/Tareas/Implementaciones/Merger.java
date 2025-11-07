/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author AAHG-PORTATIL
 * Entradas = n
 * Salidas = 1
 */
public class Merger extends Tarea
{
    private ArrayList<Slot> listaSlotsEntrada;
    private Slot slotSalida;
    
    public Merger(ArrayList<Slot> listaSlotsEntrada, Slot slotSalida, TipoTarea tipo)
    {
        super(tipo);
        for(Slot s: listaSlotsEntrada)
        {
            this.listaSlotsEntrada.add(s);
        }
        
        this.slotSalida=slotSalida;
    }
    
    public ArrayList<Slot> getSlotEntradas()
    {
        return this.listaSlotsEntrada;
    }

    public Slot getSlotSalida()
    {
        return this.slotSalida;
    }

    public void setSlotSalida(Slot slotSalida)
    {
        this.slotSalida = slotSalida;
    }
    
    /**
     * Ejecuta la tarea de Merger
     * Lee los mensajes de todos los Slots de entrada y los envía al Slot de salida
     * El objetivo es fusionar los mensajes en una misma secuencia de Salida
     */
    public void ejecutar()
    {
        if (this.listaSlotsEntrada == null || this.listaSlotsEntrada.isEmpty())
        {
            //throw new Exception("NO hay ningun Slot de Entrada definido en la tarea Merger");
        }
        if (this.slotSalida == null)
        {
            //throw new Exception("NO hay ningun Slot de Salida definido en la tarea Merger");
        }

        // Recorremos cada Slot de Entrada
        for (Slot s : this.listaSlotsEntrada)
        {
            // Obtenemos el numero de mensajes que quieren escribir en cada Slot de Entrada
            int numMensajes = s.getQueue().size();

            for (int i = 0; i < numMensajes; i++)
            {
                Document mensaje;
                try 
                {
                    mensaje = s.leer();
                    
                    if (mensaje != null)
                    {
                        try
                        {
                            this.slotSalida.escribir(mensaje);
                        } catch (Exception ex)
                        {
                            Logger.getLogger(Merger.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception ex)
                {
                    Logger.getLogger(Merger.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    
    
}
