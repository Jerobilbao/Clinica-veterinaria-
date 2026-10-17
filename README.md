# Hospital Veterinario – Evaluación POO en Java

Sistema de consola para gestionar los pacientes de una clínica veterinaria.
Solo Java (JDK 17+), sin librerías externas, sin archivos ni bases de datos, y con **vectores de capacidad fija** (sin `ArrayList`).

## 1. Cómo compilar y ejecutar

Desde IntelliJ: abrir la carpeta, marcar `src` como *Sources Root* y ejecutar `Main`.

Desde terminal, dentro de `src`:

```
javac *.java
java Main
```

Se escribe un comando por línea y `salir` termina el programa.

## 2. Estructura del proyecto

```
src/
 ├─ Paciente.java     clase base (abstracta)
 ├─ Perro.java        hereda de Paciente
 ├─ Gato.java         hereda de Paciente
 ├─ AveExotica.java   hereda de Paciente
 ├─ Jaula.java        jaulas del hospital
 ├─ Hospital.java     TODA la lógica de gestión
 └─ Main.java         único lugar con Scanner y System.out
```

```
            Paciente (abstracta)
           /      |        \
       Perro     Gato    AveExotica

Hospital ──tiene──> Paciente[10]  y  Jaula[6]
Paciente <──referencia──> Jaula   (enlace en ambos sentidos)
```

## 3. Decisiones de diseño (lo que hay que saber explicar)

### 3.1 Herencia y polimorfismo

`Paciente` guarda lo que es común: nombre, peso, edad, estado, factorDosis, dosisAcumulada y jaula.
Cada subclase solo aporta lo propio y llama a `super(...)` pasando su `factorDosis` (Perro 10, Gato 6, Ave 3), así **no hay código duplicado**.

`Paciente` es **abstracta** porque nunca se crea un "paciente genérico". Declara métodos abstractos que cada subclase responde a su manera:

| Método | Perro | Gato | Ave |
|---|---|---|---|
| `getPesoMaximo()` | 80 | 15 | 15 |
| `getZonaRequerida()` | A | A | B |
| `getTipo()` | "Perro" | "Gato" | "Ave" |

Además hay dos métodos que se **sobrescriben solo donde hace falta**:

- `getTamanoRequerido()` (R4): en `Paciente` calcula S/M/L por peso. `AveExotica` lo sobrescribe: si la envergadura es mayor a 60 cm y por peso saldría S, devuelve M.
- `bloqueadoPorEstres()` y `despuesDeMedicar()`: en `Paciente` no hacen nada. `Gato` los sobrescribe (bloquea con estrés ≥ 8 y sube el estrés 2 tras medicar, con tope 10).

**Por qué es importante:** `Hospital` nunca pregunta "¿es un gato?" para aplicar estas reglas. Llama al método y cada objeto responde según su tipo. Esto es polimorfismo, y por eso los tres tipos viven en el mismo `Paciente[]`.
(La única vez que se usa `instanceof` es en `vacunar`, porque ese comando solo aplica a perros.)

### 3.2 Encapsulamiento

- Todos los atributos son `private`; se accede con getters y setters.
- Los setters **validan** y devuelven `boolean`: `true` si guardaron el valor, `false` si lo rechazaron **sin modificar el atributo**.
  Ejemplos: `setPeso` rechaza `NaN`, `<= 0` o mayor al máximo del tipo; `setEdad` exige 0–30; `setNivelEstres` exige 0–10; `Jaula.setTamano` solo acepta S/M/L.
- Los **constructores usan esos mismos setters**. Si alguno devuelve `false`, el constructor lanza `IllegalArgumentException`. Así la validación está escrita **una sola vez**.
- `Hospital` atrapa esa excepción y responde `ERROR: datos invalidos` (R1).

Ese diseño hace que el orden de R2 (datos → duplicado → lleno) salga natural: primero se intenta construir el objeto (datos), luego se revisa el nombre y por último la capacidad.

### 3.3 Separación dominio / consola

