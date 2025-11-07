/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import static Tareas.Implementaciones.TipoTarea.TRANSFORMADORAS;
import java.util.ArrayList;
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
 *
 * Clase Splitter genérica
 * Divide un XML en múltiples mensajes individuales (n elementos de una lista), agregando metadatos.
 *
 * Ejemplo:
 *   <cafe_order>
 *       <order_id>1</order_id>
 *       <drinks>
 *           <drink>...</drink>
 *           <drink>...</drink>
 *       </drinks>
 *   </cafe_order>
 *
 * Cada salida incluirá:
 *   <mensaje>
 *       <metadatos>
 *           <id_secuencia>1</id_secuencia>
 *           <posicion_secuencia>1</posicion_secuencia>
 *           <tamanio_secuencia>2</tamanio_secuencia>
 *       </metadatos>
 *       <datos>
 *           <drink>...</drink>
 *       </datos>
 *   </mensaje>
 *
 * @author agustinrodriguez
 */
public class Splitter extends Tarea{

    private String groupTag;   // Ej: "drinks", "productos", "items"
    private String elementTag; // Ej: "drink", "producto", "item"
    private String idTag;      // Ej: "order_id", "pedido_id"

    public Splitter(ArrayList<Slot> entrada, ArrayList<Slot> salida, String groupTag, String elementTag, String idTag) {
        super(entrada, salida, TRANSFORMADORAS);
        this.groupTag = groupTag;
        this.elementTag = elementTag;
        this.idTag = idTag;
    }

    @Override
    public void ejecutar() {
        try {
            // Crear parser de documentos XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Slot entrada = getEntradas().getFirst();
            Slot salida = getSalidas().getFirst();
            
            // Leer documento de entrada desde el slot
            Document doc = entrada.leer();
            XPath xPath = XPathFactory.newInstance().newXPath();
            
            // Obtener los elementos dentro del grupo configurado (ej: //drinks/drink)
            String expression = String.format("//%s/%s", groupTag, elementTag);
            NodeList elementos = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            
            // Obtener el ID de secuencia (por ejemplo, order_id)
            NodeList idNodes = doc.getElementsByTagName(idTag);
            String idSecuencia = "N/A";
            if (idNodes != null && idNodes.getLength() > 0 && idNodes.item(0) != null) {
                idSecuencia = idNodes.item(0).getTextContent();
            }
            
            int total = (elementos != null) ? elementos.getLength() : 0;
            
            // Procesar cada elemento individual
            for (int i = 0; i < total; i++) {
                Node elemento = elementos.item(i);
                
                // Validar el tipo de nodo antes de continuar
                boolean esElementoValido = elemento != null && elemento.getNodeType() == Node.ELEMENT_NODE;
                
                if (esElementoValido) {
                    // Crear un nuevo documento de salida
                    Document nuevoDoc = builder.newDocument();
                    
                    // Nodo raíz <mensaje>
                    Element mensaje = nuevoDoc.createElement("mensaje");
                    nuevoDoc.appendChild(mensaje);
                    
                    // --- Sección de metadatos ---
                    Element metadatos = nuevoDoc.createElement("metadatos");
                    mensaje.appendChild(metadatos);
                    
                    Element id = nuevoDoc.createElement("id_secuencia");
                    id.setTextContent(idSecuencia);
                    metadatos.appendChild(id);
                    
                    Element posicion = nuevoDoc.createElement("posicion_secuencia");
                    posicion.setTextContent(String.valueOf(i + 1));
                    metadatos.appendChild(posicion);
                    
                    Element tamanio = nuevoDoc.createElement("tamanio_secuencia");
                    tamanio.setTextContent(String.valueOf(total));
                    metadatos.appendChild(tamanio);
                    
                    // --- Sección de datos ---
                    Element datos = nuevoDoc.createElement("datos");
                    mensaje.appendChild(datos);
                    
                    Node itemImportado = nuevoDoc.importNode(elemento, true);
                    datos.appendChild(itemImportado);
                    
                    // Escribir en el slot de salida
                    salida.escribir(nuevoDoc);
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
