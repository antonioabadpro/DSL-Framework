package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Entradas: 1; Salidas: 1
 * Relaciona mensajes que comparten el mismo 'idCorrelacion'
 * Cuando ha recibido un mensaje por cada Slot de entrada para un mismo idCorrelacion, reenvía esos mensajes a los Slots de salida en el mismo orden.
 */
public class CorrelationIdSetter extends Tarea {

    private Map<UUID, List<Mensaje>> mensajesEntrada;

    public CorrelationIdSetter(ArrayList<Slot> entradas, ArrayList<Slot> salidas) {
        super(entradas, salidas, Tipo.ENRUTADORA);
        this.mensajesEntrada = new HashMap<>();

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Correlation Id Setter debe tener 1 Slot de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Correlation Id Setter debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void ejecutar() {
        boolean hayMensajes = true;

        // Mientras quede algo en cualquiera de los Slots de entrada seguimos leyendo los Mensajes
        while (hayMensajes) {
            hayMensajes = false;

            for (Slot slot : listaEntradas) {
                if (!slot.estaVacio()) {
                    Mensaje mensaje = slot.leerSlot();
                    relacionar(mensaje);
                    hayMensajes = true;
                }
            }
        }
    }

    /**
     * Acumula mensajes por 'idCorrelacion'. Cuando para un 'idCorrelacion' se han recibido tantos mensajes como Slots de salida
     * (uno por cada Slot de Entrada), se envían dichos Mensajes a los Slots de Salida en el mismo orden que llegaron
     */
    public void relacionar(Mensaje mensaje) {
        UUID idCorr = mensaje.getIdCorrelacion();

        if (idCorr == null) {
            return;
        }

        List<Mensaje> listaMensajes = mensajesEntrada.get(idCorr);

        if (listaMensajes == null) {
            listaMensajes = new ArrayList<>();
            mensajesEntrada.put(idCorr, listaMensajes);
        }

        listaMensajes.add(mensaje);

        // Cuando tenemos un mensaje en cada Slot de Entrada (misma correlación), reenviamos tantos mensajes como salidas haya.
        if (listaMensajes.size() == listaSalidas.size()) {
            for (int i = 0; i < listaSalidas.size(); i++) {
                listaSalidas.get(i).escribirSlot(listaMensajes.get(i));
            }
            
            mensajesEntrada.remove(idCorr); // Limpiamos la entrada el 'idCorrelacion'
        }
    }
}
