package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.ContextEnricher;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorContextEnricher extends CreadorTarea{
    private String claveContexto;

    public CreadorContextEnricher(String claveContexto) {
        this.claveContexto = claveContexto;
    }
    
    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new ContextEnricher(slotEntradas, this.claveContexto, slotSalidas);
    }
    
}
