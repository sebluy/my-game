package com.sebluy.mygame;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class GameMap {

	final ArrayList<Wall> walls;
	private Vector2 position;
	private final MyGame game;

	public GameMap(MyGame game) {
		this.game = game;
		walls = new ArrayList<>();
		setPosition(0, 0)
				.verticalWall(1000)
				.horizontalWall(1000)
				.verticalWall(-1000)
				.horizontalWall(-1000)
				.setPosition(500, 0)
				.verticalWall(750)
				.horizontalWall(250)
				.verticalWall(-350)
				.setPosition(0, 250)
				.horizontalWall(300)
				.verticalWall(400);
	}

	private GameMap setPosition(float x, float y) {
		position = new Vector2(x, y);
		return this;
	}

	private GameMap horizontalWall(float length) {
		Vector2 nextPosition = new Vector2(position.x + length, position.y);
		walls.add(new Wall(game, position, nextPosition));
		position = nextPosition;
		return this;
	}

	private GameMap verticalWall(float length) {
		Vector2 nextPosition = new Vector2(position.x, position.y + length);
		walls.add(new Wall(game, position, nextPosition));
		position = nextPosition;
		return this;
	}

	private GameMap wallTo(float x, float y) {
		Vector2 nextPosition = new Vector2(x, y);
		walls.add(new Wall(game, position, nextPosition));
		position = nextPosition;
		return this;
	}

	public void render() {
		for (Wall wall : walls) {
			wall.render();
		}
	}

}
