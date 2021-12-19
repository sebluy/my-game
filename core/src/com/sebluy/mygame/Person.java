package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class Person {

	private static int currentId = 1;
	private final static float RADIUS = 25f;

	double x;
	double y;
	int id;
	boolean pickedUp;
	ShapeRenderer shapeRenderer;
	MyGame game;
	Map<Integer, Bullet> bullets;

	public Person(MyGame game, double x, double y) {
		this.x = x;
		this.y = y;
		this.game = game;
		this.shapeRenderer = game.shapeRenderer;
		bullets = new HashMap<>();
		id = currentId;
		currentId += 1;
		game.people.put(id, this);
	}

	public String toString() {
		return String.format("Person %d %f %f", id, x, y);
	}

	public void shoot(Person other) {
		Bullet b = new Bullet(game, x, y, other.x, other.y);
		bullets.put(b.id, b);
	}

	public void render() {
		if (pickedUp) {
			Vector3 cs = game.unproject(Gdx.input.getX(), Gdx.input.getY());
			x = cs.x;
			y = cs.y;
		}
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.circle((float)x, (float)y, RADIUS);
	}

	public boolean contains(double x, double y) {
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
