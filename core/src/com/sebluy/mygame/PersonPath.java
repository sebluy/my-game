package com.sebluy.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class PersonPath {

    static final float SIZE = 20f;
    private static final float RADIUS = 40f;
    static Texture texture;

    List<Sprite> sprites;
    List<Vector2> points;
    List<Vector2> directions;
    Vector2 lastPoint;
    MyGame game;
    private boolean missingDirection;

    public PersonPath(MyGame game) {
        if (texture == null) {
            texture = new Texture(
                    Gdx.files.internal("Top_Down_Survivor/rifle/idle/survivor-idle_rifle_0.png")
            );
        }
        points = new ArrayList<>();
        directions = new ArrayList<>();
        sprites = new ArrayList<>();
        this.game = game;
    }

    public void input(Vector3 click) {
        if (points.size() > directions.size()) {
            Vector2 direction = toV2(click).sub(lastPoint).nor();
            directions.add(direction);
            sprites.get(sprites.size() - 1).setRotation(direction.angleDeg());
            missingDirection = false;
        } else {
            lastPoint = toV2(click);
            points.add(lastPoint);
            sprites.add(makeSprite(lastPoint));
            missingDirection = true;
        }
    }

    private Vector2 toV2(Vector3 v3) {
        return new Vector2(v3.x, v3.y);
    }

    public void renderShapes() {
        ShapeRenderer sr = game.shapeRenderer;
        for (int i = 0; i < points.size(); i++) {
            Vector2 point = points.get(i);
            sr.setColor(1, 0, 0, 1);
            sr.circle(point.x, point.y, SIZE);
            sr.setColor(0, 1, 0, 1);
            Vector2 direction;
            if (i < directions.size()) {
                direction = directions.get(i);
            } else {
                Vector2 mouse = toV2(game.unproject(Gdx.input.getX(), Gdx.input.getY()));
                direction = mouse.sub(point).nor();
            }
            sr.rectLine(
                    point.x,
                    point.y,
                    point.x + direction.x * SIZE,
                    point.y + direction.y * SIZE,
                    5f
            );
        }
    }

    public void renderSprites() {
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.get(i);
            if (missingDirection && (i == sprites.size() - 1)) {
                Vector2 mouse = toV2(game.unproject(Gdx.input.getX(), Gdx.input.getY()));
                Vector2 pos = new Vector2(sprite.getX(), sprite.getY());
                float angle = mouse.sub(pos).nor().angleDeg();
                sprite.setRotation(angle);
            }
            sprite.draw(game.batch);
            game.font.draw(game.batch, String.valueOf(i), sprite.getX(), sprite.getY());
        }
    }

    private Sprite makeSprite(Vector2 pos) {
        Sprite sprite = new Sprite(texture);
        sprite.setSize(RADIUS * 2, RADIUS * 2);
        sprite.setOriginCenter();
        sprite.setCenter(pos.x, pos.y);
        return sprite;
    }
}
