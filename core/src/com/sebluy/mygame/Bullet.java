package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Bullet {

	final static double SPEED = 200.0; // Pixels / second
	static int currentId = 1;

	ShapeRenderer shapeRenderer;
	int id;
	double x;
	double y;
	double xVel;
	double yVel;

	public Bullet(MyGame game, double x, double y, double xDest, double yDest) {
		this.x = x;
		this.y = y;
		double xDel = xDest - x;
		double yDel = yDest - y;
		id = currentId;
		currentId += 1;
		xVel = SPEED / Math.sqrt(1 + (yDel * yDel) / (xDel * xDel));
		yVel = SPEED / Math.sqrt(1 + (xDel * xDel) / (yDel * yDel));
		if (xDel < 0 ) xVel = -xVel;
		if (yDel < 0 ) yVel = -yVel;
		this.shapeRenderer = game.shapeRenderer;
		game.bullets.put(id, this);
	}

	public void render() {
		shapeRenderer.setColor(1, 0, 0, 1);
		shapeRenderer.circle((int)x, (int)y, 5);
		float delta = Gdx.graphics.getDeltaTime();
		x += xVel * delta;
		y += yVel * delta;
	}

}
