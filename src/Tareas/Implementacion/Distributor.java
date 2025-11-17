package Tareas.Implementacion;

import Mensajes.Mensaje;
import Slots.Slot;
import Tareas.Tarea;
import Tareas.Tipo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;

/**
 * Entradas: 1; Salidas: n
 */
public class Distributor extends Tarea {

    private final List<XPathExpression> listaExpresionesXpath;

    /**
     * @param listaEntradas lista de slots de entrada (1)
     * @param listaSalidas lista de slots de salida (n), cada salida tiene su expresion XPath
     * @param xPathExpressions Lista de expresiones XPath (una por salida)
     */
    public Distributor(ArrayList<Slot> listaEntradas, ArrayList<Slot> listaSalidas, List<String> xPathExpressions) {
        super(listaEntradas, listaSalidas, Tipo.ENRUTADORA);

        if (listaEntradas == null || listaEntradas.size() != 1) {
            throw new IllegalArgumentException("La Tarea Distributor debe tener 1 Slot de Entrada");
        }

        if (listaSalidas == null || listaSalidas.size() < 2) {
            throw new IllegalArgumentException("La Tarea Distributor debe tener al menos 2 Slots de Salida");
        }

        if (xPathExpressions == null || xPathExpressions.size() != listaSalidas.size()) {
            throw new IllegalArgumentException("En la Tarea Distributor el número de expresiones XPath debe coincidir con el número de Slots de Salida");
        }

        // Compilamos las expresiones XPath, por si acaso la sintaxis es incorrecta
        this.listaExpresionesXpath = new ArrayList<>();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        for (String expresion : xPathExpressions) {
            try {
                this.listaExpresionesXpath.add(xpath.compile(expresion));
            } catch (XPathExpressionException e) {
                throw new IllegalArgumentException("Expresión XPath inválida: " + expresion, e);
            }
        }
    }
    
    @Override
    public void ejecutar() {
        while (!this.listaEntradas.get(0).estaVacio()) {
            Mensaje mensaje = this.listaEntradas.get(0).leerSlot();
            if (mensaje != null) {
                try {
                    Document cuerpo = mensaje.getCuerpo();
                    boolean enviado = false;
                    int i = 0;

                    while (i < listaExpresionesXpath.size() && !enviado) {
                        XPathExpression expresion = listaExpresionesXpath.get(i);
                        Boolean resultado = (Boolean) expresion.evaluate(cuerpo, XPathConstants.BOOLEAN);

                        if (resultado) {
                            this.listaSalidas.get(i).escribirSlot(mensaje);
                            enviado = true;
                            System.out.println("Distributor: Mensaje enviado a salida " + i);
                        }
                        i++;
                    }

                    if (!enviado) {
                        Logger.getLogger(Distributor.class.getName()).log(Level.WARNING,
                                "Distributor: Ninguna expresión XPath coincidió para el mensaje: {0}", mensaje.getId());
                    }

                } catch (XPathExpressionException ex) {
                    Logger.getLogger(Distributor.class.getName()).log(Level.SEVERE,
                            "Distributor: Error al evaluar expresión XPath", ex);
                }
            }
        }
    }
}
