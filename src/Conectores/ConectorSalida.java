package Conectores;

import Mensajes.Mensaje;
import Puertos.Puerto;
import Puertos.PuertoSalida;
import java.io.File;
import java.util.Scanner;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 * Exporta mensajes a archivos XML
 * El Conector de Salida escribe mensajes como archivos XML
 * Permite que sistemas externos lean los mensajes a través de archivos
 */
public class ConectorSalida extends Conector {

    public ConectorSalida(Puerto puerto, String directorioSalida) {
        super(puerto, directorioSalida);
        
        if (!(puerto instanceof PuertoSalida)) {
            throw new IllegalArgumentException("ConectorSalida requiere un PuertoSalida");
        }
    }

    @Override
    public void ejecutar() {
        PuertoSalida puertoSalida = (PuertoSalida) puerto;
        Mensaje mensaje = puertoSalida.leerMensajeDeSlot();

        if (mensaje != null && mensaje.getCuerpo() != null) {
            File directorio = new File(configuracion);
            
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            System.out.println("Introduce el nombre del fichero de salida: (sin .xml) ");
            Scanner sc = new Scanner(System.in);
            String salida = sc.nextLine();
            String nombreArchivo = configuracion + salida + ".xml";
            File archivoSalida = new File(nombreArchivo);

            exportarDocumentoXML(mensaje.getCuerpo(), archivoSalida.getAbsolutePath());
            System.out.println("ConectorSalida: Mensaje exportado a " + nombreArchivo);
            sc.close();
        }
    }

    private void exportarDocumentoXML(Document documento, String rutaArchivo) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(documento);
            StreamResult result = new StreamResult(new File(rutaArchivo));

            transformer.transform(source, result);
            
        } catch (Exception ex) {
            System.err.println("ConectorSalida: Error exportando documento - " + ex.getMessage());
        }
    }
}