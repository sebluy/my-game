package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;

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
	int team;
	boolean pickedUp;
	MyGame game;
	Map<Integer, Bullet> bullets;
	float directionTimeout = 0;
	float bulletTimeout = 0;
	private float xVel = 0;
	private float yVel = 0;
	private float direction = 0;

	public Person(MyGame game, int team, float x, float y) {
		this.x = x;
		this.y = y;
		this.game = game;
		this.team = team;
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
		bulletTimeout = 1;
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
		sprite.setCenter(x, y);
	}

	private void updateDirection(float newDirection) {
		direction = newDirection % 360;
		double directionR = direction / 180 * Math.PI;
		xVel = (float) Math.cos(directionR) * SPEED;
		yVel = (float) Math.sin(directionR) * SPEED;
	}

	public void update() {
		if (pickedUp) {
			Vector3 cs = game.unproject(Gdx.input.getX(), Gdx.input.getY());
			x = cs.x;
			y = cs.y;
		} else {
			if (directionTimeout < 0) {
				updateDirection(direction + (float)(Math.random() * 60 - 30));
				directionTimeout = 2;
			}
			x += xVel * Gdx.graphics.getDeltaTime();
			y += yVel * Gdx.graphics.getDeltaTime();
		}
		directionTimeout -= Gdx.graphics.getDeltaTime();
		bulletTimeout -= Gdx.graphics.getDeltaTime();
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
		sprite.setRotation(direction);
		sprite.setCenter(x, y);
		sprite.draw(game.batch);
	}

	public void renderShapes() {
		Rectangle rect = sprite.getBoundingRectangle();
		game.shapeRenderer.setColor(1, 0, 0, 1);
		game.shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		game.shapeRenderer.setColor(0, 1, 0, 1);
		game.shapeRenderer.circle(x, y, 20);
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

	public void tryToShoot(Person other) {
		if (other.team == team) return;
		Vector2 thisV = new Vector2(x, y);
		Vector2 otherV = new Vector2(other.x, other.y);

		// Check angles
		float angle = otherV.cpy().sub(thisV).angleDeg();
		if (Math.abs(angle - direction % 360) > 60f) {
			return;
		}

		// Check walls
		for (Wall wall : game.gameMap.walls) {
			boolean intersects = Intersector.intersectSegmentRectangle(
					thisV, otherV, wall.getBoundingRectangle()
			);
			if (intersects) {
				return;
			}
		}

		updateDirection(angle);
		if (bulletTimeout > 0) return;
		shoot(other);
	}
}
