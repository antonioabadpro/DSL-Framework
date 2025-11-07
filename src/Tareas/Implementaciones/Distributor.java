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

    public Distributor(ArrayList<Slot> entrada, ArrayList<Slot> salidas) {
        super(entrada, salidas, TipoTarea.ENRUTADORAS);
    }

    @Override
    public void ejecutar() {


        while (!this.getEntradas().get(0).getQueue().isEmpty()) {
            Document docOriginal = null;
            try {

                docOriginal = getEntradas().get(0).leer();
                if (docOriginal != null) {

                    for (int i = 0; i < getSalidas().size(); i++) {
                        Slot salida = getSalidas().get(i);
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