- Solo `Main` usa `Scanner` y `System.out`.
- Las clases del dominio y `Hospital` **no leen ni imprimen**: cada método de `Hospital` **devuelve un `String`** con el mensaje, y `Main` lo imprime.
- `Main` solo lee la línea, la separa por espacios (`split("\\s+")`), convierte números y llama al método de `Hospital` que corresponde.
  Si un número no se puede convertir, `leerDecimal` devuelve `NaN` y `leerEntero` devuelve `-1`; ambos valores son inválidos para el dominio y producen `ERROR: datos invalidos`.

### 3.4 Manejo de vectores

- `Paciente[] pacientes = new Paciente[10]` y `int cantidad` (cuántos hay realmente).
- **Agregar:** se guarda en `pacientes[cantidad]` y luego `cantidad++`. Antes se verifica que `cantidad < 10`.
- **Buscar:** recorrer de `0` a `cantidad - 1` comparando con `equalsIgnoreCase` (nombre único sin distinguir mayúsculas). Devuelve el índice o `-1`.
- **Eliminar con compactación (R10):** para el paciente en la posición `i`:
  ```java
  for (int k = i; k < cantidad - 1; k++) {
      pacientes[k] = pacientes[k + 1];   // cada uno se corre una posición
  }
  pacientes[cantidad - 1] = null;        // se limpia la última posición
  cantidad--;
  ```
  Ejemplo: `[A, B, C, D]` y se da de alta a B → `[A, C, D, null]` y `cantidad` baja de 4 a 3.
- **Jaulas:** `Jaula[] jaulas = new Jaula[6]`, que se recorre completo para buscar la primera compatible.
- Los datos iniciales se cargan en `cargarDatosIniciales()`, que el constructor de `Hospital` llama al arrancar.

### 3.5 Enlace paciente ↔ jaula

Al asignar se enlazan los dos lados:

```java
jaulas[j].setOcupante(p);   // la jaula conoce a su ocupante
p.setJaula(jaulas[j]);      // el paciente conoce su jaula
```

Al dar de alta se desenlazan los dos (`setOcupante(null)` y `setJaula(null)`).
Una jaula está **libre** cuando su ocupante es `null`; por eso `Jaula.estaLibre()` solo compara con `null`.

## 4. Reglas de negocio: dónde está cada una

| Regla | Dónde se implementa |
|---|---|
| R1 datos válidos | setters de cada clase + constructores que lanzan excepción; `Hospital` la convierte en el mensaje |
| R2 máx. 10 y nombre único | `Hospital.agregar()` (duplicado primero, lleno después) |
| R3 perro no vacunado → CUARENTENA | constructor de `Perro` |
| R4 tamaño de jaula | `Paciente.getTamanoRequerido()` y su override en `AveExotica` |
| R5 zona | `getZonaRequerida()` en cada subclase |
| R6 asignar | `Hospital.asignar()` + `Jaula.esCompatible()` |
| R7 dosis máxima y tope | `getDosisMaxima()`, `excedeDosisMaxima()`, `superaTopeAcumulado()` en `Paciente` |
| R8 orden de validaciones de medicar | `Hospital.medicar()` (en el orden exacto del enunciado) |
| R9 efectos de medicar | `Paciente.aplicarDosis()` + `Gato.despuesDeMedicar()` |
| R10 alta | `Hospital.alta()` (verifica estado, libera jaula y compacta) |

**Dosis (R7):** dosis máxima = `peso × factorDosis`; el acumulado no puede superar `3 × dosis máxima`.
Ejemplo con Rocky (30 kg, factor 10): máximo por aplicación 300 mg y tope acumulado 900 mg. Puede recibir 300 + 300 + 300 y la cuarta aplicación da `ERROR: tope acumulado superado`.
En las comparaciones se usa una tolerancia muy pequeña (`1e-9`) para evitar errores de redondeo de los `double`.

**Interpretación del tamaño de jaula (R6):** se asigna una jaula del **tamaño exacto** que requiere el paciente (según R4), de su zona y libre. Si se quisiera aceptar "ese tamaño o mayor", solo habría que cambiar la condición en `Jaula.esCompatible()`.

