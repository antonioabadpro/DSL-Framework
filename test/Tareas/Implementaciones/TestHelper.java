package Tareas.Implementaciones; // O el paquete de test que uses

import Mensajes.Mensaje; 
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Clase de utilidad para crear y leer XML en los tests (JUnit 4).
 */
public class TestHelper {

    private DocumentBuilder builder;

    public TestHelper() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException("Error inicializando DocumentBuilder", e);
        }
    }

    public Document crearDocumento(String xml) throws Exception {
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public String getStringDesdeDocumento(Document doc) throws Exception {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString().trim().replaceAll(">\\s+<", "><");
    }
    
    public Mensaje crearMensaje(String xml) throws Exception {
        return new Mensaje(crearDocumento(xml));
    }
}