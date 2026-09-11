# The Feline Graph Chronicles

Proyecto de Lenguajes y Compiladores — Universidad EIA. Pola y Minerva contra
Limon y Nero: cuatro misiones, seis algoritmos de grafos implementados desde
cero en Java, con una interfaz grafica en JavaFX.

## Integrantes

- Evelyn Gómez ([EvyG06](https://github.com/EvyG06))
- Jerónimo Arsitizabal

## Cómo compilar y correr

Requisito único: un **JDK 17 o superior** instalado (el proyecto se probó
con JDK 26). El Maven Wrapper incluido en el repositorio descarga Maven
automáticamente, así que no hace falta instalarlo aparte.

```bash
./mvnw javafx:run      # Linux / macOS
mvnw.cmd javafx:run    # Windows
```

Para correr únicamente los tests automatizados:

```bash
./mvnw test
```

> Nota de entorno: si tu `JAVA_HOME` apunta a un JDK antiguo (por ejemplo
> Java 8), expórtalo primero, p. ej. en PowerShell:
> `$env:JAVA_HOME = "C:\ruta\a\tu\jdk17-o-mas"`.

## Estructura del proyecto

```
src/main/java/com/eia/felinas/
├── Main.java          Punto de entrada (lanzador plano, ver Decisiones)
├── algoritmos/         BFS, DFS, Dijkstra, Floyd-Warshall, Bellman-Ford,
│                        Kruskal y Union-Find. CERO imports de JavaFX/Swing:
│                        todo se puede probar sin abrir una ventana.
├── modelo/              Tipos compartidos entre capas: Grilla, Arista,
│                        resultados de cada algoritmo (ResultadoBusqueda,
│                        ResultadoFloydWarshall, ResultadoBellmanFord...).
├── parser/              Lectura de la entrada como flujo de tokens
│                        (LectorTokens) y errores legibles (ErrorDeEntrada).
├── misiones/            Un orquestador por misión: parsea la entrada,
│                        ejecuta el/los algoritmos y arma la salida exacta
│                        "Case #k: ...". También carga los samples del
│                        enunciado (Samples) para tests y para el botón
│                        "cargar sample" de la GUI.
└── gui/                 Aplicación JavaFX: selector de misiones, áreas de
    └── viz/              entrada/salida (App) y los componentes de dibujo
                           (grilla con caminos resaltados, grafos, matriz
                           N x N de Floyd-Warshall).

src/main/resources/samples/   Los 4 samples del enunciado, en texto plano.
src/test/java/                Un test JUnit 5 por misión, usando esos
                               samples como valor esperado.
```

La regla de diseño que sostiene todo el proyecto: **`algoritmos` y `modelo`
no saben que existe una GUI.** Cada misión expone un único método,
`String resolver(String entrada)`, que recibe el texto pegado por el
usuario y devuelve exactamente lo que se debe mostrar. La GUI (cuando esté
completa) llama a ese mismo método; los tests lo llaman directamente. Si
mañana cambiáramos JavaFX por Swing, ni una línea de `algoritmos`,
`modelo`, `parser` o `misiones` cambiaría.

## Decisiones tomadas

- **DFS iterativo con pila explícita, no un Thread con stack agrandado.**
  Las grillas de la Misión 1 pueden llegar a 1000×1000 = 10⁶ celdas, y un
  DFS recursivo de esa profundidad desborda el stack de la JVM
  (`StackOverflowError`). En vez de mover el problema a un hilo con más
  stack, la pila vive explícitamente en el heap: cada posición es un
  "marco de llamada" `(celda, próxima dirección a intentar)`, lo que
  reproduce el mismo camino que daría la versión recursiva con el orden de
  vecinos exigido por el enunciado (arriba, abajo, izquierda, derecha).
  Ver el comentario de cabecera de `algoritmos/Dfs.java` para el detalle.
- **Grilla como arreglo booleano plano (`boolean[R*C]`), no `boolean[R][C]`.**
  Con hasta un millón de celdas, un arreglo 1D contiguo en memoria evita la
  indirección extra de una matriz de arreglos y permite tratar cada celda
  como un simple `int` en colas y pilas.
- **`long` para todo peso acumulado y un centinela nunca operado
  aritméticamente** (`Pesos.SIN_RUTA = Long.MIN_VALUE`). Todas las sumas
  de pesos comprueban primero que ningún operando sea el centinela, tal
  como exige la sección 2.1 del enunciado.
- **Floyd-Warshall y Bellman-Ford, ambos maximizantes, con el mismo
  criterio de "no acotado".** La Misión 3 exige que las dos ejecuciones
  concuerden para cada caso; el criterio de propagación de ciclos de
  ganancia positiva está escrito una sola vez en cada algoritmo pero sigue
  exactamente la misma definición matemática (ver comentarios en
  `FloydWarshall.java` y `BellmanFord.java`), y `Mision3` calcula un
  `crossCheckCoincide` booleano que la GUI usa para avisar si algún día
  dejaran de coincidir.
- **Maven + JavaFX (vía `javafx-maven-plugin`) en vez de Swing.** JavaFX da
  `Canvas` para las visualizaciones (grilla, grafos) y controles listos
  (`TabPane`, `ScrollPane`, `GridPane`) para la matriz de la Misión 3. Se
  declara como dependencia Maven normal, así que compila desde un clon
  limpio sin configuración adicional.
- **Un lanzador plano (`Main`) separado de la clase `Application`
  (`gui.App`).** Si la clase invocada directamente por la JVM extiende
  `Application` y JavaFX no está en el *module path* (solo en el
  *classpath*, que es como lo trae Maven), la JVM aborta con
  "JavaFX runtime components are missing" aunque los `.jar` de JavaFX sí
  estén presentes. Separar el punto de entrada evita el problema.
- **Límites de visualización centralizados en `LimitesVisualizacion`**
  (paquete `gui.viz`), con los umbrales exactos de la sección 2.3 del
  enunciado. Por encima del umbral, la respuesta numérica se sigue
  mostrando siempre (eso lo calcula `misiones`, no depende del dibujo) y
  el panel de dibujo se reemplaza por un mensaje explicando la omisión.



