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
 * Entradas = 1
 * Salidas = n
 */
public class Replicator extends Tarea
{
    private ArrayList<Slot> listaSlotsSalida;
    private Slot slotEntrada;
    
    public Replicator(Slot slotEntrada, ArrayList<Slot> listaSlotsSalida, TipoTarea tipo)
    {
        super(tipo);
        this.slotEntrada=slotEntrada;
        
        for(Slot s: listaSlotsSalida)
        {
            this.listaSlotsSalida.add(s);
        }
    }
    
    public Slot getSlotEntrada()
    {
        return this.slotEntrada;
    }

    public ArrayList<Slot> getSlotsSalida()
    {
        return this.listaSlotsSalida;
    }

    public void setSlotEntrada(Slot slotEntrada)
    {
        this.slotEntrada = slotEntrada;
    }

    public void setSlotsSalida(ArrayList<Slot> listaSlotsSalida)
    {
        this.listaSlotsSalida = listaSlotsSalida;
    }
    
    /**
     * Ejecuta la tarea de Replicator
     * Lee los mensajes del Slot de entrada y los replica en todos los Slots de Salida
     */
    public void ejecutar()
    {
        if (this.slotEntrada == null)
        {
            //throw new Exception("NO hay ningun Slot de Entrada definido en la tarea Replicator");
        }
        if (this.listaSlotsSalida == null || this.listaSlotsSalida.isEmpty())
        {
            //throw new Exception("NO hay ningun Slot de Salida definido en la tarea Replicator");
        }

        int numMensajes = this.slotEntrada.getQueue().size();
        
        for (int i = 0; i < numMensajes; i++)
        {
            Document mensaje;
            try
            {
                mensaje = this.slotEntrada.leer();
                
                if (mensaje != null)
                {
                    // Replicamos el mismo mensaje en cada Slot de Salida
                    for (Slot salida : this.listaSlotsSalida)
                    {
                        try
                        {
                            salida.escribir(mensaje);
                        } catch (Exception ex)
                        {
                            Logger.getLogger(Replicator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (Exception ex)
            {
                Logger.getLogger(Replicator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
