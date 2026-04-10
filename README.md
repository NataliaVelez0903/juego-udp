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
  `GestorSonidos` garantiza una única instancia global de audio.

- **Callback / Listener:**  
  `ClienteUDP` notifica eventos y estados mediante escuchas, desacoplando la red de la interfaz.

- **Delegación:**  
  `PantallaJuego` delega responsabilidades a componentes especializados como entrada, renderizado y estado de red.

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

- **S (SRP):** Separación entre UI, lógica y red.
- **O (OCP):** Posibilidad de extender reglas sin modificar la base.
- **L (LSP):** Las implementaciones respetan el comportamiento esperado de sus abstracciones.
- **I (ISP):** Interfaces pequeñas y específicas para cada necesidad.
- **D (DIP):** La UI depende de abstracciones, no de sockets directamente.

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