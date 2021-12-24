package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

public class Bullet {

	final static float SIZE = 4f;
	final static float SPEED = 1000f; // Pixels / second
	static int currentId = 1;

	ShapeRenderer shapeRenderer;
	MyGame game;
	int id;
	Vector2 pos;
	Vector2 vel;

	public Bullet(MyGame game, Vector2 src, Vector2 dest) {
		id = currentId;
		currentId += 1;
		this.pos = src.cpy();
		this.game = game;
		this.vel = dest.cpy().sub(src).nor().scl(SPEED);
		this.shapeRenderer = game.shapeRenderer;
		game.bullets.put(id, this);
	}

	public void render() {
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.circle((int)pos.x, (int)pos.y, SIZE);
		float delta = Gdx.graphics.getDeltaTime();
		pos.add(vel.cpy().scl(delta));
	}

	public void update(Iterator<Bullet> it) {
		for (Wall wall : game.gameMap.walls) {
			Rectangle boundary1 = this.getBoundingRectangle();
			Rectangle boundary2 = wall.getBoundingRectangle();
			if (boundary1.overlaps(boundary2)) {
				it.remove();
				return;
			}
		}
	}

	private Rectangle getBoundingRectangle() {
		return new Rectangle(pos.x - SIZE, pos.y - SIZE, SIZE * 2f, SIZE * 2f);
	}

}
