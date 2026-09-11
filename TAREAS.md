# División del trabajo — The Feline Graph Chronicles

> Parcial de Lenguajes y Compiladores (EIA). Entrega: **13/09/2026 23:59** (repo + release tag).

## Reglas comunes (leerlas antes de escribir código)

- Java 17+, todo algoritmo implementado **desde cero** (sin JGraphT, GraphStream, etc.).
- El paquete `algoritmos` **no puede importar JavaFX**: todo debe correr desde un test.
- Entrada: flujo de tokens separados por espacios en blanco (no asumir tokens por línea).
- Salida: `Case #k: ...` exacta, carácter por carácter, ASCII plano, sin punto final.
- Pesos acumulados en `long`; nunca hacer aritmética sobre el centinela de "sin ruta".
- Entrada malformada → mensaje de error legible en la GUI, nunca stack trace.
- Commits pequeños y frecuentes: el historial de Git es evidencia evaluada (sección 7.4).
- Cada algoritmo lleva comentario con su complejidad y por qué es el correcto para su misión.
- Cada misión: mínimo un test JUnit con el sample del enunciado como valor esperado.

## Integrante A

- [x] Fase 1: esqueleto Maven + JavaFX + repo Git
- [x] Misión 1 (12%): parser de grilla, BFS con cola, DFS **iterativo con stack explícito**
      (orden fijo de vecinos: arriba, abajo, izquierda, derecha) + test (`BFS 18 DFS 32`)
- [x] Misión 3 (20%): Floyd-Warshall maximizante (con pase extra de pares no acotados),
      Bellman-Ford maximizante (propagación de ciclos positivos), **cross-check** entre ambos
      + tests (`110`, `Infinite churun!`, `-65`)
- [x] Visualización de la grilla de M1 (camino resaltado, hasta 50×50) —
      `VisualizadorGrilla`, verificado visualmente (BFS 18 / DFS 32 del sample)
- [x] Visualización de la matriz N×N de M3 (scrollable hasta N=100) —
      `VisualizadorMatrizFloydWarshall`, con aviso de discrepancia del cross-check
- [x] README.md (integrantes, comando único de build/run, estructura, decisiones)

## Integrante B

- [x] Misión 2 (12%): Dijkstra con `java.util.PriorityQueue` (obligatoria; el O(N²) no se acepta),
      grafo no dirigido, quedarse con la arista más barata o guardar todas
      + test (`100`, `150`, `Nina is very sad`)
- [x] Misión 4 (12%): Union-Find con path compression + union by size/rank, Kruskal ordenando
      aristas (O(C log C)) + test (`55`, y el caso `Limon cut too many cables`)
- [x] GUI base (14%): selector de las 4 misiones (App.java + tema.css), áreas de
      entrada/salida, botón "cargar sample", manejo de errores legible, tema gatuno —
      verificado corriendo la app real (Misiones 1, 2 y 3 probadas con captura de pantalla)
- [x] Visualización de grafos (16%, compartido): `VisualizadorGrafo` — layout circular,
      resalta la ruta (M2 Dijkstra, verificado en vivo), la ruta o el ciclo (M3, cableado y
      compilando) y el conjunto de aristas del MST (M4, mismo motor de dibujo que M2/M3 —
      pendiente de una verificación visual propia, revisar antes de la defensa)
- [ ] AI_USAGE.md: herramientas usadas, 2-3 prompts decisivos, ≥2 salidas erróneas de la IA
      y cómo se corrigieron, qué aprendió cada uno

## Contrato entre las partes

- Los tipos compartidos viven en `modelo` (grafo, aristas, resultados). Definirlos/ajustarlos
  de común acuerdo antes de cambiarlos.
- Cada misión expone en `misiones` un método `String resolver(String entrada)` que devuelve
  la salida exacta. La GUI solo llama a esos métodos: nunca implementa lógica de grafos.
- Trabajar en ramas por misión (`mision-1`, `mision-2`, ...) y hacer merge a `main` cuando
  el test del sample pase.

## Cómo compilar y correr

```
mvnw javafx:run    (Windows: mvnw.cmd javafx:run)
mvnw test          (corre todos los tests)
```

Solo se necesita un JDK 17+ instalado; el wrapper descarga Maven solo.
