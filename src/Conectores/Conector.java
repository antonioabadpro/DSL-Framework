package Conectores;

import Puertos.Puerto;

// AHORA IMPLEMENTA RUNNABLE
public abstract class Conector implements Runnable {

    protected Puerto puerto;
    protected String configuracion;
    // Bandera para detener el hilo si fuera necesario
    protected volatile boolean activo = true;

    public Conector(Puerto puerto, String configuracion) {
        this.puerto = puerto;
        this.configuracion = configuracion;
    }

    @Override
    public void run() {
        // Por defecto, un conector intentará trabajar mientras esté activo
        while (activo) {
            ejecutar();
            // Pequeña pausa para no quemar la CPU si la implementación de ejecutar no es bloqueante
            // (En las implementaciones buenas no hará falta, pero por seguridad en la clase base)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Mantenemos este método abstracto, pero ahora representará UNA iteración o EL proceso principal
    public abstract void ejecutar();

    public void detener() {
        this.activo = false;
    }
}
