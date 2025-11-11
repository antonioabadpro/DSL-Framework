/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // Importar
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AAHG-PORTATIL
 * Entradas = n
 * Salidas = 1
 */


public class Merger extends Tarea {
    
    public Merger(ArrayList<Slot> listaSlotsEntrada, ArrayList<Slot> slotSalida, TipoTarea tipo) {
        super(listaSlotsEntrada, slotSalida, tipo);
    }
    
    @Override
    public void ejecutar() {
        ArrayList<Slot> listaSlotsEntrada = getEntradas();
        Slot slotSalida = getSalidas().getFirst(); // Merger solo tiene una salida

        try {
            // Recorremos cada Slot de Entrada
            for (Slot s : listaSlotsEntrada) {
                
                // Drenamos la cola de entrada de forma segura
                while (!s.estaVacio()) {
                    // leer() ahora devuelve Mensaje
                    Mensaje mensaje = s.leer();
                    
                    if (mensaje != null) {
                        // escribir() ahora toma Mensaje
                        slotSalida.escribir(mensaje);
                    }
                }
            }
        } catch (Exception ex) {
            // Captura para cualquier error de lógica (no de Slot)
            Logger.getLogger(Merger.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Merger: Error al procesar mensajes");
        }
    }
}