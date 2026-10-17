public class Jaula {

    private int numero;
    private String tamano;   // S, M o L
    private String zona;     // A o B
    private Paciente ocupante; // null si esta libre

    public Jaula(int numero, String tamano, String zona) {
        if (!setNumero(numero) || !setTamano(tamano) || !setZona(zona)) {
            throw new IllegalArgumentException("datos invalidos");
        }
    }

    public boolean estaLibre() {
        return ocupante == null;
    }

    /** R6: libre, del tamano que necesita el paciente y de su zona. */
    public boolean esCompatible(Paciente p) {
        return estaLibre()
                && tamano.equals(p.getTamanoRequerido())
                && zona.equals(p.getZonaRequerida());
    }

    public int getNumero() { return numero; }
    public String getTamano() { return tamano; }
    public String getZona() { return zona; }
    public Paciente getOcupante() { return ocupante; }

    public boolean setNumero(int numero) {
        if (numero <= 0) {
            return false;
        }
        this.numero = numero;
        return true;
    }

    public boolean setTamano(String tamano) {
        if (!"S".equals(tamano) && !"M".equals(tamano) && !"L".equals(tamano)) {
            return false;
        }
        this.tamano = tamano;
        return true;
    }

    public boolean setZona(String zona) {
        if (!"A".equals(zona) && !"B".equals(zona)) {
            return false;
        }
        this.zona = zona;
        return true;
    }

    /** Acepta null (liberar la jaula). */
    public void setOcupante(Paciente ocupante) {
        this.ocupante = ocupante;
    }
}
