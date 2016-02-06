package com.nahroto.fruitdestroyer.huds.powerupoverlays;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.nahroto.fruitdestroyer.Font;
import com.nahroto.fruitdestroyer.Input;
import com.nahroto.fruitdestroyer.huds.BuyHud;

public final class PowerupOverlay
{
    private boolean isOwned = false;

    private Integer cost;
    private Vector2 position;

    private Image background;
    private Image checkedBox;
    private Image uncheckedBox;
    private ImageButton exitButton;
    private ImageButton powerupButton;
    private Label title;
    private Label costLabel;
    private Label descriptionLabel;
    private Array<Actor> actors;

    private Font titleFont;
    private String titleText;

    public PowerupOverlay(String titleText, String description, final Integer cost, ImageButton powerupButton, TextureAtlas gameScreenAtlas, final BuyHud buyHud)
    {
        this.cost = cost;
        this.powerupButton = new ImageButton(powerupButton.getStyle());
        this.titleText = titleText;
        actors = new Array<Actor>();
        position = new Vector2();
        background = new Image(gameScreenAtlas.findRegion("powerupDescBG"));
        checkedBox = new Image(gameScreenAtlas.findRegion("checked"));
        uncheckedBox = new Image(gameScreenAtlas.findRegion("unchecked"));
        exitButton = new ImageButton(new TextureRegionDrawable(gameScreenAtlas.findRegion("exitButton")));
        exitButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                buyHud.emptyActorList();
                buyHud.removeAllActors();
                buyHud.getStage().clear();
                buyHud.addBuyOverlayActors();
            }
        });

        this.powerupButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                if (buyHud.getPoints() >= cost)
                    upgrade(buyHud);
            }
        });

        titleFont = new Font("fonts/trompus.otf", 55, Color.WHITE, Color.BLACK, 4, true);
        title = new Label(titleText, new Label.LabelStyle(titleFont.getFont(), Color.WHITE));
        descriptionLabel = new Label(description, new Label.LabelStyle(new Font("fonts/trompus.otf", 30, Color.WHITE, Color.BLACK, 2, true).getFont(), Color.WHITE));
        if (cost == 1)
            costLabel = new Label("Price: " + cost.toString() + " point", new Label.LabelStyle(new Font("fonts/trompus.otf", 40, Color.WHITE, Color.BLACK, 2, true).getFont(), Color.YELLOW));
        else
            costLabel = new Label("Price: " + cost.toString() + " points", new Label.LabelStyle(new Font("fonts/trompus.otf", 40, Color.WHITE, Color.BLACK, 2, true).getFont(), Color.YELLOW));
        actors.add(background);
        actors.add(title);
        actors.add(uncheckedBox);
        actors.add(costLabel);
        actors.add(descriptionLabel);
        actors.add(costLabel);
        actors.add(this.powerupButton);
        actors.add(exitButton);
    }

    public void upgrade(BuyHud buyHud)
    {
        if (!isOwned)
        {
            isOwned = true;
            actors.clear();
            actors.add(background);
            actors.add(title);
            actors.add(checkedBox);
            actors.add(costLabel);
            actors.add(descriptionLabel);
            actors.add(costLabel);
            actors.add(powerupButton);
            actors.add(exitButton);

            buyHud.addBuyOverlayActors();
            buyHud.addOverlayActors(titleText);

            buyHud.reducePoints(cost);
        }
    }

    public void reset()
    {
        isOwned = false;
        actors.clear();
        actors.add(background);
        actors.add(title);
        actors.add(uncheckedBox);
        actors.add(costLabel);
        actors.add(descriptionLabel);
        actors.add(costLabel);
        actors.add(powerupButton);
        actors.add(exitButton);
    }

    public void updatePriceLabelColor(int currentPoints)
    {
        if (currentPoints >= cost)
            costLabel.setColor(Color.GREEN);
        else
            costLabel.setColor(Color.RED);
    }

    public void setPosition(float x, float y, int align)
    {
        position.set(x, y);
        background.setPosition(position.x, position.y, align);
        title.setPosition(background.getX(Align.center) - (titleFont.getWidth(titleText) / 2), background.getY(Align.top) - 160);
        checkedBox.setPosition(background.getX() + 100, background.getY() + 360);
        uncheckedBox.setPosition(background.getX() + 100, background.getY() + 360);
        powerupButton.setPosition(background.getX(Align.center), background.getY() + 415, align);
        costLabel.setPosition(background.getX() + 50, background.getY() + 50);
        descriptionLabel.setPosition(background.getX() + 60, background.getY() + 305, Align.topLeft);
        exitButton.setPosition(background.getX() + background.getWidth() - 32, background.getY() + background.getHeight() - 32, Align.center);
    }

    public Array<Actor> getActors()
    {
        return actors;
    }
}
