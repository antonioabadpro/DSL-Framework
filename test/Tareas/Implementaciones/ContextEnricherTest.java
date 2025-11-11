package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Mensajes.AlmacenMensajes; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import org.w3c.dom.Document;

public class ContextEnricherTest extends BaseTaskTest {

    @Test
    public void testContextEnricherEjecutar() throws Exception {
        // --- Preparación ---
        String clave = "datos_cliente_vip";
        String xmlContexto = "<info><cliente>Cliente VIP</cliente></info>";
        Document docContexto = helper.crearDocumento(xmlContexto);
        AlmacenMensajes.getInstance().guardar(clave, docContexto);

        String xmlEntrada = "<pedido><item>Cafe</item></pedido>";
        Mensaje msgEntrada = helper.crearMensaje(xmlEntrada);
        msgEntrada.setHeader("PedidoID", "P-500");
        
        Slot inSlot = new Slot();
        inSlot.escribir(msgEntrada);
        Slot outSlot = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot); }};

        ContextEnricher enricher = new ContextEnricher(entradas, clave, salidas);

        // --- Ejecución ---
        enricher.ejecutar();

        // --- Verificación ---
        assertTrue(inSlot.estaVacio());
        assertEquals(1, outSlot.getTamanio());
        Mensaje msgSalida = outSlot.leer();
        
        String xmlEsperado = "<pedido><item>Cafe</item><info><cliente>Cliente VIP</cliente></info></pedido>";
        assertEquals(xmlEsperado, helper.getStringDesdeDocumento(msgSalida.getCuerpo()));
        assertNotNull(msgSalida.getCabecera());
        assertEquals("P-500", msgSalida.getHeader("PedidoID"));
    }
}