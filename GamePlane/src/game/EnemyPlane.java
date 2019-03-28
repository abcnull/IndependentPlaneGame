package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.List;

/**
 * 敌机类，从上向下沿直线运动
 */
public class EnemyPlane extends AutoSprite {

    private int power = 1;//敌机的抗打击能力
    private int value = 0;//打一个敌机的得分

    //构造方法
    public EnemyPlane(Bitmap bitmap){
        super(bitmap);
    }
    
    //重写afterDraw()方法
    @Override
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
    	//重写
        super.afterDraw(canvas, paint, gameView);
        //绘制完成后要检查自身是否被子弹打中
        if(!isDestroyed()){
            List<Bullet> bullets = gameView.getAliveBullets();
            for(Bullet bullet : bullets){
                //判断敌机是否与子弹相交
                Point p = getCollidePointWithOther(bullet);
                //若相交
                if(p != null){
                    bullet.destroy();//子弹销毁
                    power--;//抗击打力，也就是血条减1
                    //当血条扣完
                    if(power <= 0){
                        explode(gameView);//敌机爆炸：敌机将爆炸位图加入到集合中
                        return;
                    }
                }
            }
        }
    }

    //创建爆炸效果后会销毁敌机
    public void explode(GameView gameView){
        //创建爆炸效果
        float centerX = getX() + getWidth() / 2;
        float centerY = getY() + getHeight() / 2;
        Bitmap bitmap = gameView.getExplosionBitmap();
        Explosion explosion = new Explosion(bitmap);
        explosion.centerTo(centerX, centerY);//初始化(x, y)
        gameView.addSprite(explosion);//加入集合
        //创建爆炸效果完成后，向GameView中添加得分并销毁敌机
        gameView.addScore(value);
        //销毁敌机
        destroy();
    }
    
    //设置抗击打能力
    public void setPower(int power){
        this.power = power;
    }

    //得到抗击打能力
    public int getPower(){
        return power;
    }

    //设置一个敌机的得分
    public void setValue(int value){
        this.value = value;
    }

    //得到一个低级的得分
    public int getValue(){
        return value;
    }
}