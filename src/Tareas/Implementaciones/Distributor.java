package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje;
import java.util.ArrayList;

public class Distributor extends Tarea {

    public Distributor(ArrayList<Slot> entrada, ArrayList<Slot> salidas) {
        super(entrada, salidas, TipoTarea.ENRUTADORAS);
    }

    @Override
    public void ejecutar() {
        // Usamos el nuevo método estaVacio() del Slot
        while (!this.getEntradas().get(0).estaVacio()) {
            Mensaje msgOriginal = null;
            try {
                // leer() ahora devuelve Mensaje y no lanza excepción
                msgOriginal = getEntradas().get(0).leer();
                
                if (msgOriginal != null) {
                    for (int i = 0; i < getSalidas().size(); i++) {
                        Slot salida = getSalidas().get(i);
                        
                        // Usamos el método helper de Tarea para clonar
                        Mensaje msgCopia = clonarMensaje(msgOriginal);
                        
                        // escribir() ahora no lanza excepción
                        salida.escribir(msgCopia);
                    }
                }
            } catch (Exception e) {
                // Este catch es para el .cloneNode() u otro error inesperado
                System.out.println("Excepcion en Distributor: " + e.getMessage());
            }
        }
    }
}