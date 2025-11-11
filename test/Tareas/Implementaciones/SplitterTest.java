package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class SplitterTest extends BaseTaskTest {

    @Test
    public void testSplitterEjecutar() throws Exception {
        // --- Preparación ---
        String xmlEntrada = "<cafe_order>" +
                                "<order_id>123</order_id>" +
                                "<drinks>" +
                                    "<drink><tipo>Latte</tipo></drink>" +
                                    "<drink><tipo>Espresso</tipo></drink>" +
                                "</drinks>" +
                            "</cafe_order>";
        Mensaje msgEntrada = helper.crearMensaje(xmlEntrada);
        msgEntrada.setHeader("Canal", "Web");

        Slot inSlot = new Slot();
        inSlot.escribir(msgEntrada);
        Slot outSlot = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot); }};

        Splitter splitter = new Splitter(entradas, salidas, "drinks", "drink", "order_id");

        // --- Ejecución ---
        splitter.ejecutar();

        // --- Verificación ---
        assertTrue(inSlot.estaVacio());
        assertEquals(2, outSlot.getTamanio());

        // Verificar Mensaje 1
        Mensaje msg1 = outSlot.leer();
        assertNotNull(msg1);
        assertEquals("123", msg1.getHeader("id_secuencia"));
        assertEquals(1, msg1.getHeader("posicion_secuencia"));
        assertEquals(2, msg1.getHeader("tamanio_secuencia"));
        assertEquals("Web", msg1.getHeader("Canal"));
        assertEquals("<drink><tipo>Latte</tipo></drink>", helper.getStringDesdeDocumento(msg1.getCuerpo()));

        // Verificar Mensaje 2
        Mensaje msg2 = outSlot.leer();
        assertNotNull(msg2);
        assertEquals("123", msg2.getHeader("id_secuencia"));
        assertEquals(2, msg2.getHeader("posicion_secuencia"));
        assertEquals(2, msg2.getHeader("tamanio_secuencia"));
        assertEquals("Web", msg2.getHeader("Canal"));
        assertEquals("<drink><tipo>Espresso</tipo></drink>", helper.getStringDesdeDocumento(msg2.getCuerpo()));
        
        assertTrue(outSlot.estaVacio());
    }
}