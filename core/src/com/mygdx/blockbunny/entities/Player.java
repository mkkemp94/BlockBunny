package com.mygdx.blockbunny.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.blockbunny.Game;

/**
 * Created by mkemp on 11/17/17.
 */

public class Player extends B2DSprites {

    private int numCrystals;
    private int totalCrystals;

    public Player(Body body) {
        super(body);

        Texture texture = Game.resources.getTexture("bunny");
        TextureRegion[] sprites = TextureRegion.split(texture, 32, 32)[0];

        setAnimation(sprites, 1 / 12f);
    }

    public void collectCrystal() {
        numCrystals++;
    }

    public  int getNumCrystals() {
        return numCrystals;
    }

    public void setTotalCrystals(int i) {
        totalCrystals = i;
    }

    public int getTotalCrystals() {
        return totalCrystals;
    }
}
