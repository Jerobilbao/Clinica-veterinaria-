public class Gato extends Paciente {

    private static final int ESTRES_MAXIMO = 10;
    private static final int ESTRES_BLOQUEO = 8;

    private int nivelEstres; // 0 a 10

    public Gato(String nombre, double peso, int edad) {
        super(nombre, peso, edad, 6);
        this.nivelEstres = 0;
    }

    @Override
    protected double getPesoMaximo() {
        return 15;
    }

    @Override
    public String getTipo() {
        return "Gato";
    }

    @Override
    public String getZonaRequerida() {
        return "A";
    }

    @Override
    public boolean bloqueadoPorEstres() {
        return nivelEstres >= ESTRES_BLOQUEO;
    }

    @Override
    protected void despuesDeMedicar() {
        nivelEstres = Math.min(ESTRES_MAXIMO, nivelEstres + 2); // R9
    }

    public int getNivelEstres() { return nivelEstres; }

    public boolean setNivelEstres(int nivelEstres) {
        if (nivelEstres < 0 || nivelEstres > ESTRES_MAXIMO) {
            return false;
        }
        this.nivelEstres = nivelEstres;
        return true;
    }
}
