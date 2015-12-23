package com.nahroto.fruitdestroyer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.nahroto.fruitdestroyer.entities.enemies.Ananas;
import com.nahroto.fruitdestroyer.entities.enemies.Enemy;
import com.nahroto.fruitdestroyer.entities.enemies.Orange;

public class WaveGenerator
{
    private static final int ANANAS_MINIMUM_WAVE = 3;

    private static final float ORANGE_MULTIPLIER = 1f;
    private static final float ANANAS_MULTIPLIER = 0.5f;

    public static Integer wave = new Integer(1);

    private static Array<Enemy> queue = new Array<Enemy>();

    private static float delay = 0;
    private static long startTime;

    public static void startNewWave()
    {
        Logger.log("NEW WAVE STARTED");

        startTime = System.currentTimeMillis();

        restoreAllEnemies();

        delayTime(0f);
            addOranges(5);
            if (wave >= ANANAS_MINIMUM_WAVE)
                addAnanases(5);
    }

    public static void update()
    {
        if (queue.size > 0 && System.currentTimeMillis() - startTime >= delay * 1000)
            sendQueueImmediatly();
    }

    private static void delayTime(float delayTime)
    {
        delay = delayTime;
    }


    private static void restoreAllEnemies()
    {
        for (Enemy enemy : Enemy.totalEnemies)
            enemy.restoreHealth();
    }

    private static void makeEnemiesReady()
    {
        for (Enemy enemy : queue)
        {
            enemy.isCollidable = true;
            enemy.isUsed = true;
            enemy.isDying = false;

            enemy.setPosition(RandomPositioner.getRandomPosition());
            enemy.calculateRotation();
            enemy.calculateVelocity();
        }
    }

    private static void addOranges(int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            checkIfUsedLoop:
            for (Orange orange : Orange.totalOranges)
            {
                if (!orange.isUsed)
                {
                    queue.add(orange);
                    orange.isUsed = true;
                    break checkIfUsedLoop;
                }
            }
        }

        if (delay == 0)
            sendQueueImmediatly();
    }

    private static void addAnanases(int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            checkIfUsedLoop:
            for (Ananas ananas : Ananas.totalAnanases)
            {
                if (!ananas.isUsed)
                {
                    queue.add(ananas);
                    ananas.isUsed = true;
                    break checkIfUsedLoop;
                }
            }
        }

        if (delay == 0)
            sendQueueImmediatly();
    }

    private static void sendQueueImmediatly()
    {
        makeEnemiesReady();
        Enemy.currentEnemies.addAll(queue);
        Logger.log("queue released!, size: " + queue.size);
        Logger.log("current enemies size: " + Enemy.currentEnemies.size);
        queue.clear();
    }

    public static Array<Enemy> getQueue()
    {
        return queue;
    }
}