package com.mygdx.blockbunny.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.blockbunny.handlers.GameStateManager;

/**
 * Created by mkemp on 11/8/17.
 */

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    public Play(GameStateManager gsm) {

        super(gsm);

        world = new World(new Vector2(0, -9.81f), true);
        b2dr = new Box2DDebugRenderer();

        // Create platform
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(160, 120);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50f, 5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        // Create falling box
        bodyDef.position.set(160, 200);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        shape.setAsBox(5, 5);
        fixtureDef.shape = shape;
        //fixtureDef.restitution = 1f;
        body.createFixture(fixtureDef);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

        // Clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(dt, 6, 2);
    }

    @Override
    public void render() {
        b2dr.render(world, cam.combined);
    }

    @Override
    public void dispose() {

    }
}
