package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

public class Bullet {

	final static float SIZE = 4f;
	final static float SPEED = 1000f; // Pixels / second
	static int currentId = 1;

	ShapeRenderer shapeRenderer;
	MyGame game;
	int id;
	float x;
	float y;
	float xVel;
	float yVel;

	public Bullet(MyGame game, float x, float y, float xDest, float yDest) {
		this.x = x;
		this.y = y;
		this.game = game;
		float xDel = xDest - x;
		float yDel = yDest - y;
		id = currentId;
		currentId += 1;
		xVel = (float)(SPEED / Math.sqrt(1 + (yDel * yDel) / (xDel * xDel)));
		yVel = (float)(SPEED / Math.sqrt(1 + (xDel * xDel) / (yDel * yDel)));
		if (xDel < 0 ) xVel = -xVel;
		if (yDel < 0 ) yVel = -yVel;
		this.shapeRenderer = game.shapeRenderer;
		game.bullets.put(id, this);
	}

	public void render() {
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.circle((int)x, (int)y, SIZE);
		float delta = Gdx.graphics.getDeltaTime();
		x += xVel * delta;
		y += yVel * delta;
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
		return new Rectangle(x - SIZE, y - SIZE, SIZE * 2f, SIZE * 2f);
	}

}
