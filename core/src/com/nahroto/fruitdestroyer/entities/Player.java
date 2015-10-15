package com.nahroto.fruitdestroyer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.nahroto.fruitdestroyer.Application;
import com.nahroto.fruitdestroyer.Constants;

public class Player
{
    private final Application APP;

    private final int KICK = 15;

    private Sprite sprite;
    private Sprite flashSprite;

    private Vector2 unprojectedCoordinates;

    private float deltaX;
    private float deltaY;

    private float flashPositionX;
    private float flashPositionY;

    private float angle;

    private long timeSinceShot;
    private long timeSinceReload;

    private boolean offsetNeeded;
    private boolean flashNeeded;
    private boolean reloading;

    public Integer ammo;

    private Sound shotSFX;
    private Sound emptySFX;
    private Sound reloadSFX;

    private Rectangle bounds;

    public Player(Sprite sprite, Sprite flashSprite, final Application APP)
    {
        this.APP = APP;

        this.sprite = sprite;
        this.sprite.setPosition(Constants.V_WIDTH / 2 - 32, Constants.V_HEIGHT / 2 - 28);
        this.sprite.setOrigin(32, 28);

        this.flashSprite = flashSprite;
        this.flashSprite.setOrigin(3, 18);

        bounds = new Rectangle(this.sprite.getX(), this.sprite.getY(), 43, 47);

        shotSFX = APP.assets.get("sounds/shot.wav", Sound.class);
        emptySFX = APP.assets.get("sounds/empty2.wav", Sound.class);
        reloadSFX = APP.assets.get("sounds/reload.wav", Sound.class);

        unprojectedCoordinates = new Vector2();

        offsetNeeded = false;
        flashNeeded = false;
        reloading = false;

        ammo = new Integer(30);
    }

    public void update()
    {
        if (offsetNeeded && System.currentTimeMillis() - timeSinceShot > 50)
        {
            this.sprite.setPosition(Constants.V_WIDTH / 2 - 32, Constants.V_HEIGHT / 2 - 28);
            offsetNeeded = false;
        }

        if (flashNeeded && System.currentTimeMillis() - timeSinceShot > 50)
            flashNeeded = false;

        if (flashNeeded)
            flashSprite.setPosition(Constants.V_WIDTH / 2 + flashPositionX, Constants.V_HEIGHT / 2 + flashPositionY - (35 / 2));

        if (reloading && System.currentTimeMillis() - timeSinceReload > 1850)
        {
            ammo = 30;
            reloading = false;
        }

        bounds.setPosition(sprite.getX(), sprite.getY());
    }

    public void followFinger()
    {
        unprojectedCoordinates.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        APP.viewport.unproject(unprojectedCoordinates);

        deltaX = unprojectedCoordinates.x - Constants.V_WIDTH / 2;
        deltaY = unprojectedCoordinates.y - Constants.V_HEIGHT / 2;

        angle = (MathUtils.atan2(deltaX, deltaY) * MathUtils.radiansToDegrees) - 90;

        // System.out.println(angle);
        sprite.setRotation(angle);
    }

    public void shoot()
    {
        if (ammo > 0) // IF THERE IS AMMO
        {
            if (!reloading)
            {
                shotSFX.play();

                ammo--; // REDUCE AMMO BY 1

                if (offsetNeeded == false)
                    timeSinceShot = System.currentTimeMillis();

                offsetNeeded = true;
                flashNeeded = true;

                // System.out.println(Bullet.totalBullets.get(0).isUsed);
                for (int i = 0; i < Bullet.totalBullets.size; i++)
                {
                    if (Bullet.totalBullets.get(i).isUsed == false)
                    {
                        Bullet.totalBullets.get(i).isOutOfScreen = false;
                        Bullet.totalBullets.get(i).isUsed = true;

                        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

                        deltaY *= -1;

                        float directionX = deltaX / length;
                        float directionY = deltaY / length;

                        float bulletPositionX = (MathUtils.cos(angle * MathUtils.degreesToRadians) * 85);
                        float bulletPositionY = (MathUtils.sin(angle * MathUtils.degreesToRadians) * 85);

                        flashPositionX = (MathUtils.cos(angle * MathUtils.degreesToRadians) * 80);
                        flashPositionY = (MathUtils.sin(angle * MathUtils.degreesToRadians) * 80);

                        flashSprite.setRotation(angle);
                        flashSprite.setPosition(Constants.V_WIDTH / 2 + flashPositionX - 3, Constants.V_HEIGHT / 2 + flashPositionY - (35 / 2));

                        // OFFSET PLAYER TO GIVE KICK
                        offsetBack();

                        Bullet.totalBullets.get(i).setPosition((Constants.V_WIDTH / 2 + bulletPositionX) - 10, (Constants.V_HEIGHT / 2 + bulletPositionY) - 10);
                        Bullet.totalBullets.get(i).setVelocity(directionX * Bullet.VELOCITY, directionY * Bullet.VELOCITY);

                        Bullet.currentBullets.add(Bullet.totalBullets.get(i));
                        break;
                    }
                }
            }
        }

        else if (!reloading)
            emptySFX.play();
    }

    public void reload()
    {
        if (!reloading && ammo != 30)
        {
            reloading = true;
            reloadSFX.play();
            timeSinceReload = System.currentTimeMillis();
        }
    }

    private void offsetBack()
    {
        sprite.translateX(-(MathUtils.cos(angle * MathUtils.degreesToRadians) * KICK));
        sprite.translateY(-(MathUtils.sin(angle * MathUtils.degreesToRadians) * KICK));
    }


    public void render(SpriteBatch batch)
    {
        sprite.draw(batch);
        if (flashNeeded)
            flashSprite.draw(batch);
    }

    public boolean isReloading()
    {
        return reloading;
    }

    public Rectangle getBounds()
    {
        return bounds;
    }
}
