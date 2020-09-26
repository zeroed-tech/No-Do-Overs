package tech.zeroed.doover.gameobjects.ai;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.doover.GameWorld;
import tech.zeroed.doover.gameobjects.enemy.Enemy;

public class Swoop extends AI {

    public Bezier<Vector2> arc;
    public Vector2 p1;
    public Vector2 p2;
    public Vector2 p3;
    public Vector2 p4;

    Vector2 tmp = new Vector2();
    float alpha = 0;
    boolean flip = false;

    public Swoop(Enemy target) {
        super(target);

        p1 = new Vector2();
        p2 = new Vector2();
        p3 = new Vector2();
        p4 = new Vector2();

        arc = new Bezier<>(p1, p2, p3, p4);
    }

    public void swoopComplete(){
        flip = !flip;
        alpha = MathUtils.clamp(alpha, 0, 1);
    }

    @Override
    public void update(float delta) {
        if((!flip && alpha < 1) || (flip && alpha > 0)) {
            arc.valueAt(tmp, alpha);
            alpha += (delta/4f) * (flip ? -1 : 1);
            target.x = tmp.x-target.boundingBoxWidth/2;
            target.y = tmp.y-target.boundingBoxHeight/2;
        }else{
            swoopComplete();
        }
    }

    @Override
    public void debugDraw() {
        super.debugDraw();

        float oldColor = GameWorld.shapeDrawer.getPackedColor();
        GameWorld.shapeDrawer.setColor(Color.GREEN);
        GameWorld.shapeDrawer.setDefaultLineWidth(1f);

        float val = 0;

        while(val <= 1f){
            arc.valueAt(tmp, val);
            GameWorld.shapeDrawer.circle(tmp.x, tmp.y, 1);
            val += 0.005f;
        }
        GameWorld.shapeDrawer.setColor(oldColor);
    }
}
