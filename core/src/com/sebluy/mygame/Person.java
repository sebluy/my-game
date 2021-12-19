package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class Person {

	private static int currentId = 1;
	private final static float RADIUS = 40f;
	static Texture texture;

	float x;
	float y;
	int id;
	boolean pickedUp;
	ShapeRenderer shapeRenderer;
	MyGame game;
	Map<Integer, Bullet> bullets;

	public Person(MyGame game, float x, float y) {
		this.x = x;
		this.y = y;
		this.game = game;
		this.shapeRenderer = game.shapeRenderer;
		bullets = new HashMap<>();
		id = currentId;
		currentId += 1;
		game.people.put(id, this);
		loadTexture();
	}

	public String toString() {
		return String.format("Person %d %f %f", id, x, y);
	}

	public void shoot(Person other) {
		Bullet b = new Bullet(game, x, y, other.x, other.y);
		bullets.put(b.id, b);
	}

	private static void loadTexture() {
		if (texture != null) return;
		texture = new Texture(
			Gdx.files.internal("Top_Down_Survivor/rifle/idle/survivor-idle_rifle_0.png")
		);
	}

	public void update() {
		if (pickedUp) {
			Vector3 cs = game.unproject(Gdx.input.getX(), Gdx.input.getY());
			x = cs.x;
			y = cs.y;
		}
	}

	public void renderSprites() {
		float tx = x - RADIUS;
		float ty = y - RADIUS;
		game.batch.draw(texture, tx, ty, RADIUS * 2, RADIUS * 2);
	}

	public void renderShapes() {
//		shapeRenderer.setColor(0, 1, 0, 1);
//		shapeRenderer.circle(x, y, RADIUS);
	}

	public boolean contains(float x, float y) {
		return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2)) < RADIUS;
	}

	public void pickUp() {
		pickedUp = true;
	}

	public void setDown() {
		pickedUp = false;
	}

	public boolean shot(Bullet b) {
		return bullets.containsKey(b.id);
	}
}
