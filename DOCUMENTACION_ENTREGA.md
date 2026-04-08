# Proyecto multijugador UDP — documento base de entrega

## 1. Descripción del juego

Juego multijugador en tiempo real (**2 a 4 jugadores**) desarrollado en Java / libGDX con comunicación **UDP**. Los jugadores compiten llevando **pelotas** a sus **zonas de gol** para sumar puntos antes de que termine el **tiempo** configurado por el anfitrión. Incluye **sala de espera**, temporizador visible para todos y **pantalla final** con ranking.

**No** incluye power-ups, zona caliente ni modificadores temporales de puntuación: la mecánica es movimiento, pelotas (arrastre con ratón) y goles en zona.

## 2. Enlace al código fuente

- Repositorio: completar con la URL de GitHub del proyecto.

## 3. Reglas del juego (mínimo 5)

1. Solo un jugador controla una pelota a la vez (salvo pelotas sueltas en el campo).
2. Si una pelota entra en la zona de gol de un jugador, se suman puntos (valor fijo por gol).
3. El anfitrión configura la **duración** de la partida; el juego termina cuando el tiempo llega a cero.
4. Las **pelotas sueltas** en el campo se pueden **coger y arrastrar** con el ratón; al soltarlas conservan inercia.
5. Gana quien tenga **mayor puntaje** al finalizar el tiempo.

## 4. Arquitectura del sistema

- `pantallas/`: interfaz y flujo (`PantallaMenu`, `PantallaEspera`, `PantallaJuego`, `PantallaFinal`).
- `red/`: transporte UDP, serialización y servidor autoritativo (`ClienteUdp`, `ServidorUdp`, `Mensaje`, `TipoMensaje`, analizadores de `STATE`).
- `modelo/`: entidades y estado (`Jugador`, `Pelota`, `Zona`, `EstadoJuego`, `ConfiguracionPartida`).
- `sonido/`: audio centralizado (`GestorSonidos`).
- `utilidades/`: constantes (puerto UDP, límites de jugadores, frecuencia de envío).

## 5. Patrones de diseño usados

1. **Singleton:** `GestorSonidos` para una instancia global de audio.
2. **Observer / callback:** `ClienteUdp` notifica estado y mensajes mediante consumidores, desacoplando red de la UI.
3. **Delegación:** pantalla de partida separa entrada (`ControladorEntradaJuego`), dibujo (`RenderizadorPartida`) y estado de red (`GestorEstadoRedPartida`).

## 6. Comunicación UDP (flujo técnico)

- Los clientes envían acciones: `UNIRSE`, `MOVER_JUGADOR`, `TOMAR_PELOTA`, `MOVER_PELOTA`, `SOLTAR_PELOTA`.
- El servidor valida reglas, calcula puntajes y transmite instantáneas `STATE|...`.
- Los clientes aplican el `STATE` al modelo local para mantener la vista sincronizada.
- El servidor es **autoritativo** para reglas, tiempo y puntuación.

## 7. Principios SOLID (aplicación breve)

- **S (SRP):** separación entre pantallas, dominio y red.
- **O (OCP):** nuevas reglas pueden extenderse con nuevas clases / interfaces sin romper el contrato UDP base.
- **I (ISP):** interfaces pequeñas (p. ej. proveedor de datos para el HUD).
- **D (DIP):** la UI depende de abstracciones (proveedores, escuchas) y no del detalle del socket.

## 8. Interfaz y requisitos funcionales

- Menú: nombre, modo anfitrión/cliente, IP (cliente), número de jugadores y tiempo (anfitrión).
- Sala de espera: conteo de conectados hasta el mínimo requerido.
- Partida: puntajes, tiempo restante, controles indicados en pantalla.
- Sonido: música de fondo y efecto al marcar gol.
- Pantalla final: ganador, ranking y tiempo total.

## 9. Dificultades y soluciones

- **Sincronización en tiempo real con UDP:** instantáneas periódicas y servidor autoritativo.
- **Paquetes duplicados o desordenados:** secuencia en el `STATE` para ignorar datos viejos.

## 10. Evidencias sugeridas (capturas)

1. Menú inicial con opciones de anfitrión/cliente.
2. Sala de espera con jugadores conectados.
3. Partida en curso con puntajes y temporizador.
4. Pantalla final con ganador y ranking.
