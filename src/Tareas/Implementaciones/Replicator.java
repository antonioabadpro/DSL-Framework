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
    
    public Replicator(ArrayList<Slot> slotEntrada, ArrayList<Slot> listaSlotsSalida, TipoTarea tipo)
    {
        super(slotEntrada, listaSlotsSalida, tipo);
    }
    
    /**
     * Ejecuta la tarea de Replicator
     * Lee los mensajes del Slot de entrada y los replica en todos los Slots de Salida
     */
    public void ejecutar()
    {
        ArrayList<Slot> slotEntrada = getEntradas();
        ArrayList<Slot> listaSlotsSalida = getSalidas();

        int numMensajes = slotEntrada.getFirst().getQueue().size();
        
        for (int i = 0; i < numMensajes; i++)
        {
            Document mensaje;
            try
            {
                mensaje = slotEntrada.getFirst().leer();
                
                if (mensaje != null)
                {
                    // Replicamos el mismo mensaje en cada Slot de Salida
                    for (Slot salida : listaSlotsSalida)
                    {
                        try
                        {
                            salida.escribir(mensaje);
                        } catch (Exception ex)
                        {
                            Logger.getLogger(Replicator.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Replicator: Error al escribir en el Slot de Salida");
                        }
                    }
                }
            } catch (Exception ex)
            {
                Logger.getLogger(Replicator.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Replicator: Error al leer del Slot de Entrada");
            }
        }
    }
}
