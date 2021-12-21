package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class Person {

	private static int currentId = 1;
	private final static float SPEED = 100f;
	private final static float RADIUS = 40f;
	static Texture texture;
	Sprite sprite;

	float x;
	float y;
	int id;
	boolean pickedUp;
	ShapeRenderer shapeRenderer;
	MyGame game;
	Map<Integer, Bullet> bullets;
	float time = 0;
	private float xVel = 0;
	private float yVel = 0;
	private float direction = 0;

	public Person(MyGame game, float x, float y) {
		this.x = x;
		this.y = y;
		this.game = game;
		this.shapeRenderer = game.shapeRenderer;
		bullets = new HashMap<>();
		id = currentId;
		currentId += 1;
		game.people.put(id, this);
		loadSprite();
	}

	public String toString() {
		return String.format("Person %d %f %f", id, x, y);
	}

	public void shoot(Person other) {
		Bullet b = new Bullet(game, x, y, other.x, other.y);
		bullets.put(b.id, b);
	}

	private void loadSprite() {
		if (texture != null) return;
		texture = new Texture(
			Gdx.files.internal("Top_Down_Survivor/rifle/idle/survivor-idle_rifle_0.png")
		);
		sprite = new Sprite(texture);
		sprite.setSize(RADIUS * 2, RADIUS * 2);
		sprite.setOriginCenter();
		sprite.setPosition(x, y);
	}

	private void updateDirection(float newDirection) {
		direction = newDirection % 360;
		double directionR = direction / 180 * Math.PI;
		xVel = (float) Math.cos(directionR) * SPEED;
		yVel = (float) Math.sin(directionR) * SPEED;
		time = 2;
		System.out.println("Turning towards " + direction);
	}

	public void update() {
		if (pickedUp) {
			Vector3 cs = game.unproject(Gdx.input.getX(), Gdx.input.getY());
			x = cs.x;
			y = cs.y;
		} else {
			Wall wall = getTouchingWall();
			if (time < 1.5 && wall != null) {
				System.out.println("Hit wall");
				if (wall.isVertical()) {
					updateDirection(180 - direction);
				} else {
					updateDirection(-direction);
				}
			} else if (time < 0) {
				updateDirection((float)(Math.random() * 360));
			}
			x += xVel * Gdx.graphics.getDeltaTime();
			y += yVel * Gdx.graphics.getDeltaTime();
			time -= Gdx.graphics.getDeltaTime();
		}
	}

	private Wall getTouchingWall() {
		for (Wall wall : game.gameMap.walls) {
			if (getBoundingRectangle().overlaps(wall.getBoundingRectangle())) {
				return wall;
			}
		}
		return null;
	}

	public Rectangle getBoundingRectangle() {
		return sprite.getBoundingRectangle();
	}

	public void renderSprites() {
		sprite.setPosition(x, y);
		sprite.setRotation(direction);
		sprite.draw(game.batch);
	}

	public void renderShapes() {
//		shapeRenderer.setColor(0, 1, 0, 1);
//		shapeRenderer.circle(x, y, RADIUS);
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
