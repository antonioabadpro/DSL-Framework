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

public class CorrelationIdSetter extends Tarea {

    private Map<UUID, List<Mensaje>> mensajesEntrada;

    public CorrelationIdSetter(ArrayList<Slot> entradas, ArrayList<Slot> salidas) {
        super(entradas, salidas, Tipo.ENRUTADORA);
        this.mensajesEntrada = new HashMap<>();

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("CorrelationIdSetter debe tener 1 Slot de Entrada");
        }
        if (listaSalidas == null || listaSalidas.size() != 1) {
            throw new IllegalArgumentException("CorrelationIdSetter debe tener 1 Slot de Salida");
        }
    }

    @Override
    public void procesarMensaje() {
        // En tu diseño actual esta tarea solo tiene 1 entrada, así que es fácil
        Mensaje mensaje = listaEntradas.get(0).leerSlot();
        if (mensaje == null) {
            return;
        }

        UUID idCorr = mensaje.getIdCorrelacion();
        if (idCorr == null) {
            return;
        }

        List<Mensaje> lista = mensajesEntrada.computeIfAbsent(idCorr, k -> new ArrayList<>());
        lista.add(mensaje);

        // Lógica: Cuando haya tantos mensajes como salidas, reenviamos
        // (Nota: En tu AppCorrelationIdSetter configuraste 1 entrada y 1 salida, 
        // así que esto pasará inmediatamente).
        if (lista.size() == listaSalidas.size()) {
            for (int i = 0; i < listaSalidas.size(); i++) {
                listaSalidas.get(i).escribirSlot(lista.get(i));
            }
            mensajesEntrada.remove(idCorr);
        }
    }
}
