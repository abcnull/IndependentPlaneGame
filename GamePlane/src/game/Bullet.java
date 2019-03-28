package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 子弹类，可能垂直运动，可能水平运动
 */

public class Bullet extends AutoSprite {
	//横向子弹速度
	private float speed2 = 0;
	private boolean rightOrientation = false;
	private boolean upOrientation = true;

    public Bullet(Bitmap bitmap){
        super(bitmap);
    }
    
    //重写beforeDraw()方法，因为有的子弹垂直移动，有的子弹左右移动
    @Override
	protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
		if(!isDestroyed()){
			//若是纵向子弹
			if(this.speed2 == 0){
				super.beforeDraw(canvas, paint, gameView);
			}
			//若是横向子弹
			else{
				move(speed2 * gameView.getDensity(), 0);
			}
		}
	}
    
    //设置纵向子弹速度
    public void setSpeedAndUpOrientation(float verticalSpeed, boolean orientation){
    	super.setSpeed(verticalSpeed);//设置垂直速度
    	this.speed2 = 0;//设置水平速度    	
    	this.rightOrientation = false;//设置方向是否向右
    	this.upOrientation = orientation;//设置方向是否向上
    }

	//设置横向子弹速度
    public void setSpeed2AndRightOrientation(float horizontalSpeed, boolean orientation){
    	super.setSpeed(0);//设置垂直速度
    	this.speed2 = horizontalSpeed;//设置水平速度        
        this.rightOrientation = orientation;//设置方向是否向右
        this.upOrientation = false;//设置方向是否向上       
    }

    //得到横向子弹速度
    public float getSpeed2(){
        return speed2;//得到水平方向速度
    }
    
    //得到一个横向方向的布尔值，向右是true，向左是false
    public boolean getRightOrientation(){
    	return rightOrientation;//返回是否方向向右
    }
    
    //得到一个纵向方向的布尔值，向上为true，向下为false
    public boolean getUpOrientation(){
    	return upOrientation;//返回是否方向向上
    }
}