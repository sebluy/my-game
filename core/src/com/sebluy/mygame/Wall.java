package com.sebluy.mygame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Wall {

	private static final float WIDTH = 4;

	MyGame game;
	Vector2 p1;
	Vector2 p2;
	private Rectangle boundingRectangle;

	public Wall(MyGame game, Vector2 p1, Vector2 p2) {
		this.game = game;
		this.p1 = p1;
		this.p2 = p2;
	}

	public void render() {
		game.shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1);
		game.shapeRenderer.rectLine(p1, p2, WIDTH);
		game.shapeRenderer.setColor(1.0f, 0, 0, 1);
	}

	public Rectangle getBoundingRectangle() {
		if (boundingRectangle != null) return boundingRectangle;
		float x, y, width, height;
		if (isVertical()) {
			x = p1.x - WIDTH / 2;
			y = Math.min(p1.y, p2.y);
			width = WIDTH;
			height = Math.abs(p2.y - p1.y);
		} else {
			x = Math.min(p1.x, p2.x);
			y = p1.y - WIDTH / 2;
			width = Math.abs(p2.x - p1.x);
			height = WIDTH;
		}
		boundingRectangle = new Rectangle(x, y, width, height);
		return boundingRectangle;
	}

	public boolean isVertical() {
		return p1.x == p2.x;
	}
}
