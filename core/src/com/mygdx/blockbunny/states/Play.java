package com.mygdx.blockbunny.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.blockbunny.Game;
import com.mygdx.blockbunny.entities.Crystal;
import com.mygdx.blockbunny.entities.HUD;
import com.mygdx.blockbunny.entities.Player;
import com.mygdx.blockbunny.handlers.B2DVars;
import com.mygdx.blockbunny.handlers.GameStateManager;
import com.mygdx.blockbunny.handlers.MyContactListener;
import com.mygdx.blockbunny.handlers.MyInput;

import static com.mygdx.blockbunny.handlers.B2DVars.PPM;

/**
 * Created by mkemp on 11/8/17.
 */

public class Play extends GameState {

    private boolean debug = false;

    // The World
    private World world;
    private MyContactListener contactListener;

    // Box 2D
    private Box2DDebugRenderer b2dr;
    private OrthographicCamera b2dCam;

    // My Player
    private Player player;

    private Array<Crystal> crystals;

    // Map
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private int tileSize;

    private HUD hud;

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

        // Create Crystals
        createCrystals();

        // Set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

        // Setup hud
        hud = new HUD(player);
    }

    @Override
    public void handleInput() {

        // Player Jump
        if (MyInput.isPressed(MyInput.BUTTON1) || Gdx.input.justTouched()) {
            if (contactListener.isPlayerOnGround()) {
                player.getBody().applyForceToCenter(0, 300, true);
            }
        }

        // Switch block color
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            switchBlocks();
        }
    }

    @Override
    public void update(float dt) {

        handleInput();

        // Update the world
        world.step(dt, 6, 2);

        // Remove crystals from the box 2d world, now that the world has finished updating.
        Array<Body> bodies = contactListener.getBodiesToRemove();
        for (int i = 0; i < bodies.size; i++) {
            Body b = bodies.get(i);
            crystals.removeValue((Crystal) b.getUserData(), true);
            world.destroyBody(b);
            player.collectCrystal();
        }
        bodies.clear();

        // Update player
        player.update(dt);

        // Update crystals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).update(dt);
        }
    }

    @Override
    public void render() {

        // Clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set camera to follow player
        cam.position.set(
                player.getPosition().x * PPM + Game.V_WIDTH / 4,
                Game.V_HEIGHT / 2,
                0
        );
        cam.update();

        // Draw tiled map
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        // Draw player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        // Draw crystals
        for (int i = 0; i < crystals.size; i++) {
            crystals.get(i).render(sb);
        }

        // Draw HUD
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);

        if (debug) {
            // Debug renderer
            b2dr.render(world, b2dCam.combined);
        }
    }

    @Override
    public void dispose() {

    }

    private void createPlayer() {
        // Create Player
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(100 / PPM, 200 / PPM);
        bodyDef.linearVelocity.set(1f, 0);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13 / PPM, 13 / PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        body.createFixture(fixtureDef).setUserData("player");

        // Create Foot Sensor
        shape.setAsBox(13 / PPM, 2 / PPM, new Vector2(0, -13 / PPM), 0);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData("foot");

        // Create Player
        player = new Player(body);

        // Circular reference
        body.setUserData(player);
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
                fixtureDef.filter.categoryBits = bits;
                fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
                fixtureDef.isSensor = false;

                world.createBody(bodyDef).createFixture(fixtureDef);
            }
        }
    }

    private void createCrystals() {
        System.out.println("createCrystals");
        crystals = new Array<Crystal>();

        MapLayer layer = tiledMap.getLayers().get("crystals");

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        for (MapObject mo : layer.getObjects()) {

            bodyDef.type = BodyDef.BodyType.StaticBody;

            float x = mo.getProperties().get("x", Float.class) / PPM;
            float y = mo.getProperties().get("y", Float.class) / PPM;

            System.out.println("crystal x " + x);
            System.out.println("crystal y " + y);

            bodyDef.position.set(x, y);

            CircleShape shape = new CircleShape();
            shape.setRadius(8f / PPM);

            fixtureDef.shape = shape;
            fixtureDef.isSensor = true;
            fixtureDef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef).setUserData("crystal");

            Crystal c = new Crystal(body);
            crystals.add(c);

            body.setUserData(c);
        }
    }

    private void switchBlocks() {
        Filter filter = player.getBody().getFixtureList().first().getFilterData();
        short bits = filter.maskBits;

        // Switch to next color (red > green > blue > red)
        if ((bits & B2DVars.BIT_RED) != 0) {
            bits &= ~B2DVars.BIT_RED;
            bits |= B2DVars.BIT_GREEN;
        }
        else if ((bits & B2DVars.BIT_GREEN) != 0) {
            bits &= ~B2DVars.BIT_GREEN;
            bits |= B2DVars.BIT_BLUE;
        }
        else if ((bits & B2DVars.BIT_BLUE) != 0) {
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_RED;
        }

        // Set new mask bits
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        // Set new mask bits for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~B2DVars.BIT_CRYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);
    }
}
