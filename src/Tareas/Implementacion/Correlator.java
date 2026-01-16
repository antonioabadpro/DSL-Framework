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
    public void procesarMensaje() {
        boolean huboActividad = false;

        // Revisamos todos los slots de entrada
        for (Slot slot : listaEntradas) {
            // TRUCO DE CONCURRENCIA: 
            // Como tenemos varias entradas, NO usamos leerSlot() a secas (que bloquea),
            // primero miramos si hay algo. Si bloqueamos en la entrada 1 estando vacía,
            // ignoraríamos la entrada 2 aunque estuviera llena.
            if (!slot.estaVacio()) {
                Mensaje mensaje = slot.leerSlot();
                if (mensaje != null) {
                    try {
                        relacionar(mensaje);
                        huboActividad = true;
                    } catch (XPathExpressionException ex) {
                        Logger.getLogger(Correlator.class.getName()).log(Level.SEVERE, "Correlator: Error XPath", ex);
                    }
                }
            }
        }

        // Si todos los buzones estaban vacíos, descansamos un poco para no poner la CPU al 100%
        if (!huboActividad) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void relacionar(Mensaje mensaje) throws XPathExpressionException {
        if (mensaje == null) {
            return;
        }

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        Node node = (Node) xpath.evaluate(expresionXpath, mensaje.getCuerpo(), XPathConstants.NODE);

        if (node == null) {
            System.out.println("Correlator: No se ha encontrado nodo con XPath: " + expresionXpath);
            return;
        }

        String valor = node.getTextContent();
        if (valor == null || valor.trim().isEmpty()) {
            return;
        }

        valor = valor.trim();
        // System.out.println("Correlator: Valor detectado: " + valor); // Descomentar para debug

        List<Mensaje> lista = mensajesEntrada.computeIfAbsent(valor, k -> new ArrayList<>());
        lista.add(mensaje);

        // Si ya tenemos tantos mensajes como salidas (el grupo está completo)
        if (lista.size() == listaSalidas.size()) {
            // Ordenar por ID para mantener consistencia
            lista.sort((m1, m2) -> m1.getId().compareTo(m2.getId()));

            for (int i = 0; i < listaSalidas.size(); i++) {
                listaSalidas.get(i).escribirSlot(lista.get(i));
            }

            System.out.println("Correlator: Grupo completado y enviado para valor: " + valor);
            mensajesEntrada.remove(valor);
        } else {
            System.out.println("Correlator: Mensaje retenido esperando pareja (" + lista.size() + "/" + listaSalidas.size() + ")");
        }
    }
}
