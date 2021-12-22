package com.sebluy.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

public class MyGame extends ApplicationAdapter {

	static final float CAMERA_SPEED = 200.0f;

	ShapeRenderer shapeRenderer;
	SpriteBatch batch;
	Map<Integer, Person> people;
	Map<Integer, Person> team1;
	Map<Integer, Person> team2;
	Map<Integer, Bullet> bullets;
	Person pickedUp;
	OrthographicCamera camera;
	float time = 0;
	float lastShot = -1;
	GameMap gameMap;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

		people = new HashMap<>();
		team1 = new HashMap<>();
		team2 = new HashMap<>();
		bullets = new HashMap<>();
		gameMap = GameMap.defaultMap(this);

		camera = new OrthographicCamera(1000, 1000);
		camera.position.set(500, 500, 0);
		camera.update();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean touchDown(int x, int y, int pointer, int button) {
				Vector3 cs = unproject(x, y);
				for (Person p : people.values()) {
					if (p.getBoundingRectangle().contains(cs.x, cs.y)) {
						pickedUp = p;
						p.pickUp();
					}
				}
				return true;
			}

			@Override
			public boolean touchUp(int x, int y, int pointer, int button) {
				if (pickedUp != null) pickedUp.setDown();
				return true;
			}
		});

		for (int i = 0; i < 20; i++) {
			Person p1 = new Person(this, (float) (Math.random()*1000), (float) (Math.random()*1000));
		}
//		for (float x = 100 ; x < 1000; x += 100) {
//			Person p1 = new Person(this, x, 100);
//			Person p2 = new Person(this, x, 900);
//			team1.put(p1.id, p1);
//			team2.put(p2.id, p2);
//		}
	}

	public Vector3 unproject(float x, float y) {
		return camera.unproject(new Vector3(x, y, 0));
	}

	private Person randomTeamMember(Map<Integer, Person> team) {
		return (Person)team.values().toArray()[(int)(Math.random() * team.size())];
	}

	private void shootIfNecessary() {
		if (team1.size() == 0 || team2.size() == 0) return;
		time += Gdx.graphics.getDeltaTime();
		if (lastShot < 0 || time - lastShot > 1.0) {
			if (Math.random() > 0.5) {
				randomTeamMember(team1).shoot(randomTeamMember(team2));
			} else {
				randomTeamMember(team2).shoot(randomTeamMember(team1));
			}
			lastShot = time;
		}
	}

	private void updateIfShot() {
		ArrayList<Bullet> removeB = new ArrayList<>();
		ArrayList<Person> removeP = new ArrayList<>();
		for (Bullet b : bullets.values()) {
			for (Person p : people.values()) {
				if (!p.shot(b) && p.getBoundingRectangle().contains(b.x, b.y)) {
					removeB.add(b);
					removeP.add(p);
					break;
				}
			}
		}
		for (Bullet b : removeB) {
			bullets.remove(b.id);
		}
		for (Person p : removeP) {
			people.remove(p.id);
			team1.remove(p.id);
			team2.remove(p.id);
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
//		shootIfNecessary();
		updateIfShot();

		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		shapeRenderer.setProjectionMatrix(camera.combined);
		batch.setProjectionMatrix(camera.combined);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		gameMap.render();
		for (Bullet b : bullets.values()) {
			b.render();
		}
		for (Person p : people.values()) {
			p.renderShapes();
			p.update();
		}
		shapeRenderer.end();
		batch.begin();
		for (Person p : people.values()) {
			p.renderSprites();
		}
		batch.end();
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
		batch.dispose();
		Person.texture.dispose();
	}
}
