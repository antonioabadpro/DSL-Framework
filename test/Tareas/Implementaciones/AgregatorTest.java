package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Mensajes.AlmacenMensajes; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class AgregatorTest extends BaseTaskTest {

    private Mensaje crearMensajeParte(String id, int pos, int total, String xml) throws Exception {
        Map<String, Object> cabecera = new HashMap<>() {{
            put("id_secuencia", id);
            put("posicion_secuencia", pos);
            put("tamanio_secuencia", total);
        }};
        return new Mensaje(cabecera, helper.crearDocumento(xml));
    }

    @Test
    public void testAgregatorEstadoCompleto() throws Exception {
        // --- Preparación ---
        Slot inSlot = new Slot();
        Slot outSlot = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot); }};

        Agregator agregator = new Agregator(entradas, salidas, "drinks", "drink", "order_id");
        AlmacenMensajes almacen = AlmacenMensajes.getInstance();
        String claveAlmacen = "agregador_123";

        Mensaje msg1 = crearMensajeParte("123", 1, 2, "<drink><tipo>Latte</tipo></drink>");
        Mensaje msg2 = crearMensajeParte("123", 2, 2, "<drink><tipo>Espresso</tipo></drink>");
        
        // --- Ejecución 1 ---
        inSlot.escribir(msg1);
        agregator.ejecutar();

        // --- Verificación 1 ---
        assertTrue(outSlot.estaVacio());
        assertTrue(almacen.existe(claveAlmacen));

        // --- Ejecución 2 ---
        inSlot.escribir(msg2);
        agregator.ejecutar();

        // --- Verificación 2 ---
        assertEquals(1, outSlot.getTamanio());
        assertFalse(almacen.existe(claveAlmacen));

        Mensaje msgFinal = outSlot.leer();
        assertNotNull(msgFinal);
        
        String xmlEsperado = "<order>" +
                                "<order_id>123</order_id>" +
                                "<drinks>" +
                                    "<drink><tipo>Latte</tipo></drink>" +
                                    "<drink><tipo>Espresso</tipo></drink>" +
                                "</drinks>" +
                             "</order>";
        assertEquals(xmlEsperado, helper.getStringDesdeDocumento(msgFinal.getCuerpo()));
    }

    @Test
    public void testAgregatorOrdenIncorrecto() throws Exception {
        Slot inSlot = new Slot();
        Slot outSlot = new Slot();
        Agregator agregator = new Agregator(new ArrayList<>(){{add(inSlot);}}, new ArrayList<>(){{add(outSlot);}}, "items", "item", "pedido_id");

        Mensaje msg2 = crearMensajeParte("ABC", 2, 2, "<item>B</item>");
        Mensaje msg1 = crearMensajeParte("ABC", 1, 2, "<item>A</item>");
        
        inSlot.escribir(msg2);
        agregator.ejecutar();
        assertTrue(outSlot.estaVacio());
        
        inSlot.escribir(msg1);
        agregator.ejecutar();
        assertEquals(1, outSlot.getTamanio());
        
        Mensaje msgFinal = outSlot.leer();
        String xmlEsperado = "<order>" +
                                "<pedido_id>ABC</pedido_id>" +
                                "<items>" +
                                    "<item>A</item>" +
                                    "<item>B</item>" +
                                "</items>" +
                             "</order>";
        assertEquals(xmlEsperado, helper.getStringDesdeDocumento(msgFinal.getCuerpo()));
    }
}