package com.sebluy.mygame;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Person {

	private static int currentId = 1;

	double x;
	double y;
	int id;
	ShapeRenderer shapeRenderer;
	MyGame game;

	public Person(MyGame game, double x, double y) {
		this.x = x;
		this.y = y;
		this.game = game;
		this.shapeRenderer = game.shapeRenderer;
		id = currentId;
		currentId += 1;
		game.people.put(id, this);
	}

	public String toString() {
		return String.format("Person %d %f %f", id, x, y);
	}

	public void shoot(Person other) {
		new Bullet(game, x, y, other.x, other.y);
	}

	public void render() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(0, 1, 0, 1);
		shapeRenderer.circle((int)x, (int)y, 25);
		shapeRenderer.end();
	}
	
}
