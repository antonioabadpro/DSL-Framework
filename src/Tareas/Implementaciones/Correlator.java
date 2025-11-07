/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tareas.Implementaciones;

import Puertos.Slot;
import static Tareas.Implementaciones.TipoTarea.ENRUTADORAS;
import java.util.ArrayList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Clase Correlator genérico
 *
 * Permite correlacionar documentos XML provenientes de un número variable de entradas,
 * basándose en una etiqueta clave configurable (por ejemplo, "order_id", "nombre", "id").
 *
 * Los documentos correlacionados se reenvían a todas las salidas configuradas.
 *
 * @author agustinrodriguez
 */
public class Correlator extends Tarea {

    private String tagClave;          // Etiqueta XML usada para correlacionar

    public Correlator() {
        super(new ArrayList<>(), new ArrayList<>(), ENRUTADORAS);
    }

    public Correlator(ArrayList<Slot> entradas, ArrayList<Slot> salidas, String tagClave) {
        super(entradas, salidas, ENRUTADORAS);
        this.tagClave = tagClave;
    }

    @Override
    public void ejecutar() {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            ArrayList<Slot> entradas = getEntradas();
            ArrayList<Slot> salidas = getSalidas();

            // Leer todos los documentos de todas las entradas
            ArrayList<Document> documentos = new ArrayList<>();
            for (Slot entrada : entradas) {
                while (!entrada.getQueue().isEmpty()) {
                    Document doc = entrada.leer();
                    if (doc != null) {
                        documentos.add(doc);
                    }
                }
            }

            // Correlacionar documentos
            for (int i = 0; i < documentos.size(); i++) {
                Document doc1 = documentos.get(i);
                String valor1 = extraerValor(doc1, tagClave, xPath);

                if (valor1 == null || valor1.isEmpty()) {
                    continue;
                }

                // Buscar coincidencias con los demás documentos
                for (int j = i + 1; j < documentos.size(); j++) {
                    Document doc2 = documentos.get(j);
                    String valor2 = extraerValor(doc2, tagClave, xPath);

                    if (valor2 != null && valor1.equalsIgnoreCase(valor2)) {
                        // Coincidencia encontrada → enviar ambos a las salidas
                        for (Slot salida : salidas) {
                            salida.escribir(doc1);
                            salida.escribir(doc2);
                        }
                    }
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
