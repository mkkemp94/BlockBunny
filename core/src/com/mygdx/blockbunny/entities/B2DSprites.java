package com.mygdx.blockbunny.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.blockbunny.handlers.Animation;
import com.mygdx.blockbunny.handlers.B2DVars;

/**
 * Created by mkemp on 11/17/17.
 */

public class B2DSprites {

    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;

    public B2DSprites(Body body) {
        this.body = body;
        animation = new Animation();
    }

    public void setAnimation(TextureRegion[] regions, float delay) {
        animation.setFrames(regions, delay);
        width = regions[0].getRegionWidth();
        height = regions[0].getRegionHeight();
    }

    public void update(float dt) {
        animation.update(dt);
    }

    public void render(SpriteBatch sb) {
        sb.begin();
        sb.draw(
                animation.getFrame(),
                body.getPosition().x * B2DVars.PPM - width / 2,
                body.getPosition().y * B2DVars.PPM - height / 2
        );
        sb.end();
    }

    public Body getBody() {
        return body;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
