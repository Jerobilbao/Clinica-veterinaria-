import java.util.Scanner;

/** Unica clase que usa Scanner y System.out. */
public class Main {

    private static final String ERR_DATOS = "ERROR: datos invalidos";
    private static final String ERR_COMANDO = "ERROR: comando desconocido";

    public static void main(String[] args) {
        Hospital hospital = new Hospital();
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String linea = sc.nextLine().trim();
            if (linea.isEmpty()) {
                continue;
            }
            if (linea.equalsIgnoreCase("salir")) {
                break;
            }
            System.out.println(procesar(hospital, linea.split("\\s+")));
        }
        sc.close();
    }

    private static String procesar(Hospital h, String[] t) {
        switch (t[0].toLowerCase()) {
            case "ingresar":
                return ingresar(h, t);
            case "vacunar":
                return t.length == 2 ? h.vacunar(t[1]) : ERR_DATOS;
            case "asignar":
                return t.length == 2 ? h.asignar(t[1]) : ERR_DATOS;
            case "medicar":
                return t.length == 3 ? h.medicar(t[1], leerDecimal(t[2])) : ERR_DATOS;
            case "alta":
                return t.length == 2 ? h.alta(t[1]) : ERR_DATOS;
            case "reporte":
                return h.reporte();
            default:
                return ERR_COMANDO;
        }
    }

    private static String ingresar(Hospital h, String[] t) {
        if (t.length < 2) {
            return ERR_DATOS;
        }
        switch (t[1].toLowerCase()) {
            case "perro":
                if (t.length != 7) return ERR_DATOS;
                return h.ingresarPerro(t[2], leerDecimal(t[3]), leerEntero(t[4]), t[5], t[6]);
            case "gato":
                if (t.length != 5) return ERR_DATOS;
                return h.ingresarGato(t[2], leerDecimal(t[3]), leerEntero(t[4]));
            case "ave":
                if (t.length != 6) return ERR_DATOS;
                return h.ingresarAve(t[2], leerDecimal(t[3]), leerEntero(t[4]), leerDecimal(t[5]));
            default:
                return ERR_COMANDO;
        }
    }

    /** NaN si no es un numero; Hospital lo trata como dato invalido. */
    private static double leerDecimal(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    /** -1 si no es un entero (fuera de rango para la edad, asi que es invalido). */
    private static int leerEntero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
