package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PersonPath {

    static final float SIZE = 20f;

    List<Vector2> points;
    List<Vector2> directions;
    Vector2 lastPoint;
    MyGame game;

    public PersonPath(MyGame game) {
        points = new ArrayList<>();
        directions = new ArrayList<>();
        this.game = game;
    }

    public void input(Vector3 click) {
        if (points.size() > directions.size()) {
            directions.add((new Vector2(click.x, click.y)).sub(lastPoint).nor());
        } else {
            lastPoint = new Vector2(click.x, click.y);
            points.add(lastPoint);
        }
    }

    public void render() {
        ShapeRenderer sr = game.shapeRenderer;
        for (int i = 0; i < points.size(); i++) {
            Vector2 point = points.get(i);
            sr.setColor(1, 0, 0, 1);
            sr.circle(point.x, point.y, SIZE);
            sr.setColor(0, 1, 0, 1);
            if (i < directions.size()) {
                Vector2 direction = directions.get(i);
                sr.rectLine(
                        point.x,
                        point.y,
                        point.x + direction.x * SIZE,
                        point.y + direction.y * SIZE,
                        5f
                );
            }
        }
    }



}
