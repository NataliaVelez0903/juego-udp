package com.proyecto.juegoudp.sonido;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * Administra los recursos de audio del juego, incluyendo efectos de sonido y música de fondo.
 *
 * Esta clase centraliza la carga, reproducción, pausa, detención y liberación de recursos
 * de audio. También permite controlar el volumen de la música y de los efectos, así como
 * activar o desactivar el sonido general del sistema.
 *
 *
 * Su implementación sigue el patrón Singleton, de modo que solo exista una única
 * instancia compartida del gestor de sonidos durante toda la ejecución del juego.
 *
 *
 * @author Natalia <natalia.velezo@autonoma.edu.co>
 * @author Sebastian <sebastian.villanedag@autonoma.edu.co>
 * @author Luis <luisc.gallegom@autonoma.edu.co>
 * @author Juan Jose <juanj.giraldot@autonoma.edu.co
 * @version 1.0
 * since 04/04/2026
 */
public class GestorSonidos {
    /** Única instancia del gestor de sonidos. */
    private static GestorSonidos instancia;

    /** Efecto de sonido utilizado al registrar un gol. */
    private Sound sonidoGol;

    /** Música de fondo reproducida durante la partida. */
    private Music musicaFondo;

    /** Indica si los efectos de sonido están disponibles para su uso. */
    private boolean sonidosDisponibles = true;

    /** Indica si la música de fondo está disponible para su uso. */
    private boolean musicaDisponible = true;

    /** Volumen configurado para los efectos de sonido. */
    private float volumenEfectos = 1.0f;

    /** Volumen configurado para la música de fondo. */
    private float volumenMusica = 0.3f;

    /** Indica si la música de fondo está activada. */
    private boolean musicaActivada = true;

    /**
     * Indica si todo el sistema de audio está silenciado.
     *
     * Cuando este atributo vale true, tanto la música como los efectos
     * se reproducen con volumen de salida igual a 0.
     *
     */
    private boolean silenciado = false;

    /**
     * Construye el gestor de sonidos e inicializa la carga de efectos y música.
     */
    private GestorSonidos() {
        cargarSonidos();
        cargarMusica();
    }

    /**
     * Obtiene la única instancia del gestor de sonidos.
     *
     * Si la instancia aún no existe, se crea en la primera invocación.
     *
     *
     * @return la instancia única de GestorSonidos
     */
    public static GestorSonidos getInstancia() {
        if (instancia == null) {
            instancia = new GestorSonidos();
        }
        return instancia;
    }

    /**
     * Carga los efectos de sonido necesarios para el juego.
     *
     * Actualmente intenta cargar el archivo sonidos/gol.wav. Si el archivo
     * no existe o se produce un error durante la carga, el sistema marca los sonidos
     * como no disponibles.
     *
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
     * Carga la música de fondo del juego.
     *
     * Actualmente intenta cargar el archivo sonidos/sonidofondo.wav. Si la carga
     * se realiza correctamente, la música se configura en modo repetición continua.
     * En caso de error o inexistencia del archivo, se marca la música como no disponible.
     *
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
     * Reproduce el efecto de sonido asociado al gol usando el volumen de efectos configurado.
     *
     * Si el sistema está silenciado o el recurso no está disponible, no realiza ninguna acción.
     *
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
     * Reproduce el efecto de sonido asociado al gol con un volumen específico.
     *
     * El valor recibido se limita al rango entre 0.0 y 3.0. Si el sistema
     * está silenciado, el volumen de salida será 0.
     *
     *
     * @param volumen volumen deseado para la reproducción del sonido
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
     * Inicia la reproducción de la música de fondo.
     *
     * La música solo se reproduce si está activada, disponible y no se encuentra ya sonando.
     *
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
     * Detiene completamente la reproducción de la música de fondo.
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
     * Pausa la reproducción actual de la música de fondo.
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
     * Reanuda la reproducción de la música de fondo si está activada y disponible.
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
     * Establece el volumen preferido para la música de fondo.
     *
     * El valor recibido se ajusta automáticamente al rango entre 0.0 y 1.0
     * Luego se aplica el volumen real al reproductor de música.
     *
     *
     * @param volumen nuevo volumen deseado para la música
     */
    public void setVolumenMusica(float volumen) {
        this.volumenMusica = Math.max(0f, Math.min(1f, volumen));
        aplicarVolumenMusicaEnReproductor();
        System.out.println("[GestorSonidos] Volumen de música (preferencia) ajustado a: " + this.volumenMusica);
    }

    /**
     * Establece el volumen preferido para los efectos de sonido.
     *
     * El valor recibido se ajusta automáticamente al rango entre 0.0 y 1.0
     *
     *
     * @param volumen nuevo volumen deseado para los efectos
     */
    public void setVolumenEfectos(float volumen) {
        this.volumenEfectos = Math.max(0f, Math.min(1f, volumen));
        System.out.println("[GestorSonidos] Volumen de efectos ajustado a: " + this.volumenEfectos);
    }

    /**
     * Calcula el volumen real de salida de la música.
     *
     * Si la música está desactivada o el sistema está silenciado, el volumen de salida será
     * 0. En caso contrario, devuelve el volumen configurado para la música.
     *
     *
     * @return volumen efectivo de salida para la música
     */
    private float volumenMusicaSalida() {
        if (!musicaActivada || silenciado) {
            return 0f;
        }
        return volumenMusica;
    }

    /**
     * Calcula el volumen real de salida para los efectos de sonido.
     *
     * @return volumen efectivo de salida para los efectos; 0 si el sistema está silenciado
     */
    private float volumenEfectosSalida() {
        return silenciado ? 0f : volumenEfectos;
    }

    /**
     * Aplica el volumen real calculado al reproductor de música de fondo.
     */
    private void aplicarVolumenMusicaEnReproductor() {
        if (musicaFondo != null) {
            musicaFondo.setVolume(volumenMusicaSalida());
        }
    }

    /**
     * Activa o desactiva el silencio global del sistema de audio.
     *
     * Esta operación afecta tanto la música de fondo como los efectos de sonido.
     *
     *
     * @param silenciado true para silenciar todo el audio, {@code false} para restaurarlo
     */
    public void setSilenciado(boolean silenciado) {
        this.silenciado = silenciado;
        aplicarVolumenMusicaEnReproductor();
    }

    /**
     * Alterna el estado de silencio global del sistema de audio.
     *
     * Si estaba silenciado, lo restaura. Si no estaba silenciado, lo silencia.
     *
     */
    public void alternarSilencio() {
        setSilenciado(!silenciado);
    }

    /**
     * Indica si el sistema de audio se encuentra silenciado.
     *
     * @return true si el audio está silenciado, false en caso contrario
     */
    public boolean isSilenciado() {
        return silenciado;
    }

    /**
     * Activa o desactiva la música de fondo.
     *
     * Si se activa, intenta iniciar su reproducción. Si se desactiva, la detiene.
     *
     *
     * @param activada true para activar la música, false para desactivarla
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
     * Verifica si la música de fondo se está reproduciendo actualmente.
     *
     * @return true si la música está sonando, false en caso contrario
     */
    public boolean isMusicaSonando() {
        return musicaFondo != null && musicaFondo.isPlaying();
    }

    /**
     * Libera los recursos de audio utilizados por el gestor.
     *
     * Este método debe invocarse cuando el sistema de sonido ya no vaya a utilizarse,
     * con el fin de evitar fugas de memoria y liberar correctamente los recursos nativos.
     *
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
