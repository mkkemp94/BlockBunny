package com.mygdx.blockbunny;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.blockbunny.handlers.Content;
import com.mygdx.blockbunny.handlers.GameStateManager;
import com.mygdx.blockbunny.handlers.MyInput;
import com.mygdx.blockbunny.handlers.MyInputProcessor;

/**
 * This is the Game class.
 */
public class Game extends ApplicationAdapter {

	public static final String TITLE = "Block Bunny";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;

	public static final float STEP = 1 / 60f;
	private float accum;

	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	public static Content resources;

	@Override
	public void create () {

		Gdx.input.setInputProcessor(new MyInputProcessor());

		resources = new Content();
		resources.loadTexture("res/images/bunny.png", "bunny");
		resources.loadTexture("res/images/crystal.png", "crystal");
		resources.loadTexture("res/images/hud.png", "hud");

		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		gsm = new GameStateManager(this);

	}

	@Override
	public void render () {

		accum += Gdx.graphics.getDeltaTime();
		while (accum >= STEP) {
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
		}
	}
	
	@Override
	public void dispose () {

	}

	public SpriteBatch getSpriteBatch() {
		return sb;
	}

	public OrthographicCamera getCamera() {
		return cam;
	}

	public OrthographicCamera getHudCamera() {
		return hudCam;
	}
}
