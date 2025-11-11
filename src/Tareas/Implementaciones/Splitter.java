package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // CAMBIO: Importar Mensaje
import static Tareas.Implementaciones.TipoTarea.TRANSFORMADORAS;
import java.util.ArrayList;
import java.util.HashMap; // CAMBIO: Importar HashMap para la cabecera
import java.util.Map; // CAMBIO: Importar Map
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Clase Splitter genérica
 * Divide un XML en múltiples mensajes individuales.
 * crea un objeto Mensaje con una cabecera y un cuerpo.
 * * @author agustinrodriguez
 */
public class Splitter extends Tarea{

    private String groupTag;   // Ej: "drinks"
    private String elementTag; // Ej: "drink"
    private String idTag;      // Ej: "order_id"

    public Splitter(ArrayList<Slot> entrada, ArrayList<Slot> salida, String groupTag, String elementTag, String idTag) {
        super(entrada, salida, TRANSFORMADORAS);
        this.groupTag = groupTag;
        this.elementTag = elementTag;
        this.idTag = idTag;
    }

    @Override
    public void ejecutar() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Slot entrada = getEntradas().getFirst();
            Slot salida = getSalidas().getFirst();
            
            // CAMBIO: Leer el Mensaje de entrada
            Mensaje msgEntrada = entrada.leer();
            if (msgEntrada == null) {
                System.out.println("Splitter: No hay mensaje en la entrada.");
                return;
            }
            
            // CAMBIO: Obtener cuerpo y cabecera
            Document doc = msgEntrada.getCuerpo();
            Map<String, Object> cabeceraOriginal = msgEntrada.getCabecera();
            
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Lógica original de XPath
            String expression = String.format("//%s/%s", groupTag, elementTag);
            NodeList elementos = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            
            NodeList idNodes = doc.getElementsByTagName(idTag);
            String idSecuencia = "N/A";
            if (idNodes != null && idNodes.getLength() > 0 && idNodes.item(0) != null) {
                idSecuencia = idNodes.item(0).getTextContent();
            }
            
            int total = (elementos != null) ? elementos.getLength() : 0;
            
            for (int i = 0; i < total; i++) {
                Node elemento = elementos.item(i);
                
                boolean esElementoValido = elemento != null && elemento.getNodeType() == Node.ELEMENT_NODE;
                
                if (esElementoValido) {
                    
                    // --- CAMBIO: Crear el nuevo Mensaje ---
                    
                    // 1. Crear el NUEVO CUERPO (solo el fragmento XML)
                    Document nuevoCuerpo = builder.newDocument();
                    Node itemImportado = nuevoCuerpo.importNode(elemento, true);
                    nuevoCuerpo.appendChild(itemImportado);
                    
                    // 2. Crear la NUEVA CABECERA
                    // Copiamos la cabecera original para preservar sus datos
                    Map<String, Object> nuevaCabecera = new HashMap<>(cabeceraOriginal);
                    
                    // Añadimos los metadatos del Split
                    nuevaCabecera.put("id_secuencia", idSecuencia);
                    nuevaCabecera.put("posicion_secuencia", i + 1); // Posición es 1-based
                    nuevaCabecera.put("tamanio_secuencia", total);
                    
                    // 3. Crear el NUEVO MENSAJE de salida
                    Mensaje msgSalida = new Mensaje(nuevaCabecera, nuevoCuerpo);
                    
                    // 4. Escribir en el slot de salida
                    salida.escribir(msgSalida);
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Splitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // ----- Getters y Setters -----
   
    public void setGroupTag(String groupTag) {
        this.groupTag = groupTag;
    }

    public void setElementTag(String elementTag) {
        this.elementTag = elementTag;
    }

    public void setIdTag(String idTag) {
        this.idTag = idTag;
    }
}