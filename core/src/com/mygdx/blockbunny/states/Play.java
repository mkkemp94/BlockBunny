package com.mygdx.blockbunny.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
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

    // The World
    private World world;
    private MyContactListener contactListener;

    // Box 2D
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera b2dCam;

    // My Player
    private Body playerBody;

    // Map
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private int tileSize;

    public Play(GameStateManager gsm) {
        super(gsm);

        // Box 2D
        world = new World(new Vector2(0, -9.81f), true);
        contactListener = new MyContactListener();
        world.setContactListener(contactListener);
        b2dr = new Box2DDebugRenderer();

        // Create Player
        createPlayer();

        // Create Tiles
        createTiles();



        // Set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);





    }

    @Override
    public void handleInput() {

        // Player Jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            if (contactListener.isPlayerOnGround()) {
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

        // Draw tiled map
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        // Debug renderer
        b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }

    private void createPlayer() {
        // Create Player
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(160 / PPM, 200 / PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5 / PPM, 5 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED;
        playerBody.createFixture(fixtureDef).setUserData("player");

        // Create Foot Sensor
        shape.setAsBox(2 / PPM, 2 / PPM, new Vector2(0, -5 / PPM), 0);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED;
        fixtureDef.isSensor = true;
        playerBody.createFixture(fixtureDef).setUserData("foot");
    }

    private void createTiles() {
        // Load Map
        tiledMap = new TmxMapLoader().load("res/maps/untitled.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tileSize = (int) tiledMap.getProperties().get("tilewidth", Integer.class);

        TiledMapTileLayer layer;

        layer = (TiledMapTileLayer) tiledMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);

        layer = (TiledMapTileLayer) tiledMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);

        layer = (TiledMapTileLayer) tiledMap.getLayers().get("blue");
        createLayer(layer, B2DVars.BIT_BLUE);
    }

    private void createLayer(TiledMapTileLayer layer, short bits) {

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        // Go through all cells in layer.
        for( int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {

                // Get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                // Check if cell exist
                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                // Create body and fixture from cell.
                bodyDef.type = BodyDef.BodyType.StaticBody;
                bodyDef.position.set(
                        (col + 0.5f) * tileSize / PPM,
                        (row + 0.5f) * tileSize / PPM
                );

                ChainShape chainShape = new ChainShape();
                Vector2[] v = new Vector2[3];

                // Bottom left corner
                v[0] = new Vector2(
                        -tileSize / 2 / PPM,
                        -tileSize / 2 / PPM
                );

                // Top left corner
                v[1] = new Vector2(
                        -tileSize / 2 / PPM,
                        tileSize / 2 / PPM
                );

                // Top right corner
                v[2] = new Vector2(
                        tileSize / 2 / PPM,
                        tileSize / 2 / PPM
                );

                chainShape.createChain(v);
                fixtureDef.friction = 0;
                fixtureDef.shape = chainShape;
                fixtureDef.filter.categoryBits = B2DVars.BIT_RED;
                fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
                fixtureDef.isSensor = false;

                world.createBody(bodyDef).createFixture(fixtureDef);
            }
        }
    }
}
