public class Perro extends Paciente {

    private String raza;
    private boolean vacunado;

    public Perro(String nombre, double peso, int edad, String raza, boolean vacunado) {
        super(nombre, peso, edad, 10);
        if (!setRaza(raza)) {
            throw new IllegalArgumentException("datos invalidos");
        }
        this.vacunado = vacunado;
        if (!vacunado) {
            setEstado(CUARENTENA); // R3
        }
    }

    @Override
    protected double getPesoMaximo() {
        return 80;
    }

    @Override
    public String getTipo() {
        return "Perro";
    }

    @Override
    public String getZonaRequerida() {
        return "A";
    }

    /** Vacuna al perro; si estaba en cuarentena pasa a INGRESADO. */
    public void vacunar() {
        vacunado = true;
        if (estaEnCuarentena()) {
            setEstado(INGRESADO);
        }
    }

    public String getRaza() { return raza; }
    public boolean isVacunado() { return vacunado; }

    public boolean setRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) {
            return false;
        }
        this.raza = raza.trim();
        return true;
    }
}
