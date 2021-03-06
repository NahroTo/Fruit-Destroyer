package com.nahroto.fruitdestroyer.entities.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.nahroto.fruitdestroyer.Application;
import com.nahroto.fruitdestroyer.Constants;
import com.nahroto.fruitdestroyer.HealthBar;

public class Enemy
{
    protected static final int BASEVELOCITY = 50;
    protected float velocityMultiplier;
    protected float knockbackMultiplier;
    protected boolean explodable;

    public boolean renderHit = false;

    public boolean isUsed;

    protected long currentTime;
    protected long lastTimeHit;

    protected int maxHealth;
    protected int health;

    protected HealthBar healthBar;

    public int BOUNDING_X;
    public int BOUNDING_Y;
    public int BOUNDING_WIDTH;
    public int BOUNDING_HEIGHT;

    protected float angle;

    protected float directionX;
    protected float directionY;

    protected Vector2 velocity;
    protected Vector2 position;

    protected Vector2 forwardVelocity;

    protected Sprite sprite;

    protected TextureAtlas.AtlasRegion normalTexture;
    protected TextureAtlas.AtlasRegion hitTexture;

    protected Polygon bounds;

    protected Sound squishSFX;
    protected float[] vertices;

    public Enemy(final Application APP, TextureAtlas.AtlasRegion normalTexture, TextureAtlas.AtlasRegion hitTexture, Sprite redBar, Sprite greenBar, int BOUNDING_X, int BOUNDING_Y, int BOUNDING_WIDTH, int BOUNDING_HEIGHT)
    {
        this.normalTexture = normalTexture;
        this.hitTexture = hitTexture;

        this.sprite = new Sprite(normalTexture);

        squishSFX = APP.assets.get("sounds/squish.wav", Sound.class);

        velocity = new Vector2();
        position = new Vector2();
        forwardVelocity = new Vector2();
        bounds = new Polygon();

        healthBar = new HealthBar(redBar, greenBar);

        this.BOUNDING_X = BOUNDING_X;
        this.BOUNDING_Y = BOUNDING_Y;
        this.BOUNDING_WIDTH = BOUNDING_WIDTH;
        this.BOUNDING_HEIGHT = BOUNDING_HEIGHT;

        vertices = new float[]{
                0, 0,
                0, BOUNDING_HEIGHT,
                BOUNDING_WIDTH, BOUNDING_HEIGHT,
                BOUNDING_WIDTH, 0};
        bounds.setVertices(vertices);
        bounds.setPosition(sprite.getX(), sprite.getY());
        position.set(this.sprite.getX(), this.sprite.getY());

        bounds.setOrigin(BOUNDING_WIDTH / 2, BOUNDING_HEIGHT / 2);

    }

    public void calculateVelocity()
    {
        float deltaX = Constants.V_WIDTH / 2 - bounds.getX() - (BOUNDING_WIDTH / 2);
        float deltaY = Constants.V_HEIGHT / 2 - bounds.getY() - (BOUNDING_HEIGHT / 2);

        float length = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        directionX = deltaX / length;
        directionY = deltaY / length;

        forwardVelocity.set(BASEVELOCITY  * directionX, BASEVELOCITY * directionY);
        forwardVelocity.scl(velocityMultiplier);
        velocity.set(forwardVelocity);
    }

    public void calculateRotation()
    {
        float deltaX = Constants.V_WIDTH / 2 - bounds.getX() - (BOUNDING_WIDTH / 2);
        float deltaY = Constants.V_HEIGHT / 2 - bounds.getY() - (BOUNDING_HEIGHT / 2);

        angle = (MathUtils.atan2(-deltaX, deltaY) * MathUtils.radiansToDegrees) - 180;

        sprite.setRotation(angle);
        bounds.setRotation(angle);
    }

    public void knockback(float rawKnockbackPower)
    {
        lastTimeHit = System.currentTimeMillis();

        float actualKnockbackPower = rawKnockbackPower * knockbackMultiplier;
        velocity.set(-actualKnockbackPower * directionX, -actualKnockbackPower * directionY);
    }

    public void update(float delta)
    {
        if (System.currentTimeMillis() - lastTimeHit > 100)
            velocity.set(forwardVelocity);

        applyVelocityToPosition(delta);
        updateBounds();
        if (renderHit && (System.currentTimeMillis() - currentTime > 100))
        {
            renderHit = false;
            setNormalTexture();
        }
    }

    public void render(SpriteBatch batch)
    {
        sprite.draw(batch);
    }

    public void applyVelocityToPosition(float delta)
    {
        velocity.scl(delta);
        sprite.translate(velocity.x, velocity.y);
        velocity.scl(1 / delta);
    }

    protected void updateBounds()
    {
        bounds.setPosition(sprite.getX() + BOUNDING_X, sprite.getY() + BOUNDING_Y);
    }

    public void playSquishSound()
    {
        squishSFX.play();
    }


    public void setPosition(Vector2 position)
    {
        sprite.setPosition(position.x, position.y);
        bounds.setPosition(position.x + BOUNDING_X, position.y + BOUNDING_Y);
    }

    public float getX()
    {
        return sprite.getX();
    }

    public float getY()
    {
        return sprite.getY();
    }

    public void setNormalTexture()
    {
        sprite.setRegion(normalTexture);
    }

    public void setHitTexture()
    {
        sprite.setRegion(hitTexture);
        currentTime = System.currentTimeMillis();
    }

    public void resetVelocity()
    {
        velocity.set(0, 0);
    }

    public float getAngle()
    {
        return angle;
    }

    public Sprite getSprite()
    {
        return sprite;
    }

    public Polygon getBounds()
    {
        return bounds;
    }

    public HealthBar getHealthBar()
    {
        return healthBar;
    }

    public int getHealth()
    {
        return health;
    }

    public int getMaxHealth()
    {
        return maxHealth;
    }

    public void restoreHealth()
    {
        health = maxHealth;
    }

    public void reduceHealth(int damage)
    {
        health -= damage;
    }

    public boolean isExplodable()
    {
        return explodable;
    }
}
