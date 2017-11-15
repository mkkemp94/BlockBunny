package com.mygdx.blockbunny.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.blockbunny.Game;
import com.mygdx.blockbunny.handlers.B2DVars;
import com.mygdx.blockbunny.handlers.GameStateManager;
import com.mygdx.blockbunny.handlers.MyContactListener;
import com.mygdx.blockbunny.handlers.MyInput;

import static com.mygdx.blockbunny.handlers.B2DVars.PPM;

/**
 * Created by mkemp on 11/8/17.
 */

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private Body playerBody;

    private MyContactListener cl;

    public Play(GameStateManager gsm) {

        super(gsm);

        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
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
        fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
        body.createFixture(fixtureDef).setUserData("ground");

        // Create player
        bodyDef.position.set(160 / PPM, 200 / PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bodyDef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        playerBody.createFixture(fixtureDef).setUserData("player");

        // create foot sensor
        shape.setAsBox(2 / PPM, 2 / PPM, new Vector2(0, -5 / PPM), 0);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        fixtureDef.isSensor = true;
        playerBody.createFixture(fixtureDef).setUserData("foot");

        // Set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
    }

    @Override
    public void handleInput() {

        // player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (cl.isPlayerOnGround()) {
                playerBody.applyForceToCenter(0, 200, true);
            }
        }
    }

    @Override
    public void update(float dt) {

        handleInput();

        world.step(dt, 6, 2);
    }

    @Override
    public void render() {

        // Clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }
}
