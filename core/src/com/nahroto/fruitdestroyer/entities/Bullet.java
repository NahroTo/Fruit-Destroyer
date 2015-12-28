package com.nahroto.fruitdestroyer.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nahroto.fruitdestroyer.Constants;
import com.nahroto.fruitdestroyer.weapons.Weapon;
import com.nahroto.fruitdestroyer.weapons.WeaponList;

public class Bullet
{
    public static final int VELOCITY = 1100;

    private static final int START_DAMAGE = 10;
    private static final int START_ROF = 200;
    private static final int START_MAGSIZE = 20;
    private static final float START_RECOIL = 1f;

    private Vector2 velocity;
    private Sprite sprite;
    private Polygon bounds;
    private float[] vertices;

    private static Weapon weapon;

    public boolean isOutOfScreen;
    public boolean isUsed;

    public Bullet(Sprite sprite)
    {
        this.sprite = sprite;

        weapon = new Weapon(START_DAMAGE, START_ROF, START_MAGSIZE, START_RECOIL);

        velocity = new Vector2();

        vertices = new float[]{
                0, 0,
                0, 10,
                10, 10,
                10, 0 };

        bounds = new Polygon(vertices);
        isOutOfScreen = false;
        isUsed = false;
    }

    public void update(float delta)
    {
        applyVelocityToPosition(delta);
        updateBounds();
        isOutOfScreen();
    }

    private void applyVelocityToPosition(float delta)
    {
        velocity.scl(delta);
        sprite.translate(velocity.x, velocity.y);
        velocity.scl(1 / delta);
    }

    private void isOutOfScreen()
    {
        isOutOfScreen = sprite.getX() > Constants.V_WIDTH || sprite.getX() + sprite.getWidth() < 0 || sprite.getY() > Constants.V_HEIGHT || sprite.getY() + sprite.getHeight() < 0;
    }

    private void updateBounds()
    {
        bounds.setPosition(sprite.getX() + 5, sprite.getY() + 5);
    }

    public void render(SpriteBatch batch)
    {
        sprite.draw(batch);
    }

    public void setPosition(float x, float y)
    {
        sprite.setPosition(x, y);
    }

    public void setVelocity(float x, float y)
    {
        velocity.set(x, y);
    }

    public Polygon getBounds()
    {
        return bounds;
    }

    public Sprite getSprite()
    {
        return sprite;
    }

    public static Weapon getWeapon()
    {
        return weapon;
    }
}
