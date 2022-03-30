package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque {

	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	private int espacioMax;

	public Parque(int espacioMax) {
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
		this.espacioMax = espacioMax;
	}

	@Override
	public synchronized void entrarAlParque(String puerta) {
		// Hemos anadido el synchronized para que solo pueda entrar un
		// hilo cada vez

		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.put(puerta, 0);
		}

		//Comprobacion 
		comprobarAntesDeEntrar();

		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta) + 1);

		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");

		//Notificamos a todos los hilos
		this.notifyAll();

		//Comprobamos que el conteo es correcto
		checkInvariante();

	}

	@Override
	public void salirDelParque(String puerta) {

		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.remove(puerta);
		}

		comprobarAntesDeSalir();

		contadorPersonasTotales--;
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta) - 1);

		sumarContadoresPuerta();

		imprimirInfo(puerta, "Salida");

		this.notifyAll();

		checkInvariante();

	}

	private void imprimirInfo(String puerta, String movimiento) {
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); // + " tiempo medio de estancia: " +
																					// tmedio);

		// Iteramos por todas las puertas e imprimimos sus entradas
		for (String p : contadoresPersonasPuerta.keySet()) {
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}

	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
		Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
		while (iterPuertas.hasMoreElements()) {
			sumaContadoresPuerta += iterPuertas.nextElement();
		}
		return sumaContadoresPuerta;
	}

	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales
				: "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
		assert contadorPersonasTotales <= espacioMax : "No entran mas personas.";
		assert contadorPersonasTotales >= 0 : "No se ha controlado bien las entradas/salidas";

	}

	protected void comprobarAntesDeEntrar() { // TODO
		while (contadorPersonasTotales == espacioMax) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void comprobarAntesDeSalir() { // TODO
		while (contadorPersonasTotales == 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
