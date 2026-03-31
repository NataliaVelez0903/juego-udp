# Juego Multijugador Sockets UDP

## 📌 Descripción
Este proyecto consiste en el desarrollo de un juego multijugador en tiempo real, donde varios jugadores interactúan desde diferentes computadores.

La comunicación entre jugadores se realiza mediante sockets UDP, permitiendo la sincronización de acciones y eventos en tiempo real.

El juego está basado en la captura de objetos, donde cada jugador debe arrastrar objetos hacia su zona para acumular puntos.

---

## Objetivo del juego
Capturar la mayor cantidad de objetos llevándolos a la zona del jugador antes de que el tiempo se agote.

---

## Reglas del juego
1. Cada jugador puede mover objetos utilizando el mouse
2. Un objeto solo puede ser controlada por un jugador a la vez
3. Cuando un objeto entra a la zona o base de un jugador, se suma un punto
4. Los objetos pueden colisionar con obstáculos dentro del tablero
5. La partida tiene una duración limitada de tiempo
6. Gana el jugador con mayor puntaje al finalizar la partida


---

## Jugadores
- Mínimo: 2 jugadores
- Máximo: 4 jugadores

Cada jugador ejecuta su propia instancia del juego.

---

## Comunicación (UDP)
El juego utiliza comunicación mediante sockets UDP.

- Un jugador actúa como **host** (el que crea la partida)
- Los demás como **clientes** (los que se unen a la partida)
- Se envían eventos como:
    - movimiento de objetos
    - puntajes
    - acciones del jugador
- Todo se sincroniza en tiempo real

---

## Tecnologías utilizadas
- Java 21
- libGDX
- UDP (DatagramSocket)
- Gradle
- Git & GitHub

---

## Arquitectura del proyecto

El sistema está organizado en módulos:

- `ui` → interfaz gráfica
- `game` → lógica del juego
- `network` → comunicación UDP
- `model` → entidades del juego
- `audio` → manejo de sonidos

---

## Patrones de diseño

- ...

---

## Principios SOLID

- Separación de responsabilidades
- Código modular
- Uso de abstracciones
- Bajo acoplamiento

---

## Cómo ejecutar el proyecto

1. Clonar el repositorio:
"git clone https://github.com/NataliaVelez0903/juego-udp.git"
2. Abrir el proyecto en Intellij o NetBeans
3. Esperar a que Gradle descargue las dependencias
4. Ejecutar
"Lwjgl3Launcher.java"

## Flujo de trabajo 
- main -> Versión estable 
- develop -> Integración
- feature/* -> desarrollo de funcionalidades 

## Estructura del repositorio 
- ...
---

## Integrantes
- Sebastian Villaneda Gutierrez
- Natalia Velez Orjuela
- Juan José Giraldo Tabares 
- Luis Carlos Gallego Morales
