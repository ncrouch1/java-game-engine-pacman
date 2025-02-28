package com.jgegroup.pacman;

import com.jgegroup.GameConfig.config.Settings;
import com.jgegroup.pacman.objects.Map;

import com.jgegroup.pacman.objects.UI;
import com.jgegroup.pacman.objects.characters.Ghost;
import com.jgegroup.pacman.objects.characters.Pacman;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;


public class GameScene implements Runnable {
    public static boolean gameFinished;
    private Thread gameThread;
    private static long FPS = 60;
    private static long targetTimePerFrame = 1000000000 / FPS;
    private static long lastTime = System.nanoTime();
    private static long currentTime;
    private static long elapsedTime;
    private static long sleepTime;
    public static final int TILE_SIZE = 32;
    public static int NUMBER_OF_TILE_COLUMN = 20;
    public static int NUMBER_OF_TILE_ROW = 28;

    public static final int RESOLUTION_HORIZONTAL = TILE_SIZE * NUMBER_OF_TILE_COLUMN; // 768
    public static final int RESOLUTION_VERTICAL = TILE_SIZE * NUMBER_OF_TILE_ROW; // 1024
    public CollisionChecker collisionChecker;
    public javafx.scene.Scene gameScene;

    private StackPane stackPane;

    Canvas gameCanvas;
    private GraphicsContext gamePainter;

    public Map map;
    private KeyHandler keyHandler;
    private Pacman pacman;
    private Ghost[] ghosts;
    private Color[] colors = {Color.RED, Color.BLUE, Color.YELLOW, Color.PINK};

    private int ghostNumber;

    public UI ui;

    private Settings settings;

    private final Media deathMedia = new Media(new File("res/sounds/death.mp3").toURI().toString());
    private final Media introMedia = new Media(new File("res/sounds/Intro.mp3").toURI().toString());
    private final MediaPlayer intro = new MediaPlayer(introMedia);
    private final MediaPlayer death = new MediaPlayer(deathMedia);

    public GameScene(int ghostNumber, Settings settings) {
        if (settings == null) {
            settings = new Settings();
        }
        this.settings = settings;
        stackPane = new StackPane();
        gameScene = new javafx.scene.Scene(stackPane, RESOLUTION_HORIZONTAL, RESOLUTION_VERTICAL, Color.BLACK);
        gameCanvas = new Canvas(RESOLUTION_HORIZONTAL, RESOLUTION_VERTICAL);
        gamePainter = gameCanvas.getGraphicsContext2D();
        addCanvasLayer(gameCanvas);

        ui = new UI(this);
        keyHandler = new KeyHandler();
        gameScene.setOnKeyPressed(keyHandler);
        this.ghostNumber = ghostNumber;
    }


    // FLOW: startThread() --> run() --> update() & redraw()


    /**
     * @@Author: Tung
     * Start thread. Called from Main class.
     */
    public void startThread() {
        init(settings);
        gameThread = new Thread(this);
        gameThread.start();
    }


    /**
     * @@Author: Tung
     * Init the game before run the game.
     */
    public void init(Settings settings) {
        if (settings.selectedMapSize()) {
            NUMBER_OF_TILE_ROW = Math.min(settings.getMapHeight(), NUMBER_OF_TILE_ROW);
            NUMBER_OF_TILE_COLUMN = Math.min(settings.getMapWidth(), NUMBER_OF_TILE_COLUMN);
        }

        map = Map.getMapInstance();
        map.createMap(settings);
        collisionChecker = new CollisionChecker(map);

        pacman = new Pacman(this, keyHandler);

        ghosts = new Ghost[ghostNumber];

        for (int i = 0; i < ghostNumber; i++) {
            ghosts[i] = new Ghost(10, this, colors[i % colors.length], pacman);
            ghosts[i].setSpawnPosition(9, 8 + i % colors.length);
        }
    }

    /**
     * @@Author: Tung, Noah, Ethan
     * Run the game after Init().
     */
    @Override
    public void run() {
        death.setVolume(0.25f);
        intro.setVolume(0.25f);
        intro.play();
        long start = System.currentTimeMillis();
        long wait = start + 4000;
        for (Ghost ghost : ghosts)
            ghost.setSpeed(0);
        pacman.setSpeed(0);
        redraw();
        while (start < wait) {
            start = System.currentTimeMillis();
        }
        if (settings.selectedLives())
            pacman.setLife(settings.getPacmanLives());
        if (settings.selectedPacmanSpeed())
            pacman.setSpeed(settings.getPacmanSpeed());
        if (settings.selectedGhostSpeed()) {
            for (int i = 0; i < ghostNumber; i++) {
                ghosts[i].setSpeed(settings.getGhostSpeed());
            }
        }
        while (pacman.getLives() >= 0) {
            update();
            redraw();
            controlFPS(); // DANGER!!!  REMOVE THIS CAUSE ATOMIC EXPLOSION
        }
        death.play();
        ui.displayGameFinish(getGamePainter());

    }

    /**
     * @@Author: Tung, Noah, Jesse
     * Main game's update(), control entities update().
     */
    public void update() {
        pacman.update();
        for (Ghost ghost : ghosts) {
            ghost.update();
        }

    }

    /**
     * @@Author: Tung
     * Main game's redraw(), control map, entities redraw().
     */
    public void redraw() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                map.drawMap(getGamePainter());
                map.drawDot(getGamePainter());
                pacman.redraw(getGamePainter());
                for (int i = 0; i < ghosts.length; i++) {
                    ghosts[i].redraw(getGamePainter());
                }
                ui.redraw(getGamePainter());
            }
        });
    }


    /**
     * @@Author: Tung
     * Attach canvas to game panel(stackPane)
     */
    public void addCanvasLayer(Canvas canvas) {
        this.stackPane.getChildren().add(canvas);
    }

    /**
     * @@Author: Noah, Tung
     * Control FPS
     */
    public void controlFPS() {
        currentTime = System.nanoTime();
        elapsedTime = currentTime - lastTime;
        lastTime = currentTime;
        sleepTime = targetTimePerFrame - elapsedTime;
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime / 1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Encapsulation

    public int getPacLives() {
        return pacman.getLives();
    }

    public int getPoints() {
        return pacman.getPoint();
    }

    public Canvas getCanvas() {
        return this.gameCanvas;
    }

    public GraphicsContext getGamePainter() {
        return this.gamePainter;
    }
}




