package com.proyecto.juegoudp.sonido;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * Punto central de audio: música de fondo, efecto de gol y volúmenes (patrón singleton).
 */
public class GestorSonidos {
    private static GestorSonidos instancia;
    private Sound sonidoGol;
    private Music musicaFondo;
    private boolean sonidosDisponibles = true;
    private boolean musicaDisponible = true;
    private float volumenEfectos = 1.0f;
    private float volumenMusica = 0.3f;
    private boolean musicaActivada = true;
    /** Si es {@code true}, música y efectos no se oyen (volumen de salida 0). */
    private boolean silenciado = false;

    private GestorSonidos() {
        cargarSonidos();
        cargarMusica();
    }

    /**
     * Patrón Singleton para tener una única instancia del gestor
     */
    public static GestorSonidos getInstancia() {
        if (instancia == null) {
            instancia = new GestorSonidos();
        }
        return instancia;
    }

    /**
     * Carga todos los efectos de sonido del juego
     */
    private void cargarSonidos() {
        try {
            FileHandle archivoSonido = Gdx.files.internal("sonidos/gol.wav");
            if (archivoSonido.exists()) {
                sonidoGol = Gdx.audio.newSound(archivoSonido);
                System.out.println("[GestorSonidos] Sonido de gol cargado correctamente");
                System.out.println("[GestorSonidos] Ruta del sonido: " + archivoSonido.path());
            } else {
                System.err.println("[GestorSonidos] No se encontró el archivo: sonidos/gol.wav");
                System.err.println("[GestorSonidos] Buscando en: " + Gdx.files.internal("sonidos").exists());
                sonidosDisponibles = false;
            }
        } catch (Exception e) {
            System.err.println("[GestorSonidos] Error al cargar el sonido: " + e.getMessage());
            sonidosDisponibles = false;
        }
    }

    /**
     * Carga la música de fondo del juego
     */
    private void cargarMusica() {
        try {
            FileHandle archivoMusica = Gdx.files.internal("sonidos/sonidofondo.wav");
            if (archivoMusica.exists()) {
                musicaFondo = Gdx.audio.newMusic(archivoMusica);
                musicaFondo.setLooping(true);
                musicaFondo.setVolume(volumenMusicaSalida());
                System.out.println("[GestorSonidos] Música de fondo cargada correctamente");
            } else {
                System.err.println("[GestorSonidos] No se encontró el archivo: sonidos/sonidofondo.wav");
                musicaDisponible = false;
            }
        } catch (Exception e) {
            System.err.println("[GestorSonidos] Error al cargar la música: " + e.getMessage());
            musicaDisponible = false;
        }
    }

    /**
     * Reproduce el sonido de gol con volumen alto
     */
    public void reproducirGol() {
        float vol = volumenEfectosSalida();
        if (vol <= 0f) {
            return;
        }
        if (sonidosDisponibles && sonidoGol != null) {
            try {
                long id = sonidoGol.play(vol);
                sonidoGol.setVolume(id, vol);
                System.out.println("[GestorSonidos] Reproduciendo sonido de gol con volumen: " + vol);
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al reproducir sonido: " + e.getMessage());
            }
        } else {
            System.err.println("[GestorSonidos] No se pudo reproducir el sonido - disponible: " + sonidosDisponibles + ", sonido: " + sonidoGol);
        }
    }

    /**
     * Reproduce el sonido de gol con volumen específico
     * @param volumen Valor entre 0.0 y 1.0
     */
    public void reproducirGol(float volumen) {
        float vol = Math.max(0f, Math.min(3f, volumen)) * (silenciado ? 0f : 1f);
        if (vol <= 0f) {
            return;
        }
        if (sonidosDisponibles && sonidoGol != null) {
            try {
                long id = sonidoGol.play(vol);
                sonidoGol.setVolume(id, vol);
                System.out.println("[GestorSonidos] Reproduciendo sonido de gol con volumen personalizado: " + vol);
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al reproducir sonido: " + e.getMessage());
            }
        }
    }

