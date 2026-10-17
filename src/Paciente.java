/**
 * Clase base de todos los pacientes. No lee ni imprime nada.
 * Los setters validan y devuelven false (sin modificar el atributo) si el valor es invalido.
 */
public abstract class Paciente {

    public static final String INGRESADO = "INGRESADO";
    public static final String CUARENTENA = "CUARENTENA";
    public static final String EN_TRATAMIENTO = "EN_TRATAMIENTO";

    private static final int EDAD_MAXIMA = 30;
    private static final double EPSILON = 1e-9;

    private String nombre;
    private double peso;          // kg
    private int edad;             // anios
    private String estado;
    private double factorDosis;   // mg/kg
    private double dosisAcumulada; // mg
    private Jaula jaula;          // null si no tiene

    protected Paciente(String nombre, double peso, int edad, double factorDosis) {
        this.estado = INGRESADO;
        if (!setNombre(nombre) || !setPeso(peso) || !setEdad(edad) || !setFactorDosis(factorDosis)) {
            throw new IllegalArgumentException("datos invalidos");
        }
    }

    // ---- Datos propios de cada tipo ----
    protected abstract double getPesoMaximo();

    public abstract String getTipo();

    public abstract String getZonaRequerida();

    /** R4: tamano de jaula segun el peso. Las subclases pueden endurecerlo. */
    public String getTamanoRequerido() {
        if (peso < 5) {
            return "S";
        }
        if (peso <= 25) {
            return "M";
        }
        return "L";
    }

    /** Los gatos con estres alto no pueden medicarse. */
    public boolean bloqueadoPorEstres() {
        return false;
    }

    /** Gancho que se ejecuta tras una medicacion valida (el gato sube su estres). */
    protected void despuesDeMedicar() {
    }

    // ---- Logica de dosis (R7, R9) ----
    public double getDosisMaxima() {
        return peso * factorDosis;
    }

    public double getTopeAcumulado() {
        return 3 * getDosisMaxima();
    }

    public boolean excedeDosisMaxima(double mg) {
        return mg > getDosisMaxima() + EPSILON;
    }

    public boolean superaTopeAcumulado(double mg) {
        return dosisAcumulada + mg > getTopeAcumulado() + EPSILON;
    }

    /** Registra una medicacion ya validada por Hospital. */
    public void aplicarDosis(double mg) {
        dosisAcumulada += mg;
        estado = EN_TRATAMIENTO;
        despuesDeMedicar();
    }

    public boolean estaEnCuarentena() {
        return CUARENTENA.equals(estado);
    }

    public boolean tieneJaula() {
        return jaula != null;
    }

    // ---- Getters ----
    public String getNombre() { return nombre; }
    public double getPeso() { return peso; }
    public int getEdad() { return edad; }
    public String getEstado() { return estado; }
    public double getFactorDosis() { return factorDosis; }
    public double getDosisAcumulada() { return dosisAcumulada; }
    public Jaula getJaula() { return jaula; }

    // ---- Setters con validacion ----
    public boolean setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        this.nombre = nombre.trim();
        return true;
    }

    public boolean setPeso(double peso) {
        if (Double.isNaN(peso) || peso <= 0 || peso > getPesoMaximo()) {
            return false;
        }
        this.peso = peso;
        return true;
    }

    public boolean setEdad(int edad) {
        if (edad < 0 || edad > EDAD_MAXIMA) {
            return false;
        }
        this.edad = edad;
        return true;
    }

    public boolean setEstado(String estado) {
        if (!INGRESADO.equals(estado) && !CUARENTENA.equals(estado) && !EN_TRATAMIENTO.equals(estado)) {
            return false;
        }
        this.estado = estado;
        return true;
    }

    public boolean setFactorDosis(double factorDosis) {
        if (Double.isNaN(factorDosis) || factorDosis <= 0) {
            return false;
        }
        this.factorDosis = factorDosis;
        return true;
    }

    public boolean setDosisAcumulada(double dosisAcumulada) {
        if (Double.isNaN(dosisAcumulada) || dosisAcumulada < 0) {
            return false;
        }
        this.dosisAcumulada = dosisAcumulada;
        return true;
    }

    /** Acepta null (sin jaula). */
    public void setJaula(Jaula jaula) {
        this.jaula = jaula;
    }
}
