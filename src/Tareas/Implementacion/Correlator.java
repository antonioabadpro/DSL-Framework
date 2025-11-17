package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;

/**
 * Entradas: n; Salidas: n
 * Acumula mensajes por valor XPath y los envía cuando tiene suficientes
 */
public class Correlator extends Tarea {

    private String expresionXpath;
    private Map<String, List<Mensaje>> mensajesEntrada;

    public Correlator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String expresionXpath) {
        super(entradas, salidas, Tipo.ENRUTADORA);
        this.expresionXpath = expresionXpath;
        this.mensajesEntrada = new HashMap<>();

        if (listaEntradas == null || listaEntradas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Correlator debe tener al menos 2 Slots de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Correlator debe tener al menos 2 Slots de Salida");
        }

        if (listaEntradas.size() != listaSalidas.size()) {
            throw new IllegalArgumentException("La Tarea Correlator debe tener el mismo numero de Slots de entradas y salidas");
        }
    }

    @Override
    public void ejecutar() {
        for (Slot slot : listaEntradas) {
            while (!slot.estaVacio()) {
                try {
                    Mensaje mensaje = slot.leerSlot();
                    relacionar(mensaje);
                } catch (XPathExpressionException ex) {
                    Logger.getLogger(Correlator.class.getName()).log(Level.SEVERE,
                            "Correlator: Error en expresión XPath", ex);
                }
            }
        }
    }

    public void relacionar(Mensaje mensaje) throws XPathExpressionException {
        if (mensaje != null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            Node node = (Node) xpath.evaluate(expresionXpath, mensaje.getCuerpo(), XPathConstants.NODE);

            if (node == null) {
                System.out.println("Correlator: No se ha encontrado ningún nodo con XPath: " + expresionXpath);
                return;
            }

            String valor = node.getTextContent();
            if (valor == null || valor.trim().isEmpty()) {
                System.out.println("Correlator: Nodo vacío con XPath: " + expresionXpath);
                return;
            }

            valor = valor.trim();
            System.out.println("Correlator: Valor para correlación: " + valor);

            // Creamos una nueva lista si NO existe
            if (mensajesEntrada.containsKey(valor)) {
                List<Mensaje> listaMensajesOrdenada = mensajesEntrada.get(valor);
                listaMensajesOrdenada.add(mensaje);

                // Verificamos si tenemos suficientes mensajes
                if (listaMensajesOrdenada.size() == listaSalidas.size()) {
                    // Ordenamos los Mensajes antes de enviarlos
                    listaMensajesOrdenada.sort((m1, m2) -> m1.getId().compareTo(m2.getId()));

                    for (int i = 0; i < listaSalidas.size(); i++) {
                        listaSalidas.get(i).escribirSlot(listaMensajesOrdenada.get(i));
                    }

                    System.out.println("Correlator: " + listaMensajesOrdenada.size() + " mensajes correlacionados por: " + valor);

                    
                    mensajesEntrada.remove(valor); // Limpiamos/Eliminamos el valor del Mensaje del mapa
                }
            } else {
                // Creamos una nueva lista de mensajes que asociamos al "valor" del map.
                List<Mensaje> listaMensajes = new ArrayList<>();
                listaMensajes.add(mensaje);
                mensajesEntrada.put(valor, listaMensajes);
                System.out.println("Correlator: Mensaje guardado esperando correlación: " + valor);
            }
        }
    }
}
