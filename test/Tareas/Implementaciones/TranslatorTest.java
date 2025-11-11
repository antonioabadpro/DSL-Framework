package Tareas.Implementaciones;

import Mensajes.Mensaje; // <-- CORREGIDO
import Puertos.Slot;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class TranslatorTest extends BaseTaskTest {

    @Test
    public void testTranslatorEjecutar() throws Exception {
        // --- Preparación ---
        String xsltContent = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" +
                             "<xsl:template match=\"/persona\"><usuario><nombre><xsl:value-of select=\"nombre\"/></nombre></usuario></xsl:template>" +
                             "</xsl:stylesheet>";
        String xsltFileName = "test_transform.xslt";
        try (FileWriter writer = new FileWriter(xsltFileName)) {
            writer.write(xsltContent);
        }
        
        String xmlEntrada = "<persona><nombre>Agustin</nombre><edad>30</edad></persona>";
        Mensaje msgEntrada = helper.crearMensaje(xmlEntrada);
        msgEntrada.setHeader("ID_Original", "ABC-123");

        Slot inSlot = new Slot();
        inSlot.escribir(msgEntrada);
        Slot outSlot = new Slot();
        ArrayList<Slot> entradas = new ArrayList<>() {{ add(inSlot); }};
        ArrayList<Slot> salidas = new ArrayList<>() {{ add(outSlot); }};

        Translator translator = new Translator(entradas, salidas, xsltFileName);

        // --- Ejecución ---
        translator.ejecutar();

        // --- Verificación ---
        assertTrue(inSlot.estaVacio());
        assertEquals(1, outSlot.getTamanio());
        Mensaje msgSalida = outSlot.leer();
        
        String xmlEsperado = "<usuario><nombre>Agustin</nombre></usuario>";
        assertEquals(xmlEsperado, helper.getStringDesdeDocumento(msgSalida.getCuerpo()));
        assertNotNull(msgSalida.getCabecera());
        assertEquals("ABC-123", msgSalida.getHeader("ID_Original"));

        new File(xsltFileName).delete();
    }
}