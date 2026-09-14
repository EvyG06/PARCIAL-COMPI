# The Feline Graph Chronicles

Proyecto de Lenguajes y Compiladores — Universidad EIA. Pola y Minerva contra
Limon y Nero: cuatro misiones, seis algoritmos de grafos implementados desde
cero en Java, con una interfaz grafica en JavaFX.

## Integrantes

- Evelyn Gómez 
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

