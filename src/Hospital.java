import java.util.Locale;

/**
 * Contiene toda la logica de gestion. No lee ni imprime: cada metodo devuelve
 * el mensaje que Main debe mostrar.
 */
public class Hospital {

    private static final int CAPACIDAD = 10;
    private static final String ERR_DATOS = "ERROR: datos invalidos";
    private static final String ERR_NO_ENCONTRADO = "ERROR: paciente no encontrado";

    private final Paciente[] pacientes = new Paciente[CAPACIDAD];
    private int cantidad = 0;
    private final Jaula[] jaulas = new Jaula[6];

    public Hospital() {
        cargarDatosIniciales();
    }

    public final void cargarDatosIniciales() {
        pacientes[0] = new Perro("Rocky", 30.0, 4, "labrador", true);
        pacientes[1] = new Gato("Misifu", 4.0, 3);
        pacientes[2] = new AveExotica("Kiwi", 0.8, 2, 70);
        cantidad = 3;

        jaulas[0] = new Jaula(1, "S", "A");
        jaulas[1] = new Jaula(2, "M", "A");
        jaulas[2] = new Jaula(3, "M", "A");
        jaulas[3] = new Jaula(4, "L", "A");
        jaulas[4] = new Jaula(5, "S", "B");
        jaulas[5] = new Jaula(6, "M", "B");
    }

    // ---------------- Ingreso ----------------

    public String ingresarPerro(String nombre, double peso, int edad, String raza, String vacunadoTxt) {
        Boolean vacunado = parsearSiNo(vacunadoTxt);
        if (vacunado == null) {
            return ERR_DATOS;
        }
        Paciente p;
        try {
            p = new Perro(nombre, peso, edad, raza, vacunado);
        } catch (IllegalArgumentException e) {
            return ERR_DATOS;
        }
        return agregar(p);
    }

    public String ingresarGato(String nombre, double peso, int edad) {
        Paciente p;
        try {
            p = new Gato(nombre, peso, edad);
        } catch (IllegalArgumentException e) {
            return ERR_DATOS;
        }
        return agregar(p);
    }

    public String ingresarAve(String nombre, double peso, int edad, double envergadura) {
        Paciente p;
        try {
            p = new AveExotica(nombre, peso, edad, envergadura);
        } catch (IllegalArgumentException e) {
            return ERR_DATOS;
        }
        return agregar(p);
    }

    /** R2: el orden es datos (ya validados), duplicado, lleno. */
    private String agregar(Paciente p) {
        if (buscarIndice(p.getNombre()) != -1) {
            return "ERROR: nombre duplicado";
        }
        if (cantidad >= CAPACIDAD) {
            return "ERROR: hospital lleno";
        }
        pacientes[cantidad] = p;
        cantidad++;
        if (p.estaEnCuarentena()) {
            return "OK: " + p.getNombre() + " ingresado en CUARENTENA";
        }
        return "OK: " + p.getNombre() + " ingresado";
    }

    // ---------------- Comandos ----------------

    public String vacunar(String nombre) {
        int i = buscarIndice(nombre);
        if (i == -1) {
            return ERR_NO_ENCONTRADO;
        }
        if (!(pacientes[i] instanceof Perro)) {
            return "ERROR: solo aplica a perros";
        }
        Perro perro = (Perro) pacientes[i];
        if (perro.isVacunado()) {
            return "ERROR: ya vacunado";
        }
        perro.vacunar();
        return "OK: " + perro.getNombre() + " vacunado (pasa a INGRESADO)";
    }

    public String asignar(String nombre) {
        int i = buscarIndice(nombre);
        if (i == -1) {
            return ERR_NO_ENCONTRADO;
        }
        Paciente p = pacientes[i];
        if (p.estaEnCuarentena()) {
            return "ERROR: paciente en cuarentena";
        }
        if (p.tieneJaula()) {
            return "ERROR: paciente ya tiene jaula";
        }
        for (int j = 0; j < jaulas.length; j++) {
            if (jaulas[j].esCompatible(p)) {
                jaulas[j].setOcupante(p);
                p.setJaula(jaulas[j]);
                return "OK: " + p.getNombre() + " asignado a jaula " + jaulas[j].getNumero();
            }
        }
        return "ERROR: no hay jaula compatible";
    }

    public String medicar(String nombre, double mg) {
        int i = buscarIndice(nombre);
        if (i == -1) {
            return ERR_NO_ENCONTRADO;
        }
        if (Double.isNaN(mg) || mg <= 0) {
            return ERR_DATOS;
        }
        Paciente p = pacientes[i];
        if (p.estaEnCuarentena()) {
            return "ERROR: paciente en cuarentena";
        }
        if (!p.tieneJaula()) {
            return "ERROR: paciente sin jaula";
        }
        if (p.bloqueadoPorEstres()) {
            return "ERROR: gato con estres alto";
        }
        if (p.excedeDosisMaxima(mg)) {
            return "ERROR: dosis excede el maximo";
        }
        if (p.superaTopeAcumulado(mg)) {
            return "ERROR: tope acumulado superado";
        }
        p.aplicarDosis(mg);
        return "OK: " + p.getNombre() + " medicado con " + formatearMg(mg) + " mg";
    }

    public String alta(String nombre) {
        int i = buscarIndice(nombre);
        if (i == -1) {
            return ERR_NO_ENCONTRADO;
        }
        Paciente p = pacientes[i];
        if (!Paciente.EN_TRATAMIENTO.equals(p.getEstado())) {
            return "ERROR: paciente no apto para alta";
        }
        if (p.tieneJaula()) {
            p.getJaula().setOcupante(null);
            p.setJaula(null);
        }
        // Compactacion: los siguientes se desplazan una posicion
        for (int k = i; k < cantidad - 1; k++) {
            pacientes[k] = pacientes[k + 1];
        }
        pacientes[cantidad - 1] = null;
        cantidad--;
        return "OK: " + p.getNombre() + " dado de alta";
    }

    public String reporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- REPORTE ---\n");
        sb.append("Pacientes: ").append(cantidad).append("/").append(CAPACIDAD).append("\n");
        for (int i = 0; i < cantidad; i++) {
            Paciente p = pacientes[i];
            String numJaula = p.tieneJaula() ? String.valueOf(p.getJaula().getNumero()) : "-";
            sb.append(p.getNombre()).append(" | ")
              .append(p.getTipo()).append(" | ")
              .append(String.format(Locale.US, "%.1f", p.getPeso())).append(" kg | ")
              .append(p.getEstado()).append(" | jaula: ")
              .append(numJaula).append("\n");
        }
        sb.append("Jaulas libres: ").append(contarJaulasLibres());
        return sb.toString();
    }

    // ---------------- Auxiliares ----------------

    private int buscarIndice(String nombre) {
        for (int i = 0; i < cantidad; i++) {
            if (pacientes[i].getNombre().equalsIgnoreCase(nombre)) {
                return i;
            }
        }
        return -1;
    }

    private int contarJaulasLibres() {
        int libres = 0;
        for (int i = 0; i < jaulas.length; i++) {
            if (jaulas[i].estaLibre()) {
                libres++;
            }
        }
        return libres;
    }

    private Boolean parsearSiNo(String txt) {
        if ("si".equalsIgnoreCase(txt)) {
            return Boolean.TRUE;
        }
        if ("no".equalsIgnoreCase(txt)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private String formatearMg(double mg) {
        if (mg == Math.rint(mg)) {
            return String.valueOf((long) mg);
        }
        return String.valueOf(mg);
    }
}
