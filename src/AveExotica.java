public class AveExotica extends Paciente {

    private double envergadura; // cm

    public AveExotica(String nombre, double peso, int edad, double envergadura) {
        super(nombre, peso, edad, 3);
        if (!setEnvergadura(envergadura)) {
            throw new IllegalArgumentException("datos invalidos");
        }
    }

    @Override
    protected double getPesoMaximo() {
        return 15;
    }

    @Override
    public String getTipo() {
        return "Ave";
    }

    @Override
    public String getZonaRequerida() {
        return "B";
    }

    /** R4: con envergadura mayor a 60 cm necesita como minimo M. */
    @Override
    public String getTamanoRequerido() {
        String porPeso = super.getTamanoRequerido();
        if (envergadura > 60 && porPeso.equals("S")) {
            return "M";
        }
        return porPeso;
    }

    public double getEnvergadura() { return envergadura; }

    public boolean setEnvergadura(double envergadura) {
        if (Double.isNaN(envergadura) || envergadura <= 0) {
            return false;
        }
        this.envergadura = envergadura;
        return true;
    }
}
