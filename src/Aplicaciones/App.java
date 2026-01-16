package Aplicaciones;

import Conectores.*;
import Puertos.*;
import Slots.Slot;
import Tareas.Implementacion.*;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String[] args) throws SQLException {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8")); // Consola con caracteres UTF-8
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 1. Cargar Driver
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL cargado correctamente.");
        } catch (ClassNotFoundException e) {
            System.out.println("NO se encontró el driver PostgreSQL: " + e.getMessage());
        }

        // 2. Definición de Slots (Canales de comunicación)
        Slot sPuertoEntradaSplitter = new Slot();
        Slot sSplitterDistributor = new Slot();

        // Ramas Frías (F) y Calientes (C)
        Slot sDistributorReplicator_F = new Slot();
        Slot sDistributorReplicator_C = new Slot();

        Slot sReplicatorTranslator_F = new Slot();
        Slot sReplicatorTranslator_C = new Slot();

        Slot sReplicatorCorrelator_F = new Slot();
        Slot sReplicatorCorrelator_C = new Slot();

        Slot sTranslatorPuertoSolicitud_F = new Slot();
        Slot sTranslatorPuertoSolicitud_C = new Slot();

        Slot sPuertoSolicitudCorrelator_F = new Slot();
        Slot sPuertoSolicitudCorrelator_C = new Slot();

        // Slots dobles entre Correlator y Enricher (El correlator saca pares de mensajes)
        Slot sCorrelatorEnricher_1_F = new Slot();
        Slot sCorrelatorEnricher_2_F = new Slot();

        Slot sCorrelatorEnricher_1_C = new Slot();
        Slot sCorrelatorEnricher_2_C = new Slot();

        Slot sEnricherMerger_F = new Slot();
        Slot sEnricherMerger_C = new Slot();

        Slot sMergerAggregator = new Slot();
        Slot sAggregatorSalida = new Slot();

        // 3. Puertos
        PuertoEntrada puertoEntrada = new PuertoEntrada(sPuertoEntradaSplitter);
        // Puertos de BD (Bidireccionales: Envían al driver y reciben respuesta)
        PuertoSolicitud puertoBD_F = new PuertoSolicitud(sTranslatorPuertoSolicitud_F, sPuertoSolicitudCorrelator_F);
        PuertoSolicitud puertoBD_C = new PuertoSolicitud(sTranslatorPuertoSolicitud_C, sPuertoSolicitudCorrelator_C);
        PuertoSalida puertoSalida = new PuertoSalida(sAggregatorSalida);

        // 4. Configuración
        System.out.print("Escribe el nombre del fichero inicial (sin .xml): ");
        Scanner sc = new Scanner(System.in);
        String fichero = sc.nextLine();
        // Ojo: Ahora ConectorEntrada buscará este fichero, lo procesará y lo renombrará a .procesado
        //ConectorEntrada conectorEntrada = new ConectorEntrada(puertoEntrada, "src/Comandas/" + fichero + ".xml");

        // Esto apunta solo a la CARPETA. El conector ya se encarga de buscar los archivos dentro.
        ConectorEntrada conectorEntrada = new ConectorEntrada(puertoEntrada, "src/Comandas/");

        String urlBD = "jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:5432/postgres?user=postgres.wttznbvrlqmioczuafnx&password=bdiia202512345";

        ConectorBD conectorBD_F = new ConectorBD(puertoBD_F, urlBD);
        ConectorBD conectorBD_C = new ConectorBD(puertoBD_C, urlBD);

        // ConectorSalida ahora es automático
        ConectorSalida conectorSalida = new ConectorSalida(puertoSalida, "./salidas/");

        // 5. Instancia de Tareas
        // SPLITTER
        ArrayList<Slot> splitIn = new ArrayList<>();
        splitIn.add(sPuertoEntradaSplitter);
        ArrayList<Slot> splitOut = new ArrayList<>();
        splitOut.add(sSplitterDistributor);
        Splitter splitter = new Splitter(splitIn, splitOut, "/cafe_order/drinks/drink");

        // DISTRIBUTOR
        ArrayList<Slot> distIn = new ArrayList<>();
        distIn.add(sSplitterDistributor);
        ArrayList<Slot> distOut = new ArrayList<>();
        distOut.add(sDistributorReplicator_F);
        distOut.add(sDistributorReplicator_C);
        ArrayList<String> reglas = new ArrayList<>();
        reglas.add("drink/type = 'cold'");
        reglas.add("drink/type = 'hot'");
        Distributor distributor = new Distributor(distIn, distOut, reglas);

        // REPLICATORS
        ArrayList<Slot> repInF = new ArrayList<>();
        repInF.add(sDistributorReplicator_F);
        ArrayList<Slot> repOutF = new ArrayList<>();
        repOutF.add(sReplicatorTranslator_F);
        repOutF.add(sReplicatorCorrelator_F);
        Replicator replicatorF = new Replicator(repInF, repOutF);

        ArrayList<Slot> repInC = new ArrayList<>();
        repInC.add(sDistributorReplicator_C);
        ArrayList<Slot> repOutC = new ArrayList<>();
        repOutC.add(sReplicatorTranslator_C);
        repOutC.add(sReplicatorCorrelator_C);
        Replicator replicatorC = new Replicator(repInC, repOutC);

        // TRANSLATORS
        ArrayList<Slot> transInF = new ArrayList<>();
        transInF.add(sReplicatorTranslator_F);
        ArrayList<Slot> transOutF = new ArrayList<>();
        transOutF.add(sTranslatorPuertoSolicitud_F);
        Translator translatorF = new Translator(transInF, transOutF, "src/Formatos/BebidasFrias.xsl");

        ArrayList<Slot> transInC = new ArrayList<>();
        transInC.add(sReplicatorTranslator_C);
        ArrayList<Slot> transOutC = new ArrayList<>();
        transOutC.add(sTranslatorPuertoSolicitud_C);
        Translator translatorC = new Translator(transInC, transOutC, "src/Formatos/BebidasCalientes.xsl");

        // CORRELATORS (Ojo: App.java usa Correlator por contenido //name)
        ArrayList<Slot> corrInF = new ArrayList<>();
        corrInF.add(sReplicatorCorrelator_F);       // Viene del Replicator (Mensaje Original)
        corrInF.add(sPuertoSolicitudCorrelator_F);  // Viene de BD (Mensaje Resultado)
        ArrayList<Slot> corrOutF = new ArrayList<>();
        corrOutF.add(sCorrelatorEnricher_1_F);
        corrOutF.add(sCorrelatorEnricher_2_F);
        Correlator correlatorF = new Correlator(corrInF, corrOutF, "//name");

        ArrayList<Slot> corrInC = new ArrayList<>();
        corrInC.add(sReplicatorCorrelator_C);
        corrInC.add(sPuertoSolicitudCorrelator_C);
        ArrayList<Slot> corrOutC = new ArrayList<>();
        corrOutC.add(sCorrelatorEnricher_1_C);
        corrOutC.add(sCorrelatorEnricher_2_C);
        Correlator correlatorC = new Correlator(corrInC, corrOutC, "//name");

        // CONTEXT ENRICHERS
        ArrayList<Slot> enrichInF = new ArrayList<>();
        enrichInF.add(sCorrelatorEnricher_1_F);
        enrichInF.add(sCorrelatorEnricher_2_F);
        ArrayList<Slot> enrichOutF = new ArrayList<>();
        enrichOutF.add(sEnricherMerger_F);
        ContextEnricher enricherF = new ContextEnricher(enrichInF, enrichOutF);

        ArrayList<Slot> enrichInC = new ArrayList<>();
        enrichInC.add(sCorrelatorEnricher_1_C);
        enrichInC.add(sCorrelatorEnricher_2_C);
        ArrayList<Slot> enrichOutC = new ArrayList<>();
        enrichOutC.add(sEnricherMerger_C);
        ContextEnricher enricherC = new ContextEnricher(enrichInC, enrichOutC);

        // MERGER
        ArrayList<Slot> mergerIn = new ArrayList<>();
        mergerIn.add(sEnricherMerger_F);
        mergerIn.add(sEnricherMerger_C);
        ArrayList<Slot> mergerOut = new ArrayList<>();
        mergerOut.add(sMergerAggregator);
        Merger merger = new Merger(mergerIn, mergerOut);

        // AGGREGATOR
        ArrayList<Slot> aggIn = new ArrayList<>();
        aggIn.add(sMergerAggregator);
        ArrayList<Slot> aggOut = new ArrayList<>();
        aggOut.add(sAggregatorSalida);
        Aggregator aggregator = new Aggregator(aggIn, aggOut, "cafe_order");

        // ---------------------------------------------------------
        // 6. ARRANQUE DE HILOS
        // ---------------------------------------------------------
        System.out.println(">>> SISTEMA ARRANCADO (Modo Concurrente) <<<");
        System.out.println("Esperando ficheros en src/Comandas/...");

        new Thread(splitter).start();
        new Thread(distributor).start();

        new Thread(replicatorF).start();
        new Thread(replicatorC).start();

        new Thread(translatorF).start();
        new Thread(translatorC).start();

        new Thread(conectorBD_F).start();
        new Thread(conectorBD_C).start();

        new Thread(correlatorF).start();
        new Thread(correlatorC).start();

        new Thread(enricherF).start();
        new Thread(enricherC).start();

        new Thread(merger).start();
        new Thread(aggregator).start();

        new Thread(conectorSalida).start();

        // Arrancamos la entrada al final (o cuando quieras)
        new Thread(conectorEntrada).start();
    }
}
