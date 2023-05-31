package es.unizar.eina.ejerciciosvoley;

public class Ejercicio {

    private String id;
    private double preferencia;
    private String nombre;
    private String descripcion;
    private int edadMin;
    private int edadMax;
    private int duracion;
    private int numJugadoresMin;
    private int numJugadoresMax;
    private double puntuacion;
    private int numUsos;
    private String objetivo;
    private String tipo;
    private String imagenURL;

    public Ejercicio() {
    }

    public Ejercicio(String id, double preferencia, String nombre, String descripcion, int edadMin, int edadMax, int duracion, int numJugadoresMin, int numJugadoresMax,
                     double puntuacion, int numUsos, String objetivo, String tipo, String imagenURL) {

        this.setId(id);
        this.setPreferencia(preferencia);
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setEdadMin(edadMin);
        this.setEdadMax(edadMax);
        this.setDuracion(duracion);
        this.setNumJugadoresMin(numJugadoresMin);
        this.setNumJugadoresMax(numJugadoresMax);
        this.setPuntuacion(puntuacion);
        this.setNumUsos(numUsos);
        this.setObjetivo(objetivo);
        this.setTipo(tipo);
        this.setImagenURL(imagenURL);

    }

    public Ejercicio(String id, String nombre, String descripcion, int duracion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPreferencia() {
        return preferencia;
    }

    public void setPreferencia(double preferencia) {
        this.preferencia = preferencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getEdadMin() {
        return edadMin;
    }

    public void setEdadMin(int edadMin) {
        this.edadMin = edadMin;
    }

    public int getEdadMax() {
        return edadMax;
    }

    public void setEdadMax(int edadMax) {
        this.edadMax = edadMax;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public int getNumJugadoresMin() {
        return numJugadoresMin;
    }

    public void setNumJugadoresMin(int numJugadoresMin) {
        this.numJugadoresMin = numJugadoresMin;
    }

    public int getNumJugadoresMax() {
        return numJugadoresMax;
    }

    public void setNumJugadoresMax(int numJugadoresMax) {
        this.numJugadoresMax = numJugadoresMax;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getNumUsos() {
        return numUsos;
    }

    public void setNumUsos(int numUsos) {
        this.numUsos = numUsos;
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

    public String getImagenURL() {
        return imagenURL;
    }

    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }

    @Override
    public String toString() {
        return "Ejercicio{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", duracion=" + duracion +
                '}';
    }
}
