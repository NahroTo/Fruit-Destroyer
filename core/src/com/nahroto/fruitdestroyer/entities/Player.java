package com.nahroto.fruitdestroyer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.nahroto.fruitdestroyer.Application;
import com.nahroto.fruitdestroyer.Constants;

public class Player
{
    private final Application APP;
    private Sprite sprite;
    private Vector2 unprojectedCoordinates;

    private float deltaX;
    private float deltaY;

    private Sound shotSFX;
    private double len;

    public Player(Sprite sprite, final Application APP)
    {
        this.APP = APP;

        this.sprite = sprite;
        this.sprite.setPosition(Constants.V_WIDTH / 2 - 32, Constants.V_HEIGHT / 2 - 28);
        this.sprite.setOrigin(32, 28);


        shotSFX = APP.assets.get("sounds/shot.wav", Sound.class);
        unprojectedCoordinates = new Vector2();
    }

    public void update()
    {
    }

    public void followFinger()
    {
        unprojectedCoordinates.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        APP.viewport.unproject(unprojectedCoordinates);

        deltaX = unprojectedCoordinates.x - Constants.V_WIDTH / 2;
        deltaY = unprojectedCoordinates.y - Constants.V_HEIGHT / 2;

        sprite.setRotation((MathUtils.atan2(deltaX, deltaY) * MathUtils.radiansToDegrees) - 90);
    }

    public void shoot()
    {
        shotSFX.play();
        for (int i = 0; i < Bullet.totalBullets.size; i++)
        {
            if (Bullet.totalBullets.get(i).reserve = true)
            {
                Bullet.totalBullets.get(i).isOutOfScreen = false;
                Bullet.totalBullets.get(i).reserve = false;

                len = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                deltaX /= len;
                deltaY /= len;

                Bullet.totalBullets.get(i).setPosition(Constants.V_WIDTH / 2 - 10, Constants.V_HEIGHT / 2 - 10);
                Bullet.totalBullets.get(i).setVelocity(deltaX * 200, deltaY * -1 * 200);

                Bullet.currentBullets.add(Bullet.totalBullets.get(i));
                System.out.println("bullet added");
                System.out.println(Bullet.currentBullets.size);
                break;
            }
        }
    }

    public void render(SpriteBatch batch)
    {
        sprite.draw(batch);
    }
}
