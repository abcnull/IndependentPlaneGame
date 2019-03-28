package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 老板敌机类，体积巨大，抗打击能力超强
 */

public class BossEnemyPlane extends EnemyPlane {
	//横向移动的速度
	private float speed2 = 1;

	//构造方法
	public BossEnemyPlane(Bitmap bitmap){
        super(bitmap);
        setPower(80);//大敌机抗抵抗能力为80，即需要80颗子弹才能销毁大敌机
        setValue(200);//销毁一个大敌机可以得200分
    }

	//重写beforeDraw()
	@Override
	protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
		if(!isDestroyed()){
			//当y>0，不再向下运行，先向右运行，然后向左运行	
			if(getY() >= 0){
				//当在画布之内运动时
				if(getX() <= canvas.getWidth() - getWidth() && getX() >= 0)
					move(speed2 * gameView.getDensity(), 0);
				//将要运动到画布外时
				else{
					setSpeed2(- speed2);
					//掉头
					move(speed2 * gameView.getDensity(), 0);
				}
				//只有左右运行时候，每50帧发射一个火球
				if(getFrame() % 55 == 0){
	                fight(gameView);
	            }				
			}
			//当老板敌机还没完全落下来时
			else				
				//垂直移动
				super.beforeDraw(canvas, paint, gameView);
		}
	}

	public void fight(GameView gameView){
		if(isDestroyed()){
            return;
        }
		float x = getX() + getWidth() / 2;//火球X
        float y = getY() + getHeight();//
		Bitmap fireBallBitmap = gameView.getFireBallBitmap();
        FireBall fireBall = new FireBall(fireBallBitmap);
        y = y - fireBall.getHeight();//火球Y
        fireBall.moveTo(x, y);//初始化(x, y)
        fireBall.setSpeed(3);//初始化速度
        gameView.addSprite(fireBall);//将火球加入到集合中
	}
	
	//设置横向移动速度
	public void setSpeed2(float speed){
		this.speed2 = speed;	
	}
	
	//得到横向移动速度
	public float getSpeed2(){
		return this.speed2;
	}
}