package Aplicaciones;

import Conectores.*;
import Puertos.*;
import Slots.Slot;
import Tareas.Implementacion.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class AppCorrelationIdSetter {

    public static void main(String[] args) throws SQLException {

        // Cargamos el Driver de PostgreSQL (BD)
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver PostgreSQL cargado correctamente.");
        } catch (ClassNotFoundException e) {
            System.out.println("NO se encontró la clase del driver PostgreSQL: " + e.getMessage());
        }
        
        // Creamos los Slots necesarios para el problema de Cafe
        Slot sPuertoEntradaSplitter = new Slot();
        Slot sSplitterDistributor = new Slot();
        Slot sDistributorReplicator_BebidasF = new Slot();
        Slot sDistributorReplicator_BebidasC = new Slot();
        Slot sReplicatorTranslator_BebidasF = new Slot();
        Slot sReplicatorTranslator_BebidasC = new Slot();
        Slot sReplicatorCorrelationIdSetter_BebidasF = new Slot();
        Slot sReplicatorCorrelationIdSetter_BebidasC = new Slot();
        Slot sTranslatorPuertoSolicitud_BebidasF = new Slot();
        Slot sTranslatorPuertoSolicitud_BebidasC = new Slot();
        Slot sPuertoSolicitudCorrelator_BebidasF = new Slot();
        Slot sPuertoSolicitudCorrelator_BebidasC = new Slot();
        Slot sCorrelationIdSetterContextEnricher_1_BebidasF = new Slot();
        Slot sCorrelationIdSetterContextEnricher_2_BebidasF = new Slot();
        Slot sCorrelationIdSetterContextEnricher_1_BebidasC = new Slot();
        Slot sCorrelationIdSetterContextEnricher_2_BebidasC = new Slot();
        Slot sContextEnricherMerger_BebidasF = new Slot();
        Slot sContextEnricherMerger_BebidasC = new Slot();
        Slot sMergerAgregator = new Slot();
        Slot sAggregatorPuertoSalida = new Slot();

        // Creamos los Puertos necesarios para el problema de Cafe
        PuertoEntrada puertoEntrada = new PuertoEntrada(sPuertoEntradaSplitter);
        PuertoSolicitud puertoSolicitud_BebidasF = new PuertoSolicitud(sTranslatorPuertoSolicitud_BebidasF, sPuertoSolicitudCorrelator_BebidasF);
        PuertoSolicitud puertoSolicitud_BebidasC = new PuertoSolicitud(sTranslatorPuertoSolicitud_BebidasC, sPuertoSolicitudCorrelator_BebidasC);
        PuertoSalida puertoSalida = new PuertoSalida(sAggregatorPuertoSalida);

        // Creamos los Conectores necesarios para el problema de Cafe
        System.out.println("Escribe el nombre de la comanda que quieres procesar: (sin .xml)");
        Scanner sc = new Scanner(System.in);
        String comanda = sc.nextLine();
        ConectorEntrada conectorEntrada = new ConectorEntrada(puertoEntrada, "src/Comandas/" + comanda + ".xml"); // Ruta Relativa al DIR de ejecucion del Programa

        String urlPostgres = "jdbc:postgresql://aws-1-eu-north-1.pooler.supabase.com:5432/postgres?user=postgres.wttznbvrlqmioczuafnx&password=bdiia202512345";

        // Test de conexión simple
        try (Connection con = DriverManager.getConnection(urlPostgres)) {
            System.out.println("CONEXIÓN OK con Supabase");
        } catch (SQLException e) {
            System.out.println("FALLO conectando a BD");
            System.out.println("  Mensaje   : " + e.getMessage());
            System.out.println("  SQLState  : " + e.getSQLState());
            System.out.println("  ErrorCode : " + e.getErrorCode());
            e.printStackTrace();
        }

        ConectorBD conector_BebidasF = new ConectorBD(puertoSolicitud_BebidasF, urlPostgres);
        ConectorBD conector_BebidasC = new ConectorBD(puertoSolicitud_BebidasC, urlPostgres);
        ConectorSalida conectorSalida = new ConectorSalida(puertoSalida, "./salidas/");

        // Creamos las Tareas necesarias para el problema de Cafe
        
        // Splitter: sPuertoEntradaSplitter -> sSplitterDistributor
        ArrayList<Slot> splitterEntrada = new ArrayList<>();
        splitterEntrada.add(sPuertoEntradaSplitter);
        ArrayList<Slot> splitterSalida = new ArrayList<>();
        splitterSalida.add(sSplitterDistributor);
        Splitter splitter = new Splitter(splitterEntrada, splitterSalida, "/cafe_order/drinks/drink");

        // Distributor: sSplitterDistributor -> sDistributorReplicator_BebidasF y sDistributorReplicator_BebidasC
        ArrayList<Slot> distributorEntrada = new ArrayList<>();
        distributorEntrada.add(sSplitterDistributor);
        ArrayList<Slot> distributorSalida = new ArrayList<>();
        distributorSalida.add(sDistributorReplicator_BebidasF);
        distributorSalida.add(sDistributorReplicator_BebidasC);
        ArrayList<String> reglasDistributor = new ArrayList<>();
        reglasDistributor.add("drink/type = 'cold'"); // BF - Bebidas Frías
        reglasDistributor.add("drink/type = 'hot'");  // BC - Bebidas Calientes
        Distributor distributor = new Distributor(distributorEntrada, distributorSalida, reglasDistributor);

        // Replicator BebidasF: sDistributorReplicator_BebidasF -> sReplicatorTranslator_BebidasF + sReplicatorCorrelator_BebidasF
        ArrayList<Slot> replicatorEntrada_BebidasF = new ArrayList<>();
        replicatorEntrada_BebidasF.add(sDistributorReplicator_BebidasF);
        ArrayList<Slot> replicatorSalida_BebidasF = new ArrayList<>();
        replicatorSalida_BebidasF.add(sReplicatorTranslator_BebidasF);
        replicatorSalida_BebidasF.add(sReplicatorCorrelationIdSetter_BebidasF);
        Replicator replicatorBebidasF = new Replicator(replicatorEntrada_BebidasF, replicatorSalida_BebidasF);

        // Replicator BebidasC: sDistributorReplicator_BebidasC -> sReplicatorTranslator_BebidasC + sReplicatorCorrelator_BebidasC
        ArrayList<Slot> replicatorEntrada_BebidasC = new ArrayList<>();
        replicatorEntrada_BebidasC.add(sDistributorReplicator_BebidasC);
        ArrayList<Slot> replicatorSalida_BebidasC = new ArrayList<>();
        replicatorSalida_BebidasC.add(sReplicatorTranslator_BebidasC);
        replicatorSalida_BebidasC.add(sReplicatorCorrelationIdSetter_BebidasC);
        Replicator replicatorBebidasC = new Replicator(replicatorEntrada_BebidasC, replicatorSalida_BebidasC);

        // Translator BebidasF: sReplicatorTranslator_BebidasF -> sTranslatorPuertoSolicitud_BebidasF con BebidasFrias.xsl
        ArrayList<Slot> translatorEntrada_BebidasF = new ArrayList<>();
        translatorEntrada_BebidasF.add(sReplicatorTranslator_BebidasF);
        ArrayList<Slot> translatorSalida_BebidasF = new ArrayList<>();
        translatorSalida_BebidasF.add(sTranslatorPuertoSolicitud_BebidasF);
        Translator translatorBebidasF = new Translator(translatorEntrada_BebidasF, translatorSalida_BebidasF, "src/Formatos/BebidasFrias.xsl");

        // Translator BebidasC: sReplicatorTranslator_BebidasC -> sTranslatorPuertoSolicitud_BebidasC con BebidasCalientes.xsl
        ArrayList<Slot> translatorEntrada_BebidasC = new ArrayList<>();
        translatorEntrada_BebidasC.add(sReplicatorTranslator_BebidasC);
        ArrayList<Slot> translatorSalida_BebidasC = new ArrayList<>();
        translatorSalida_BebidasC.add(sTranslatorPuertoSolicitud_BebidasC);
        Translator translatorBebidasC = new Translator(translatorEntrada_BebidasC, translatorSalida_BebidasC, "src/Formatos/BebidasCalientes.xsl");

        // CorrelationIdSetter BebidasC: sReplicatorCorrelationIdSetter_BebidasF + sPuertoSolicitudCorrelationIdSetter_BebidasF -> sCorrelatorContextEnricher_1_BebidasF + sCorrelatorContextEnricher_2_BebidasF
        ArrayList<Slot> correlationIdSetterEntrada_BebidasF = new ArrayList<>();
        correlationIdSetterEntrada_BebidasF.add(sReplicatorCorrelationIdSetter_BebidasF);
        correlationIdSetterEntrada_BebidasF.add(sPuertoSolicitudCorrelator_BebidasF);
        ArrayList<Slot> correlationIdSetterSalida_BebidasF = new ArrayList<>();
        correlationIdSetterSalida_BebidasF.add(sCorrelationIdSetterContextEnricher_1_BebidasF);
        correlationIdSetterSalida_BebidasF.add(sCorrelationIdSetterContextEnricher_2_BebidasF);
        CorrelationIdSetter correlationIdSetter_BebidasF = new CorrelationIdSetter(correlationIdSetterEntrada_BebidasF, correlationIdSetterSalida_BebidasF);

        // CorrelationIdSetter BebidasC: sReplicatorCorrelationIdSetter_BebidasC + sPuertoSolicitudCorrelationIdSetter_BebidasC -> sCorrelatorContextEnricher_1_BebidasC + sCorrelatorContextEnricher_2_BebidasC
        ArrayList<Slot> correlationIdSetterEntrada_BebidasC = new ArrayList<>();
        correlationIdSetterEntrada_BebidasC.add(sReplicatorCorrelationIdSetter_BebidasC);
        correlationIdSetterEntrada_BebidasC.add(sPuertoSolicitudCorrelator_BebidasC);
        ArrayList<Slot> correlationIdSetterSalida_BebidasC = new ArrayList<>();
        correlationIdSetterSalida_BebidasC.add(sCorrelationIdSetterContextEnricher_1_BebidasC);
        correlationIdSetterSalida_BebidasC.add(sCorrelationIdSetterContextEnricher_2_BebidasC);
        CorrelationIdSetter correlationIdSetter_BebidasC = new CorrelationIdSetter(correlationIdSetterEntrada_BebidasC, correlationIdSetterSalida_BebidasC);

        // ContextEnricher BebidasF: sCorrelatorContextEnricher_1_BebidasF + sCorrelatorContextEnricher_2_BebidasF -> sContextEnricherMerger_BebidasF
        ArrayList<Slot> contextEnricherEntrada_BebidasF = new ArrayList<>();
        contextEnricherEntrada_BebidasF.add(sCorrelationIdSetterContextEnricher_1_BebidasF);
        contextEnricherEntrada_BebidasF.add(sCorrelationIdSetterContextEnricher_2_BebidasF);
        ArrayList<Slot> contextEnricherSalida_BebidasF = new ArrayList<>();
        contextEnricherSalida_BebidasF.add(sContextEnricherMerger_BebidasF);
        ContextEnricher contextEnricherBebidasF = new ContextEnricher(contextEnricherEntrada_BebidasF, contextEnricherSalida_BebidasF);

        // ContextEnricher BebidasC: sCorrelatorContextEnricher_1_BebidasC + sCorrelatorContextEnricher_2_BebidasC -> sContextEnricherMerger_BebidasC
        ArrayList<Slot> contextEnricherEntrada_BebidasC = new ArrayList<>();
        contextEnricherEntrada_BebidasC.add(sCorrelationIdSetterContextEnricher_1_BebidasC);
        contextEnricherEntrada_BebidasC.add(sCorrelationIdSetterContextEnricher_2_BebidasC);
        ArrayList<Slot> contextEnricherSalida_BebidasC = new ArrayList<>();
        contextEnricherSalida_BebidasC.add(sContextEnricherMerger_BebidasC);
        ContextEnricher contextEnricherBebidasC = new ContextEnricher(contextEnricherEntrada_BebidasC, contextEnricherSalida_BebidasC);

        // Merger: sContextEnricherMerger_BebidasF + sContextEnricherMerger_BebidasC -> sMergerAggregator
        ArrayList<Slot> mergerEntrada = new ArrayList<>();
        mergerEntrada.add(sContextEnricherMerger_BebidasF);
        mergerEntrada.add(sContextEnricherMerger_BebidasC);
        ArrayList<Slot> mergerSalida = new ArrayList<>();
        mergerSalida.add(sMergerAgregator);
        Merger merger = new Merger(mergerEntrada, mergerSalida);

        // Aggregator: sMergerAggregator -> sAggregatorPuertoSalida
        ArrayList<Slot> aggregatorEntrada = new ArrayList<>();
        aggregatorEntrada.add(sMergerAgregator);
        ArrayList<Slot> aggregatorSalida = new ArrayList<>();
        aggregatorSalida.add(sAggregatorPuertoSalida);
        Aggregator aggregator = new Aggregator(aggregatorEntrada, aggregatorSalida, "cafe_order");

        // Realizamos la Secuencia de ejecucion del Programa
        conectorEntrada.ejecutar();
        splitter.ejecutar();
        distributor.ejecutar();
        replicatorBebidasF.ejecutar();
        replicatorBebidasC.ejecutar();
        translatorBebidasF.ejecutar();
        translatorBebidasC.ejecutar();
        conector_BebidasF.ejecutar();
        conector_BebidasC.ejecutar();
        correlationIdSetter_BebidasF.ejecutar();
        correlationIdSetter_BebidasC.ejecutar();
        contextEnricherBebidasF.ejecutar();
        contextEnricherBebidasC.ejecutar();
        merger.ejecutar();
        aggregator.ejecutar();
        conectorSalida.ejecutar();
    }
}
