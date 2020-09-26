package tech.zeroed.doover.gameobjects.enemy;

import tech.zeroed.doover.gameobjects.ai.BossAI;

public class Boss extends Enemy {

    public float hoverHeight;

    public Boss(){
        super();

        this.boundingBoxWidth = 50;
        this.boundingBoxHeight = 50;
        this.boundingBoxY = 0;
        this.boundingBoxX = 3;
        this.gravityX = 0;
        this.gravityY = 0;

        this.moveSpeedX = 50;
        this.moveSpeedY = 0;

        ai = new BossAI(this);
    }

    @Override
    public void run(float delta) {
        super.run(delta);
        if(((BossAI)ai).getState() == BossAI.STATE.Null){
            ((BossAI)ai).setState(BossAI.STATE.Hover);
        }
    }

    @Override
    public void debugDraw() {
        super.debugDraw();
        ai.debugDraw();
    }
}
