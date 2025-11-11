package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class MergerTest extends BaseTaskTest {

    @Test
    public void testMergerEjecutar() throws Exception {
        // --- Preparación ---
        Slot inSlot1 = new Slot();
        Slot inSlot2 = new Slot();
        inSlot1.escribir(helper.crearMensaje("<msg1/>"));
        inSlot2.escribir(helper.crearMensaje("<msg2/>"));
        Slot outSlot = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot1); add(inSlot2); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot); }};

        Merger merger = new Merger(entradas, salidas);

        // --- Ejecución ---
        merger.ejecutar();

        // --- Verificación ---
        assertEquals(2, outSlot.getTamanio());
        assertTrue(inSlot1.estaVacio());
        assertTrue(inSlot2.estaVacio());
        assertNotNull(outSlot.leer());
        assertNotNull(outSlot.leer());
    }
}