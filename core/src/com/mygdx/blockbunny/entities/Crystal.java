package com.mygdx.blockbunny.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.blockbunny.Game;

/**
 * Created by mkemp on 11/20/17.
 */

public class Crystal extends B2DSprites {

    public Crystal(Body body) {
        super(body);

        Texture texture = Game.resources.getTexture("crystal");
        TextureRegion[] sprites = TextureRegion.split(texture, 16, 16)[0];

        setAnimation(sprites, 1 / 12f);
    }
}
