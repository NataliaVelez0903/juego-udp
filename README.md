# Juego Multijugador UDP – libGDX

---

## Introducción

Este documento presenta la descripción técnica y funcional del proyecto **Juego Multijugador UDP**, desarrollado en Java utilizando libGDX. El objetivo es implementar un sistema de juego en tiempo real con múltiples jugadores conectados mediante comunicación UDP, aplicando principios de arquitectura de software, diseño modular y sincronización cliente-servidor.

---

## Descripción del juego

Juego multijugador en tiempo real (2 a 4 jugadores) desarrollado en Java/libGDX con comunicación UDP.

Los jugadores compiten llevando pelotas a sus zonas de gol para sumar puntos antes de que termine el tiempo configurado por el anfitrión.

Incluye:
- Sala de espera
- Temporizador visible para todos
- Pantalla final con ranking

La mecánica principal se basa en:
- Movimiento del jugador
- Arrastre de pelotas con el ratón
- Anotación en zonas de gol

---

## Enlace al código fuente

Repositorio:  
https://github.com/NataliaVelez0903/juego-udp.git

---

## Características principales

- Juego multijugador en tiempo real (2–4 jugadores)
- Comunicación mediante UDP
- Servidor autoritativo
- Sistema de puntaje en tiempo real
- Interacción con objetos (pelotas)
- Sala de espera antes de iniciar
- Pantalla final con resultados
- Arquitectura modular por capas

---

## Reglas del juego

1. Cada jugador controla únicamente su personaje dentro del campo.
2. Una pelota solo puede ser controlada por un jugador a la vez.
3. Si una pelota entra en la zona de gol de un jugador, se suman puntos.
4. La partida finaliza cuando el temporizador llega a cero.
5. Gana el jugador con mayor puntaje al finalizar el tiempo.

---

## Controles

- **W, A, S, D:** mover jugador
- **Clic sostenido:** tomar y arrastrar pelota
- **Soltar clic:** liberar pelota (con inercia)

---

## Arquitectura del sistema

El proyecto está organizado en los siguientes módulos:

- `pantallas/`  
  Maneja la interfaz y flujo del juego:
    - PantallaMenu
    - PantallaEspera
    - PantallaJuego
    - PantallaFinal

- `modelo/`  
  Representa el estado del juego:
    - Jugador
    - Pelota
    - Zona
    - EstadoJuego
    - ConfiguracionPartida

- `red/`  
  Gestiona la comunicación UDP:
    - ClienteUDP
    - ServidorUDP
    - Mensaje
    - TipoMensaje
    - Analizadores de estado (`STATE`)

- `sonido/`  
  Gestión centralizada de audio:
    - GestorSonidos

- `utilidades/`  
  Constantes y utilidades generales

---

## Patrones de diseño utilizados

- **Singleton:**  
  `Se utiliza en la clase GestorSonidos, garantizando una única instancia global para la gestión de audio dentro del juego. Esto evita la duplicación de recursos y centraliza el control del sonido.

- **Callback / Listener:**  
  `La clase ClienteUDP implementa un mecanismo de callbacks para notificar eventos y actualizaciones de estado a otros componentes del sistema. Esto permite desacoplar la lógica de red de la interfaz gráfica y facilita la comunicación entre módulos.

- **Delegación:**  
  `La clase PantallaJuego delega responsabilidades específicas en otras clases como ControladorEntradaJuego, RenderizadorPartida y GestorEstadoRedPartida. Esto permite dividir la lógica en componentes especializados, mejorando la claridad y mantenibilidad del código.
- **Arquitectura modular por capas:**
  `Arquitectura modular por capas
---

## Comunicación UDP (Sockets)

El sistema funciona bajo un modelo de servidor autoritativo:

1. Los clientes envían acciones:
    - `UNIRSE`
    - `MOVER_JUGADOR`
    - `TOMAR_PELOTA`
    - `MOVER_PELOTA`
    - `SOLTAR_PELOTA`

2. El servidor:
    - Valida reglas
    - Actualiza el estado
    - Calcula puntajes
    - Controla el tiempo

3. El servidor envía periódicamente:
    - `STATE|...` (instantánea completa del juego)

4. Los clientes:
    - Reciben el estado
    - Lo aplican localmente
    - Mantienen la vista sincronizada

---

## Principios SOLID aplicados

- **S (SRP):** Cada clase tiene una única responsabilidad bien definida. Por ejemplo, ServidorUDP maneja la lógica del servidor, ClienteUDP la comunicación del cliente y RenderizadorPartida la visualización del juego.
- **O (OCP):** El sistema está diseñado para ser extendido sin modificar su estructura base. Por ejemplo, el uso del enum TipoMensaje permite agregar nuevos tipos de mensajes sin alterar la lógica existente.
- **L (LSP):** Las clases que heredan de ScreenAdapter (como PantallaMenu, PantallaJuego, etc.) pueden ser utilizadas de manera intercambiable sin afectar el comportamiento del sistema.
- **I (ISP):** Se utilizan interfaces específicas como IConexionSala y EscuchaSala, evitando dependencias innecesarias y asegurando que cada clase implemente únicamente lo que necesita.
- **D (DIP):** Las clases dependen de abstracciones en lugar de implementaciones concretas. Por ejemplo, ConexionSalaUdp interactúa mediante la interfaz EscuchaSala, lo que reduce el acoplamiento entre la lógica de red y la interfaz.

---

## Interfaz y requisitos funcionales

### Menú
- Nombre del jugador
- Selección de anfitrión o cliente
- IP del host (cliente)
- Número de jugadores y tiempo (host)

### Sala de espera
- Visualización de jugadores conectados

### Partida
- Puntaje en tiempo real
- Temporizador visible
- Controles en pantalla

### Sonido
- Música de fondo
- Efecto al marcar gol

### Pantalla final
- Ranking
- Ganador
- Tiempo total

---

## Requisitos técnicos

- Java JDK 21
- Gradle
- libGDX
- Red local para pruebas multijugador

---

## Dificultades y soluciones

### Sincronización en tiempo real
- Problema: UDP no garantiza entrega ni orden
- Solución: uso de instantáneas (`STATE`) y servidor autoritativo

---

## Conclusión

El proyecto permitió desarrollar un sistema multijugador en tiempo real utilizando comunicación UDP, aplicando conceptos de arquitectura modular, diseño orientado a objetos y sincronización cliente-servidor.

El principal reto fue mantener la consistencia del estado en red, lo cual se abordó mediante un modelo de servidor autoritativo y el envío periódico de instantáneas del juego.

## Autores
- Juan Jose Giraldo
- Sebastian Villaneda
- Natalia Velez
- Luis Carlos Gallego
