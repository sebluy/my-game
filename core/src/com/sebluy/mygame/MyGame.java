package com.sebluy.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyGame extends ApplicationAdapter {
	ShapeRenderer shapeRenderer;
	Map<Integer, Person> people;
	Map<Integer, Bullet> bullets;

	@Override
	public void create () {
		shapeRenderer = new ShapeRenderer();

		people = new HashMap<>();
		bullets = new HashMap<>();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int[] ys = { height / 10, height * 9 / 10 };
		for (int y : ys) {
			for (int x = width / 10 ; x < width; x += width / 10) {
				new Person(this, x, y);
			}
		}
		for (Person p : people.values()) {
			System.out.println(p);
		}
		people.get(3).shoot(people.get(17));
		people.get(1).shoot(people.get(10));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(.25f, .25f, .25f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		for (Person p : people.values()) {
			p.render();
		}
		for (Bullet b : bullets.values()) {
			b.render();
		}
	}

	@Override
	public void dispose () {
		shapeRenderer.dispose();
	}
}
