/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Puertos;

import java.util.LinkedList;
import java.util.Queue;
import org.w3c.dom.Document;

/**
 *
 * @author agustinrodriguez
 */
public class Slot {
 
    private Queue<Document> testQueue = new LinkedList();

    public Queue getQueue() {
        return testQueue;
    }

    public Document leer() throws Exception {
        return testQueue.remove();
    }

    public void escribir(Document doc) throws Exception {
        testQueue.add(doc);
    }
    
}
