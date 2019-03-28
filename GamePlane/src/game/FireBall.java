package game;

import android.graphics.Bitmap;

/**
 * 直接继承自AutoSprite
 */

public class FireBall extends AutoSprite {
	public FireBall(Bitmap bitmap){
        super(bitmap);
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
        //销毁敌机
        destroy();
    }
	
}
