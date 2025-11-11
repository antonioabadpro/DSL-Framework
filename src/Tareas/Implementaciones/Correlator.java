package Tareas.Implementaciones;

import Puertos.Slot;
import Mensajes.Mensaje; // CAMBIO: Importar Mensaje
import static Tareas.Implementaciones.TipoTarea.ENRUTADORAS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Clase Correlator genérico
 * Usa el AlmacenMensajes para guardar mensajes pendientes de correlación.
 *
 * @author agustinrodriguez
 */
public class Correlator extends Tarea {

    private String tagClave;          // Etiqueta XML usada para correlacionar
    private XPath xPath; // Hacemos el XPath un miembro de la clase

    public Correlator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String tagClave) {
        super(entradas, salidas, ENRUTADORAS);
        this.tagClave = tagClave;
        this.xPath = XPathFactory.newInstance().newXPath();
    }

    @Override
    public void ejecutar() {
        try {
            ArrayList<Slot> entradas = getEntradas();
            ArrayList<Slot> salidas = getSalidas();

            // Procesar mensajes de todas las entradas
            for (Slot entrada : entradas) {
                while (!entrada.estaVacio()) {
                    
                    // CAMBIO: Leer Mensaje
                    Mensaje msg = entrada.leer();
                    if (msg == null) continue;

                    // CAMBIO: Extraer el valor clave del cuerpo del mensaje
                    String valor = extraerValor(msg.getCuerpo(), tagClave, xPath);
                    if (valor == null || valor.isEmpty()) {
                        System.out.println("Correlator: Mensaje sin valor clave. Ignorando.");
                        continue;
                    }

                    String claveAlmacen = "correlator_" + valor;

                    // --- CAMBIO: Lógica de Almacén Sincronizada ---
                    List<Mensaje> mensajesCoincidentes;
                    synchronized (almacen) {
                        mensajesCoincidentes = (List<Mensaje>) almacen.obtener(claveAlmacen);
                        if (mensajesCoincidentes == null) {
                            mensajesCoincidentes = Collections.synchronizedList(new ArrayList<>());
                            almacen.guardar(claveAlmacen, mensajesCoincidentes);
                        }
                    }
                    // --- Fin Lógica de Almacén ---

                    // CAMBIO: Correlacionar
                    // Si ya hay mensajes con esa clave, enviamos la(s) pareja(s)
                    if (!mensajesCoincidentes.isEmpty()) {
                        for (Mensaje msgPrevio : mensajesCoincidentes) {
                            for (Slot salida : salidas) {
                                // Enviar ambos, clonados para evitar modificar
                                // el original que está en el almacén.
                                salida.escribir(clonarMensaje(msg));
                                salida.escribir(clonarMensaje(msgPrevio));
                            }
                        }
                        // Opcional: ¿limpiar la lista después de correlacionar?
                        // Depende de la lógica de negocio (ej: 1-a-N vs 1-a-1)
                        // Por ahora, no la limpiamos, permitiendo correlación N-a-N
                    }
                    
                    // Añadir el mensaje actual a la lista para futuras correlaciones
                    mensajesCoincidentes.add(msg);
                }
            }
        } catch (Exception e) {
            System.err.println("Error en Correlator: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extrae el valor de una etiqueta del documento XML.
     */
    private String extraerValor(Document doc, String tag, XPath xPath) {
        try {
            // Lógica original de extracción
            NodeList nodes = (NodeList) xPath.compile("//" + tag).evaluate(doc, XPathConstants.NODESET);
            if (nodes != null && nodes.getLength() > 0 && nodes.item(0) != null) {
                return nodes.item(0).getTextContent().trim();
            }
        } catch (Exception e) {
            System.err.println("Error extrayendo valor de " + tag + ": " + e.getMessage());
        }
        return null;
    }

    // ----- Getters y Setters -----

    public String getTagClave() {
        return tagClave;
    }

    public void setTagClave(String tagClave) {
        this.tagClave = tagClave;
    }
}