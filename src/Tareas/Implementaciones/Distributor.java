package Tareas.Implementaciones;

import Puertos.Slot;
import java.util.ArrayList;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Distributor extends Tarea {

    private Slot entrada;
    private ArrayList<Slot> salidas;

    public Distributor(Slot entrada, ArrayList<Slot> salidas) {
        this.entrada = entrada;
        this.salidas = salidas;
    }

    public Slot getEntrada() {
        return entrada;
    }

    public void setEntrada(Slot entrada) {
        this.entrada = entrada;
    }

    public ArrayList<Slot> getSalidas() {
        return salidas;
    }

    public void setSalidas(ArrayList<Slot> salidas) {
        this.salidas = salidas;
    }

    @Override
    public void ejecutar() {

        while (!this.entrada.getQueue().isEmpty()) {

            Document docOriginal = null;
            try {

                docOriginal = entrada.leer();
                if (docOriginal != null) {

                    for (int i = 0; i < salidas.size(); i++) {
                        Slot salida = salidas.get(i);
                        Document docCopia = (Document) docOriginal.cloneNode(true);
                        salida.escribir(docCopia);
                    }
                }

            } catch (Exception e) {
                System.out.println("Excepcion al leer: " + e.getMessage());
            }
        }
    }
}
