# Peloteros — juego multijugador con UDP

Juego en tiempo real para **2 a 4 jugadores**. Un participante hace de **anfitrión** (servidor UDP autoritativo); el resto se conectan como **clientes**. La partida tiene **duración configurable**, **sala de espera** hasta completar jugadores y **pantalla de resultados** al terminar el tiempo.

**Alcance:** mecánica centrada en **mover**, **arrastrar pelotas sueltas** y **meter gol** en la zona propia. **No** hay power-ups, objetos recolectables ni “zona caliente” con doble puntaje.

---

## Requisitos

- **JDK 21**
- **Gradle** (wrapper incluido: `gradlew` / `gradlew.bat`)

---

## Cómo ejecutar

En la raíz del proyecto:

```bash
./gradlew :lwjgl3:run
```

En Windows (PowerShell o CMD):

```bat
gradlew.bat :lwjgl3:run
```

Alternativa: abrir el proyecto en IntelliJ IDEA / Android Studio y ejecutar la clase  
`lwjgl3/src/main/java/com/proyecto/juegoudp/lwjgl3/Lwjgl3Launcher.java`.

**Puerto UDP:** `5000` (definido en `Constantes.PUERTO_UDP`). El anfitrión debe indicar su **IP de red local** a quienes se unen.

---

## Cómo jugar (resumen)

| Acción | Entrada |
|--------|---------|
| Mover al jugador | **W A S D** |
| Coger / arrastrar / soltar pelota libre | **Ratón** (clic y arrastre) |

- Mete la pelota en **tu zona de gol** (marcadores a los lados del campo) para sumar puntos.
- La partida termina cuando **se agota el tiempo** configurado por el anfitrión.
- Gana quien tenga **más puntaje** al final.

---

## Arquitectura del código

El proyecto está organizado por **capas** (presentación → dominio → infraestructura):

```
com.proyecto.juegoudp
├── pantallas/          # libGDX: menú, espera, partida, final
│   ├── juego/          # Entrada, render, movimiento local, gestor de estado de red
│   ├── espera/         # Conexión de sala (ConexionSalaUdp, EscuchaSala)
│   ├── menu/           # Validación del menú (ValidacionMenu)
│   └── ui/             # FabricaSkinBasico, IFabricaSkin
├── modelo/             # EstadoJuego, Jugador, Pelota, Zona, ConfiguracionPartida (sin sockets)
├── red/                # ClienteUDP, ServidorUDP, Mensaje, serialización y análisis de STATE
├── sonido/             # GestorSonidos (singleton)
└── utilidades/         # Constantes (puerto, máx. jugadores, frecuencia de envío)
```

**Flujo de red:** todos los jugadores (incluido el anfitrión) usan `ClienteUDP`. Solo el anfitrión ejecuta `ServidorUDP`, que difunde instantáneas `STATE|...` y procesa mensajes (`UNIRSE`, `MOVER_JUGADOR`, pelota, etc.).

**Patrones destacados:** delegación en pantalla de partida (`ControladorEntradaJuego`, `RenderizadorPartida`, `GestorEstadoRedPartida`), factoría de apariencia (`IFabricaSkin`), procesador y serializador de estado en el servidor, callbacks hacia la UI en la sala de espera.

---

## Estructura de módulos Gradle

| Módulo | Rol |
|--------|-----|
| `core` | Lógica, modelo, red, pantallas compartidas |
| `lwjgl3` | Escritorio: launcher y empaquetado de assets |

---

## Documentación adicional

- `DOCUMENTACION_ENTREGA.md`: base para entrega académica o informe en PDF.

---

## Git (flujo sugerido)

- `main` — versión estable  
- `develop` — integración  
- `feature/*` — desarrollo por funcionalidad  

---

## Integrantes

- Sebastián Villaneda Gutiérrez  
- Natalia Vélez Orjuela  
- Juan José Giraldo Tabares  
- Luis Carlos Gallego Morales  

---

## Licencia

Según lo definido por el equipo / la institución.