**Comandos con cantidad de argumentos incorrecta:** el enunciado no lo define; responden `ERROR: datos invalidos`.

## 5. Flujo de un comando (ejemplo: `medicar Misifu 5`)

1. `Main` lee la línea y la separa en `["medicar", "Misifu", "5"]`.
2. Convierte `"5"` a `double` y llama a `hospital.medicar("Misifu", 5.0)`.
3. `Hospital` valida en este orden y se detiene en el primer fallo:
   no encontrado → mg inválido → cuarentena → sin jaula → gato con estrés alto → dosis > máxima → acumulado superado.
4. Si todo pasa, `p.aplicarDosis(5)` suma al acumulado, cambia el estado a `EN_TRATAMIENTO` y, si es gato, sube su estrés (polimorfismo).
5. `Hospital` devuelve `"OK: Misifu medicado con 5 mg"` y `Main` lo imprime.

## 6. Ejemplo de ejecución

```
asignar Rocky
OK: Rocky asignado a jaula 4
asignar Kiwi
OK: Kiwi asignado a jaula 6
medicar Rocky 300
OK: Rocky medicado con 300 mg
ingresar perro Toby 10 2 criollo no
OK: Toby ingresado en CUARENTENA
asignar Toby
ERROR: paciente en cuarentena
vacunar Toby
OK: Toby vacunado (pasa a INGRESADO)
alta Kiwi
ERROR: paciente no apto para alta
alta Rocky
OK: Rocky dado de alta
reporte
--- REPORTE ---
Pacientes: ...
```

Por qué Kiwi va a la jaula 6 y no a la 5: pesa 0.8 kg (por peso sería S), pero su envergadura es 70 cm (> 60), así que necesita mínimo M, y la única jaula M de zona B es la 6.

## 7. Posibles preguntas del profesor

**¿Por qué `Paciente` es abstracta?**
Porque no existe un paciente genérico, solo perros, gatos y aves. Además permite declarar métodos abstractos que obligan a cada subclase a definir su zona, su tipo y su peso máximo.

**¿Cómo evitaste repetir código entre Perro, Gato y Ave?**
Los atributos y validaciones comunes están en `Paciente`; las subclases llaman a `super(...)` con su `factorDosis` y solo agregan lo propio.

**¿Por qué guardas Perro, Gato y Ave en un solo vector?**
Porque todos son un `Paciente` (herencia). Al llamar a un método, Java ejecuta la versión del objeto real (polimorfismo).

**¿Cómo logras que un setter no modifique el atributo si el valor es inválido?**
Primero valida y, si falla, hace `return false` antes de asignar. Solo si pasa la validación asigna el valor y devuelve `true`.

**¿Por qué los constructores lanzan una excepción?**
Un constructor no puede devolver `false`. Si los datos son inválidos, el objeto no debe existir, así que lanza `IllegalArgumentException`, que `Hospital` convierte en `ERROR: datos invalidos`.

**¿Cómo funciona la compactación del vector al dar de alta?**
Desde la posición del paciente, cada elemento se copia a la posición anterior; luego se pone `null` en la última posición ocupada y se baja el contador.

**¿Por qué `Hospital` devuelve `String` en vez de imprimir?**
Para cumplir que solo `Main` use `Scanner` y `System.out`. Así la lógica queda separada de la consola y se puede probar sin ella.

**¿Qué pasa si se intenta medicar a un gato con estrés 8 o más?**
`bloqueadoPorEstres()` devuelve `true` y `Hospital.medicar` responde `ERROR: gato con estres alto`. Cada medicación válida sube el estrés en 2 (tope 10), así que a la cuarta medicación seguida un gato con estrés inicial 0 queda bloqueado.

**¿Por qué el orden de las validaciones importa?**
El enunciado dice que se aplica la primera que falla y el comando termina sin cambios. Por eso `Hospital` revisa en el orden exacto indicado y retorna en la primera falla, sin modificar nada.
