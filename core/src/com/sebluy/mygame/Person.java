package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public class Person {

	private static int currentId = 1;
	private final static float SPEED = 1000f;
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
		if (texture == null) {
			texture = new Texture(
					Gdx.files.internal("Top_Down_Survivor/rifle/idle/survivor-idle_rifle_0.png")
			);
		}
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
		System.out.println("Turning towards " + direction);
	}

	public void update() {
		if (pickedUp) {
			Vector3 cs = game.unproject(Gdx.input.getX(), Gdx.input.getY());
			x = cs.x;
			y = cs.y;
		} else {
			if (time < 0) {
				updateDirection(direction + (float)(Math.random() * 60 - 30));
				time = 2;
			}
			x += xVel * Gdx.graphics.getDeltaTime();
			y += yVel * Gdx.graphics.getDeltaTime();
			time -= Gdx.graphics.getDeltaTime();
		}
		preventOverlap();
	}

	private void preventOverlap() {
		for (Wall wall : game.gameMap.walls) {
			preventOverlap(wall);
		}
	}

	private void preventOverlap(Wall wall) {
		Rectangle boundary1 = this.getBoundingRectangle();
		Rectangle boundary2 = wall.getBoundingRectangle();

		if (!boundary1.overlaps(boundary2)) return;

		MinimumTranslationVector mtv = new MinimumTranslationVector();
		Intersector.overlapConvexPolygons(
			rectangleToPolygon(boundary1), rectangleToPolygon(boundary2), mtv
		);

		float delX = mtv.normal.x * mtv.depth;
		float delY = mtv.normal.y * mtv.depth;
		System.out.printf("Velocity: %f %f\n", xVel, yVel);
		System.out.printf("Adjusting position: %f %f\n", delX, delY);
		x += delX;
		y += delY;
		float newDirection;
		if (delX == 0) {
			if (xVel > 0) newDirection = 0;
			else if (xVel < 0) newDirection = 180;
			else newDirection = direction - 90;
		} else {
			if (yVel > 0) newDirection = 90;
			else if (yVel < 0) newDirection = 270;
			else newDirection = direction - 90;
		}
		updateDirection(newDirection);
	}

	private Polygon rectangleToPolygon(Rectangle r) {
		return new Polygon(new float[]{
				r.x,
				r.y,
				r.x + r.width,
				r.y,
				r.x + r.width,
				r.y + r.height,
				r.x,
				r.y + r.height
		});
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
