package com.nahroto.fruitdestroyer.huds;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nahroto.fruitdestroyer.Constants;
import com.nahroto.fruitdestroyer.screens.GameScreen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class BuyHud extends Hud
{
    private final float EASE_TIME = 0.7f;

    private boolean isShowed;
    private boolean isEasing;

    private Image overlay;
    private Image blackShader;

    private ImageButton extraAmmoButton;

    private Runnable toggleBuying;
    private Runnable toggleEasing;
    private Runnable resetPosition;
    private Runnable hideBlackShader;

    public BuyHud(Viewport viewport, SpriteBatch batch, TextureAtlas gameScreenAtlas, Texture blackShaderTexture)
    {
        super(viewport, batch);

        toggleBuying = new Runnable()
        {
            @Override
            public void run()
            {
                GameScreen.buying = !GameScreen.buying;
            }
        };

        toggleEasing = new Runnable()
        {
            @Override
            public void run()
            {
                isEasing = !isEasing;
            }
        };

        resetPosition = new Runnable()
        {
            @Override
            public void run()
            {
                resetPosition();
            }
        };

        hideBlackShader = new Runnable()
        {
            @Override
            public void run()
            {
                hideBlackShader();
            }
        };

        blackShader = new Image(blackShaderTexture);
        blackShader.addAction(alpha(0f));

        overlay = new Image(gameScreenAtlas.findRegion("overlay"));

        extraAmmoButton = new ImageButton(new TextureRegionDrawable(gameScreenAtlas.findRegion("moreAmmoUpgrade")), new TextureRegionDrawable(gameScreenAtlas.findRegion("moreAmmoUpgrade-down")));
        extraAmmoButton.setPosition(overlay.getX(Align.center), overlay.getY(Align.center), Align.bottom);

        resetPosition();

        actors.add(blackShader);
        actors.add(overlay);
        actors.add(extraAmmoButton);

        addAllActorsToStage();
    }

    @Override
    public void update(float delta)
    {
        super.update(delta);
        updateComponents();
    }

    private void updateComponents()
    {
        extraAmmoButton.setPosition(overlay.getX(Align.center), overlay.getY(Align.center), Align.bottom);
    }

    public void toggle()
    {
        if (isShowed == false)
        {
            easeIn();
            showBlackShader();
            isShowed = true;
        }

        else
        {
            easeOut();
            isShowed = false;
        }
    }

    private void easeIn()
    {
        isEasing = true;
        overlay.addAction(sequence(
                moveToAligned(Constants.V_WIDTH / 2, Constants.V_HEIGHT / 2, Align.center, EASE_TIME, Interpolation.pow2Out),
                run(toggleEasing)));
        GameScreen.buying = true;
    }

    private void easeOut()
    {
        isEasing = true;
        overlay.addAction(sequence(
                parallel(moveToAligned(0, Constants.V_HEIGHT / 2, Align.right, EASE_TIME, Interpolation.pow2Out), run(hideBlackShader)),
                run(toggleEasing),
                run(toggleBuying),
                run(resetPosition)));
    }

    private void resetPosition()
    {
        overlay.setPosition(Constants.V_WIDTH, Constants.V_HEIGHT / 2, Align.left);
    }

    private void showBlackShader()
    {
        blackShader.addAction(alpha(1f, EASE_TIME));
    }

    private void hideBlackShader()
    {
        blackShader.addAction(
                alpha(0f, EASE_TIME));
    }
}
