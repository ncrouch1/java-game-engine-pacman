package com.jgegroup.pacman.objects.characters;

import com.jgegroup.pacman.KeyHandler;
import com.jgegroup.pacman.MainScene;
import com.jgegroup.pacman.objects.Entity;
import com.jgegroup.pacman.objects.Enums.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Pac extends Entity {

    private MainScene mainScene;
    private KeyHandler keyHandler;

    public Pac(MainScene mainScene, KeyHandler keyHandler) {
        this.mainScene = mainScene;
        this.keyHandler = keyHandler;
        setDefaultValues();
        setPacImage();
    }

    public void setDefaultValues() {
        x = 300;
        y = 200;
        speed = 1;
        collision_range = new Rectangle(0, 0, 31, 31);
        direction = Direction.STOP;
    }

    public void setPacImage() {
        // TODO
    }

    public void update() {
        setDefaultDirection(keyHandler.movement);

        collisionDetected = mainScene.collisionChecker.checkTile(this);
        updatePosition(collisionDetected);
        System.out.println("Entity collisionDetected is " + collisionDetected);
    }

    public void redraw(GraphicsContext painter) {
        painter.clearRect(x - 5 , y - 5, mainScene.RESOLUTION_VERTICAL, mainScene.RESOLUTION_HORIZONTAL );
        painter.setFill(Color.WHITE);
        painter.fillRect(x, y, 32, 32);
//        painter.clearRect(x - speed, y - speed, mainScene.RESOLUTION_HORIZONTAL, mainScene.RESOLUTION_VERTICAL);
//        painter.drawImage(up, x, y, 400, 100);
    }

    public void setDefaultDirection(Direction key) {
        switch (key) {
            case UP:
                direction = Direction.UP;
                break;
            case DOWN:
                direction = Direction.DOWN;
                break;
            case LEFT:
                direction = Direction.LEFT;
                break;
            case RIGHT:
                direction = Direction.RIGHT;
                break;
            default:
                break;
        }
    }

    public void updatePosition(boolean collisionDetected) {
        if (collisionDetected == false) {
            switch (keyHandler.movement) {
                case UP:
                    this.y -= speed;
                    break;
                case DOWN:
                    this.y += speed;
                    break;
                case LEFT:
                    this.x -= speed;
                    break;
                case RIGHT:
                    this.x += speed;
                    break;
                default:
                    break;
            }
        }
    }



}