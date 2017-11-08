package com.mygdx.blockbunny.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.blockbunny.Game;
import com.mygdx.blockbunny.handlers.B2DVars;
import com.mygdx.blockbunny.handlers.GameStateManager;
import com.mygdx.blockbunny.handlers.MyContactListener;

import static com.mygdx.blockbunny.handlers.B2DVars.PPM;

/**
 * Created by mkemp on 11/8/17.
 */

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    public Play(GameStateManager gsm) {

        super(gsm);

        world = new World(new Vector2(0, -0.81f), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();

        // Create platform
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(160 / PPM, 120 / PPM);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 / PPM, 5 / PPM);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_GROUND;
        fixtureDef.filter.maskBits = B2DVars.BIT_BOX | B2DVars.BIT_BALL;
        body.createFixture(fixtureDef).setUserData("ground");

        // Create falling box
        bodyDef.position.set(160 / PPM, 200 / PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_BOX;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fixtureDef).setUserData("box");

        // Create ball
        bodyDef.position.set(168 / PPM, 220 / PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(5f / PPM);
        fixtureDef.shape = circleShape;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = B2DVars.BIT_BALL;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fixtureDef).setUserData("ball");

        // Set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
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
        b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }
}
