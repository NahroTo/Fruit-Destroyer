package com.nahroto.fruitdestroyer.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nahroto.fruitdestroyer.Constants;

public class Bullet
{
    public static final int VELOCITY = 700;

    public static int magSize = 30;
    public static int rateOfFire = 200;
    public static int damage = 16;

    public static Array<Bullet> totalBullets = new Array<Bullet>();
    public static Array<Bullet> currentBullets = new Array<Bullet>();

    private Vector2 position;
    private Vector2 velocity;
    private Sprite sprite;
    private Polygon bounds;

    public boolean isOutOfScreen;
    public boolean isUsed;

    public Bullet(Sprite sprite)
    {
        this.sprite = sprite;
        position = new Vector2();
        velocity = new Vector2();
        bounds = new Polygon(new float[]
                {
                5, 5,
                5, 5 + 10,
                5 + 10, 5 + 10,
                5 + 10, 5
                });
        isOutOfScreen = false;
        isUsed = false;
    }


    public void update(float delta)
    {
        updateBounds();
        applyVelocityToPosition(delta);
        isOutOfScreen();
    }

    private void applyVelocityToPosition(float delta)
    {
        velocity.scl(delta);
        position.add(velocity);
        velocity.scl(1 / delta);
    }

    private void isOutOfScreen()
    {
        if (position.x > Constants.V_WIDTH || position.x  + sprite.getWidth() < 0 || position.y > Constants.V_HEIGHT || position.y  + sprite.getHeight() < 0)
            isOutOfScreen = true;
        else
            isOutOfScreen = false;
    }

    private void updateBounds()
    {
        bounds.setPosition(this.position.x, this.position.y);
        // System.out.println(bounds.width + " " + bounds.height);
    }

    public void render(SpriteBatch batch)
    {
        batch.draw(sprite, position.x, position.y);
    }


    public void setPosition(float x, float y)
    {
        position.set(x, y);
    }

    public Vector2 getPosition()
    {
        return position;
    }

    public void setVelocity(float x, float y)
    {
        velocity.set(x, y);
    }

    public Polygon getBounds()
    {
        return bounds;
    }

    public int getDamage()
    {
        return damage;
    }
}
