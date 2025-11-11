package Tareas.Creadoras;

import Puertos.Slot;
import Tareas.Implementaciones.Splitter;
import Tareas.Implementaciones.Tarea;
import java.util.ArrayList;

/**
 *
 * @author Sergio
 */
public class CreadorSplitter extends CreadorTarea{
     private String groupTag;
    private String elementTag;
    private String idTag; 

    public CreadorSplitter(String groupTag, String elementTag, String idTag) {
        this.groupTag = groupTag;
        this.elementTag = elementTag;
        this.idTag = idTag;
    }

    @Override
    public Tarea creaTarea(ArrayList<Slot> slotEntradas, ArrayList<Slot> slotSalidas) {
        return new Splitter(slotEntradas, slotSalidas,this.groupTag, this.elementTag, this.idTag);
    }
    
    
}