    /**
     * Inicia la reproducción de la música de fondo
     */
    public void iniciarMusicaFondo() {
        if (musicaActivada && musicaDisponible && musicaFondo != null) {
            try {
                if (!musicaFondo.isPlaying()) {
                    musicaFondo.setVolume(volumenMusicaSalida());
                    musicaFondo.play();
                    System.out.println("[GestorSonidos] Música de fondo iniciada con volumen: " + volumenMusicaSalida());
                }
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al iniciar música: " + e.getMessage());
            }
        }
    }

    /**
     * Detiene la reproducción de la música de fondo
     */
    public void detenerMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            try {
                musicaFondo.stop();
                System.out.println("[GestorSonidos] Música de fondo detenida");
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al detener música: " + e.getMessage());
            }
        }
    }

    /**
     * Pausa la música de fondo
     */
    public void pausarMusicaFondo() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            try {
                musicaFondo.pause();
                System.out.println("[GestorSonidos] Música de fondo pausada");
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al pausar música: " + e.getMessage());
            }
        }
    }

    /**
     * Reanuda la música de fondo
     */
    public void reanudarMusicaFondo() {
        if (musicaActivada && musicaDisponible && musicaFondo != null) {
            try {
                if (!musicaFondo.isPlaying()) {
                    musicaFondo.setVolume(volumenMusicaSalida());
                    musicaFondo.play();
                    System.out.println("[GestorSonidos] Música de fondo reanudada");
                }
            } catch (Exception e) {
                System.err.println("[GestorSonidos] Error al reanudar música: " + e.getMessage());
            }
        }
    }

    /**
     * Establece el volumen de la música de fondo
     */
    public void setVolumenMusica(float volumen) {
        this.volumenMusica = Math.max(0f, Math.min(1f, volumen));
        aplicarVolumenMusicaEnReproductor();
        System.out.println("[GestorSonidos] Volumen de música (preferencia) ajustado a: " + this.volumenMusica);
    }

    /**
     * Establece el volumen de los efectos de sonido
     */
    public void setVolumenEfectos(float volumen) {
        this.volumenEfectos = Math.max(0f, Math.min(1f, volumen));
        System.out.println("[GestorSonidos] Volumen de efectos ajustado a: " + this.volumenEfectos);
    }

    /** Volumen real de la música según silencio y si la música está permitida. */
    private float volumenMusicaSalida() {
        if (!musicaActivada || silenciado) {
            return 0f;
        }
        return volumenMusica;
    }

    /** Volumen real de efectos (0 si está silenciado). */
    private float volumenEfectosSalida() {
        return silenciado ? 0f : volumenEfectos;
    }

    private void aplicarVolumenMusicaEnReproductor() {
        if (musicaFondo != null) {
            musicaFondo.setVolume(volumenMusicaSalida());
        }
    }

    /**
     * Silencia o restaura todo el audio (música de fondo y efectos).
     */
    public void setSilenciado(boolean silenciado) {
        this.silenciado = silenciado;
        aplicarVolumenMusicaEnReproductor();
    }

    public void alternarSilencio() {
        setSilenciado(!silenciado);
    }

    public boolean isSilenciado() {
        return silenciado;
    }

    /**
     * Activa o desactiva la música de fondo
     */
    public void setMusicaActivada(boolean activada) {
        this.musicaActivada = activada;
        if (activada) {
            iniciarMusicaFondo();
        } else {
            detenerMusicaFondo();
        }
    }

    /**
     * Verifica si la música de fondo está sonando
     */
    public boolean isMusicaSonando() {
        return musicaFondo != null && musicaFondo.isPlaying();
    }

    /**
     * Libera los recursos de sonido y música
     */
    public void dispose() {
        if (sonidoGol != null) {
            sonidoGol.dispose();
            sonidoGol = null;
        }
        if (musicaFondo != null) {
            musicaFondo.dispose();
            musicaFondo = null;
        }
    }
}
