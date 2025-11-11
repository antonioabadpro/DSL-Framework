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
 * Entradas = 1
 * Salidas = n
 */
public class Replicator extends Tarea {
    
    public Replicator(ArrayList<Slot> slotEntrada, ArrayList<Slot> listaSlotsSalida, TipoTarea tipo) {
        super(slotEntrada, listaSlotsSalida, tipo);
    }
    
    @Override
    public void ejecutar() {
        Slot slotEntrada = getEntradas().getFirst();
        ArrayList<Slot> listaSlotsSalida = getSalidas();

        // Usamos el nuevo método estaVacio()
        while (!slotEntrada.estaVacio()) {
            Mensaje msgOriginal;
            try {
                // leer() devuelve Mensaje
                msgOriginal = slotEntrada.leer();
                
                if (msgOriginal != null) {
                    // Replicamos el mismo mensaje en cada Slot de Salida
                    for (Slot salida : listaSlotsSalida) {
                        // Usamos el método helper de Tarea para clonar
                        Mensaje msgCopia = clonarMensaje(msgOriginal);
                        
                        // escribir() toma Mensaje
                        salida.escribir(msgCopia);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(Replicator.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Replicator: Error al procesar mensaje");
            }
        }
    }
}