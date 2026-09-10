# Uso de IA — The Feline Graph Chronicles

Declaración honesta de uso de IA exigida por la sección 8.1 del enunciado.
_(Este archivo se completa a medida que avanza el proyecto; antes de la
entrega ningún placeholder `[COMPLETAR: ...]` debe quedar sin resolver.)_

## Herramientas usadas

- **Claude Code** (Anthropic, modelo Claude Sonnet 5), usado como asistente
  de pareja de programación durante todo el proyecto: lectura y análisis
  del enunciado, diseño de la arquitectura, implementación de los seis
  algoritmos, escritura de tests JUnit, componentes de visualización en
  JavaFX, y esta documentación.
- `[COMPLETAR: cualquier otra herramienta que use el otro integrante —
  ChatGPT, Copilot, etc. — y para qué partes específicas]`

## Prompts decisivos

1. **"Vamos a realizar este parcial en conjunto... me harás preguntas para
   entender mejor el proyecto"** — el primer prompt del proyecto. Fue
   decisivo porque, en vez de generar código de inmediato, forzó una fase
   de lectura completa del PDF (17 páginas) y de preguntas de aclaración
   (framework de GUI, build tool, idioma, estrategia de DFS) antes de
   escribir una sola línea. Esa arquitectura de paquetes (`algoritmos`
   sin JavaFX, `misiones` como frontera testeable) se decidió ahí y se
   sostuvo sin cambios durante todo el desarrollo.
2. **"Implementa Floyd-Warshall y Bellman-Ford maximizantes con
   cross-check entre ambos"** (Misión 3) — decisivo porque el enunciado
   exige que los dos algoritmos usen el **mismo criterio exacto** para
   marcar un par como "no acotado" (ciclo de ganancia positiva
   alcanzable); si los criterios de propagación no son idénticos, el
   cross-check falla silenciosamente en cualquier grafo con un ciclo
   positivo que no alcanza al destino. El prompt exigió explícitamente
   escribir un test para ese caso límite exacto (ver
   `Mision3Test.cicloPositivoQueNoAlcanzaAlDestinoNoEsInfinito`).
3. **"Profundiza [la explicación del DFS] y continúa"** — pedimos que la
   IA generara la traza REAL ejecutando el propio código del proyecto
   (no una explicación de memoria) sobre el sample del enunciado, e
   imprimiera el camino de BFS y de DFS sobre la grilla 10×10. Eso
   permitió verificar, celda por celda, que la simulación de recursión
   con pila explícita reproduce exactamente el mismo camino que
   produciría el DFS recursivo clásico, y sirvió como material de
   defensa oral verificable en vez de una afirmación sin comprobar.

## Casos donde la salida generada fue incorrecta o subóptima

1. **Verificación visual de la GUI con el flag equivocado.** Al intentar
   confirmar visualmente los componentes de visualización, el primer
   intento lanzó `mvnw javafx:run "-Djavafx.mainClass=..."` asumiendo que
   el plugin de Maven acepta ese override por línea de comandos. No es
   así: el plugin ignoró el flag sin avisar y corrió la clase configurada
   por defecto (`App`, la ventana placeholder), lo que a simple vista
   parecía "funcionar" porque no hubo ningún error. El problema solo se
   detectó porque insistimos en tomar una captura de pantalla real en vez
   de confiar en el log de `BUILD SUCCESS`. Se corrigió construyendo el
   classpath de ejecución con `mvn dependency:build-classpath` e
   invocando la clase de demo directamente con `java -cp ...`.
2. **`Application` como clase de entrada directa.** Al corregir lo
   anterior, el segundo intento invocó `java -cp ... DemoVisualizacion`
   (la clase que extiende `javafx.application.Application`) directamente,
   lo que falló con `Error: JavaFX runtime components are missing` — un
   comportamiento conocido de JavaFX: si la clase invocada por la JVM
   extiende `Application` y no está en el *module-path* (solo en el
   *classpath*, que es como la trae Maven), la JVM aborta con ese mensaje
   aunque los `.jar` de JavaFX sí estén presentes. Se corrigió agregando
   una clase lanzadora plana (`DemoVisualizacionLauncher`) que no extiende
   `Application` y solo llama a `Application.launch(...)` — el mismo
   patrón que ya se había usado para `Main`/`App` por esta misma razón.
3. `[COMPLETAR: al menos un caso adicional detectado por el otro
   integrante durante las Misiones 2 y 4, o durante la GUI]`

## Qué aprendió cada integrante

- **Evelyn Gómez:** `[COMPLETAR: 2-4 frases honestas sobre algo que no
  sabías antes de este proyecto — puede ser sobre grafos, sobre JavaFX,
  sobre Maven, o sobre cómo trabajar con un asistente de IA]`
- `[COMPLETAR: nombre del segundo integrante]:` `[COMPLETAR]`
