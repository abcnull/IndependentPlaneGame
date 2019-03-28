package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 包括敌机类，奖励道具类，子弹类，火球类
 * 一般为直上直下，但是子弹类也可以左右移动
 * 在子弹类重写AutoSprite的BeforeDraw()方法来实现子弹类的左右移动
 */

public class AutoSprite extends Sprite {
    //每帧移动的像素数
    private float speed = 2;

    //构造方法
    public AutoSprite(Bitmap bitmap){
        super(bitmap);
    }

    //设置速度
    public void setSpeed(float speed){
        this.speed = speed;
    }

    //得到速度
    public float getSpeed(){
        return speed;
    }

    //重写Sprite的beforeDraw()方法，实际上Sprite的此方法为空，AutoSprite是通过beforeDraw()来改变位置的
    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(!isDestroyed()){
            //在y轴方向移动speed像素
            move(0, speed * gameView.getDensity());
        }
    }

    //重写Sprite的afterDraw()方法，实际上Sprite的此方法为空，AutoSprite是通过afterDraw()来讲超过画布的此类销毁的
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView){
        if(!isDestroyed()){
            //检查Sprite是否超出了Canvas的范围，如果超出，则销毁Sprite
            RectF canvasRecF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
            RectF spriteRecF = getRectF();
            //Rect自带的方法，两矩形相离该精灵就会被销毁
            if(!RectF.intersects(canvasRecF, spriteRecF)){
                destroy();
            }
        }
    }
}