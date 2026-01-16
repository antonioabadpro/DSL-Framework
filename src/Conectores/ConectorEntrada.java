package Conectores;

import Mensajes.Mensaje;
import Puertos.Puerto;
import Puertos.PuertoEntrada;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class ConectorEntrada extends Conector {

    private boolean rutaImpresa = false; // Para no spamear la consola

    public ConectorEntrada(Puerto puerto, String directorioEntrada) {
        super(puerto, directorioEntrada);

        if (!(puerto instanceof PuertoEntrada)) {
            throw new IllegalArgumentException("ConectorEntrada requiere un PuertoEntrada");
        }
    }

    @Override
    public void ejecutar() {
        File directorio = new File(configuracion);

        // DEBUG: Imprimir la ruta absoluta UNA VEZ para ver dónde está buscando realmente
        if (!rutaImpresa) {
            System.out.println("DEBUG: ConectorEntrada vigilando carpeta -> " + directorio.getAbsolutePath());
            rutaImpresa = true;
        }

        if (!directorio.exists() || !directorio.isDirectory()) {
            System.err.println("ERROR: La ruta no existe o no es una carpeta: " + directorio.getAbsolutePath());
            this.detener();
            return;
        }

        // Buscamos cualquier .xml que NO sea .procesado
        File[] archivos = directorio.listFiles((dir, name)
                -> name.toLowerCase().endsWith(".xml") && !name.toLowerCase().endsWith(".procesado")
        );

        if (archivos != null) {
            for (File archivo : archivos) {
                procesarArchivo(archivo);
            }
        }

        // Pausa de 2 segundos para no saturar
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void procesarArchivo(File archivo) {
        System.out.println(">>> DETECTADO: " + archivo.getName());

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documento = builder.parse(archivo);
            documento.getDocumentElement().normalize();

            Mensaje mensaje = new Mensaje(documento);
            PuertoEntrada puertoEntrada = (PuertoEntrada) puerto;

            puertoEntrada.escribirMensajeEnSlot(mensaje);
            System.out.println("ConectorEntrada: Enviado al sistema.");

            // Renombrar a .procesado
            File archivoProcesado = new File(archivo.getAbsolutePath() + ".procesado");
            if (archivoProcesado.exists()) {
                archivoProcesado.delete();
            }

            if (archivo.renameTo(archivoProcesado)) {
                System.out.println("Estado: " + archivo.getName() + " -> PROCESADO.");
            }

        } catch (Exception ex) {
            Logger.getLogger(ConectorEntrada.class.getName()).log(Level.SEVERE, "Error leyendo " + archivo.getName(), ex);
        }
    }
}
