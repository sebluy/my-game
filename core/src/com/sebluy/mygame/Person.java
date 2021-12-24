package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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

	Vector2 pos;
	int id;
	int team;
	boolean pickedUp;
	MyGame game;
	Map<Integer, Bullet> bullets;
	float directionTimeout = 0;
	float bulletTimeout = 0;
	private final Vector2 vel;
	private float direction = 0;

	public Person(MyGame game, int team, Vector2 pos) {
		this.pos = pos;
		this.game = game;
		this.team = team;
		vel = new Vector2(0, SPEED);
		bullets = new HashMap<>();
		id = currentId;
		currentId += 1;
		game.people.put(id, this);
		loadSprite();
	}

	public String toString() {
		return String.format("Person %d %f %f", id, pos.x, pos.y);
	}

	public void shoot(Person other) {
		Bullet b = new Bullet(game, pos, other.pos);
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
		sprite.setCenter(pos.x, pos.y);
	}

	private void updateDirection(float newDirection) {
		direction = newDirection % 360;
		vel.setAngleDeg(direction);
	}

	public void update() {
		if (pickedUp) {
			Vector3 cs = game.unproject(Gdx.input.getX(), Gdx.input.getY());
			pos.x = cs.x;
			pos.y = cs.y;
		} else {
			if (directionTimeout <= 0) {
				updateDirection(direction + (float)(Math.random() - 0.5) * 8f);
				directionTimeout = 0.1f;
			}
			pos.add(vel.cpy().scl(Gdx.graphics.getDeltaTime()));
		}
		for (Person person2 : game.people.values()) {
			tryToShoot(person2);
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
		pos.x += delX;
		pos.y += delY;
		float newDirection;
		if (delX == 0) {
			if (vel.x > 0) newDirection = 0;
			else if (vel.x < 0) newDirection = 180;
			else newDirection = direction - 90;
		} else {
			if (vel.y > 0) newDirection = 90;
			else if (vel.y < 0) newDirection = 270;
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
		float size = RADIUS * 2f * 1.42f;
		return new Rectangle(pos.x - size / 2f, pos.y - size / 2f, size, size);
	}

	public void renderSprites() {
		sprite.setRotation(direction);
		sprite.setCenter(pos.x, pos.y);
		sprite.draw(game.batch);
	}

	public void renderShapes() {
		Rectangle rect = getBoundingRectangle();
		game.shapeRenderer.setColor(1, 0, 0, 1);
		game.shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		game.shapeRenderer.setColor(0, 1, 0, 1);
		game.shapeRenderer.circle(pos.x, pos.y, 20);
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

		// Check angles
		float angle = other.pos.cpy().sub(pos).angleDeg();
		if (Math.abs(angle - direction % 360) > 60f) {
			return;
		}

		// Check walls
		for (Wall wall : game.gameMap.walls) {
			boolean intersects = Intersector.intersectSegmentRectangle(
					pos, other.pos, wall.getBoundingRectangle()
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
