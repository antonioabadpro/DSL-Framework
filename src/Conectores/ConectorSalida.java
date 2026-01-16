package Conectores;

import Mensajes.Mensaje;
import Puertos.Puerto;
import Puertos.PuertoSalida;
import java.io.File;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

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

        // Leemos del slot (bloqueante si está vacío)
        Mensaje mensaje = puertoSalida.leerMensajeDeSlot();

        if (mensaje != null && mensaje.getCuerpo() != null) {
            File directorio = new File(configuracion);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // GENERACIÓN AUTOMÁTICA DE NOMBRE
            // Usamos el idCorrelacion (o el id) para que el nombre sea único y trazable
            String nombreFichero = "pedido_" + mensaje.getIdCorrelacion() + ".xml";
            File archivoSalida = new File(directorio, nombreFichero);

            exportarDocumentoXML(mensaje.getCuerpo(), archivoSalida.getAbsolutePath());
            System.out.println("ConectorSalida: Mensaje exportado automáticamente a " + archivoSalida.getName());
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
