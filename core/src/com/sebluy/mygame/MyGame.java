package com.sebluy.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
	Music from: https://musiclab.chromeexperiments.com/Song-Maker/song/5100954262700032
	Sprites from: https://opengameart.org/content/animated-top-down-survivor-player

	TODO: Allow user to create paths for people. A list of positions and directions.
	TODO: Have shot lead target.
	TODO: Make a smaller person collision box for bullet collisions.
 */

public class MyGame extends ApplicationAdapter {

	static final float CAMERA_SPEED = 200.0f;

	ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	Map<Integer, Person> people;
	Map<Integer, Bullet> bullets;
	OrthographicCamera camera;
	GameMap gameMap;
	PersonPath path;
	BitmapFont font;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();
		font = new BitmapFont();
		font.getData().setScale(2);

		people = new HashMap<>();
		bullets = new HashMap<>();

		gameMap = GameMap.defaultMap(this);
//		gameMap = GameMap.testMap(this, 500);

		Music music = Gdx.audio.newMusic(Gdx.files.internal("song.wav"));
		music.setLooping(true);
		music.play();

		camera = new OrthographicCamera(1000, 1000);
		camera.position.set(500, 500, 0);
		camera.update();

		path = new PersonPath(this);

		setInputProcessor();

//		Person p1 = new Person(this, 1, new Vector2(100, 300));
	}

	private float randomPos() {
		return (float) (Math.random() * 950 + 25);
	}

	private void setInputProcessor() {
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				path.input(unproject(x, y));
				return true;
			}
		});
	}

	public Vector3 unproject(float x, float y) {
		return camera.unproject(new Vector3(x, y, 0));
	}

	private Person randomTeamMember(Map<Integer, Person> team) {
		return (Person)team.values().toArray()[(int)(Math.random() * team.size())];
	}

	private void updateIfShot() {
		Iterator<Bullet> bi = bullets.values().iterator();
		while (bi.hasNext()) {
			Bullet b = bi.next();
			Iterator<Person> pi = people.values().iterator();
			while (pi.hasNext()) {
				Person p = pi.next();
				if (!p.shot(b) && p.getBoundingRectangle().contains(b.pos)) {
					pi.remove();
					bi.remove();
					break;
				}
			}
		}
	}

	private void handleCameraMovement() {
		float distance = CAMERA_SPEED * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			camera.translate(0, distance);
		} else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			camera.translate(0, -distance);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.translate(-distance, 0);
		} else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.translate(distance, 0);
		}
	}

	@Override
	public void render() {
		handleCameraMovement();
		updateIfShot();

		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		gameMap.render();
		Iterator<Bullet> it = bullets.values().iterator();
//		path.renderShapes();
		while (it.hasNext()) {
			Bullet b = it.next();
			b.update(it);
			b.render();
		}
		for (Person p : people.values()) {
			p.renderShapes();
		}
		shapeRenderer.end();

		batch.begin();
		path.renderSprites();
		for (Person p : people.values()) {
			p.update();
			p.renderSprites();
		}
		batch.end();
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
		batch.dispose();
		if (Person.texture != null) {
			Person.texture.dispose();
		}
	}
}
