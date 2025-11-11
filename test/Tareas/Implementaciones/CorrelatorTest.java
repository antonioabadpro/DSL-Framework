package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Mensajes.AlmacenMensajes; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class CorrelatorTest extends BaseTaskTest {

    @Test
    public void testCorrelatorEjecutar() throws Exception {
        // --- Preparación ---
        Slot inSlot1 = new Slot();
        Slot inSlot2 = new Slot();
        Slot outSlot = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot1); add(inSlot2); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot); }};

        Correlator correlator = new Correlator(entradas, salidas, "id");
        AlmacenMensajes almacen = AlmacenMensajes.getInstance();

        // --- Ejecución 1 ---
        inSlot1.escribir(helper.crearMensaje("<pedido><id>123</id></pedido>"));
        correlator.ejecutar();

        // --- Verificación 1 ---
        assertTrue(outSlot.estaVacio());
        assertTrue(almacen.existe("correlator_123"));

        // --- Ejecución 2 ---
        inSlot2.escribir(helper.crearMensaje("<factura><id>123</id></factura>"));
        correlator.ejecutar();

        // --- Verificación 2 ---
        assertEquals(2, outSlot.getTamanio());
        Mensaje msgA = outSlot.leer();
        Mensaje msgB = outSlot.leer();
        String xmlA = helper.getStringDesdeDocumento(msgA.getCuerpo());
        String xmlB = helper.getStringDesdeDocumento(msgB.getCuerpo());
        assertTrue(xmlA.contains("pedido") || xmlA.contains("factura"));
        assertTrue(xmlB.contains("pedido") || xmlB.contains("factura"));
        assertNotEquals(xmlA, xmlB);

        // --- Ejecución 3 ---
        inSlot1.escribir(helper.crearMensaje("<envio><id>123</id></envio>"));
        correlator.ejecutar();

        // --- Verificación 3 ---
        assertEquals(4, outSlot.getTamanio());
    }
}