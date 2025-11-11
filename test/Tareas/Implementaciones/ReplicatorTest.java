package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class ReplicatorTest extends BaseTaskTest {

    @Test
    public void testReplicatorEjecutar() throws Exception {
        // --- Preparación ---
        Slot inSlot = new Slot();
        inSlot.escribir(helper.crearMensaje("<root>test</root>"));
        Slot outSlot1 = new Slot();
        Slot outSlot2 = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot1); add(outSlot2); }};

        Replicator replicator = new Replicator(entradas, salidas);

        // --- Ejecución ---
        replicator.ejecutar();

        // --- Verificación ---
        assertTrue(inSlot.estaVacio());
        assertEquals(1, outSlot1.getTamanio());
        assertEquals(1, outSlot2.getTamanio());

        Mensaje msg1 = outSlot1.leer();
        Mensaje msg2 = outSlot2.leer();
        assertNotNull(msg1);
        assertNotNull(msg2);

        assertNotSame(msg1, msg2);
        assertEquals(helper.getStringDesdeDocumento(msg1.getCuerpo()), helper.getStringDesdeDocumento(msg2.getCuerpo()));
    }
}