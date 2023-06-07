package util;

public class Rutina {
	
	private int numJugadores;
	private int duracion;
	private int edad;
	private String objetivo;
	private String tipo;
	
	public Rutina() {		
	}
	
	public Rutina(int duracion) {
		this.duracion = duracion;
	}
	
	public Rutina(int numJugadores, int edad, int duracion, String objetivo,String tipo) {
		this.numJugadores = numJugadores;
		this.duracion = duracion;
		this.edad = edad;
		this.objetivo = objetivo;
		this.tipo = tipo;
	}

	public int getNumJugadores() {
		return numJugadores;
	}

	public void setNumJugadores(int numJugadores) {
		this.numJugadores = numJugadores;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

}
