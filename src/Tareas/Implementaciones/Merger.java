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
    
    public Merger(ArrayList<Slot> listaSlotsEntrada, ArrayList<Slot> slotSalida, TipoTarea tipo)
    {
        super(listaSlotsEntrada, slotSalida, tipo);
    }
    
    /**
     * Ejecuta la tarea de Merger
     * Lee los mensajes de todos los Slots de entrada y los envía al Slot de salida
     * El objetivo es fusionar los mensajes en una misma secuencia de Salida
     */
    public void ejecutar()
    {
        ArrayList<Slot> listaSlotsEntrada = getEntradas();
        ArrayList<Slot> slotSalida = getSalidas();

        // Recorremos cada Slot de Entrada
        for (Slot s : listaSlotsEntrada)
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
                            slotSalida.getFirst().escribir(mensaje);
                        } catch (Exception ex)
                        {
                            Logger.getLogger(Merger.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Merger: Error al escribir en el Slot de Salida");
                        }
                    }
                } catch (Exception ex)
                {
                    Logger.getLogger(Merger.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Merger: Error al lerr del Slot de Entrada");
                }
            }
        }
    }

    
    
}
