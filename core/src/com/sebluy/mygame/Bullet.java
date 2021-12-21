package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Bullet {

	final static float SIZE = 4f;
	final static float SPEED = 1000f; // Pixels / second
	static int currentId = 1;

	ShapeRenderer shapeRenderer;
	int id;
	float x;
	float y;
	float xVel;
	float yVel;

	public Bullet(MyGame game, float x, float y, float xDest, float yDest) {
		this.x = x;
		this.y = y;
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

}
