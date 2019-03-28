package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 爆炸效果类，位置不可变，但是可以显示动态的爆炸效果
 */

public class Explosion extends Sprite {

    private int segment = 14;//爆炸效果由14个片段组成
    private int level = 0;//最开始处于爆炸的第0片段
    private int explodeFrequency = 2;//每个爆炸片段绘制2帧

    //构造方法
    public Explosion(Bitmap bitmap){
        super(bitmap);
    }

    //重写afterDraw()，要不断换为下一个片段，需要不断在位图上移动
    @Override
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(!isDestroyed()){
            if(getFrame() % explodeFrequency == 0){
                //level自加1，用于绘制下个爆炸片段
                level++;
                if(level >= segment){
                	if(getBitmap() == gameView.getFireBitmap()){
                		level = 0;
                	}
                	else if(getBitmap() == gameView.getExplosionBitmap()){
                    	//当绘制完所有的爆炸片段后，销毁爆炸效果
                    	destroy();
                	}
                }
            }
        }
    }
    
    //重写Sprite中得到位图宽度，这里实际得到的是一个片段的宽度，一共有14个片段，也就是位图宽度/14
    @Override
    public float getWidth() {
        Bitmap bitmap = getBitmap();
        if(bitmap != null){
            return bitmap.getWidth() / segment;
        }
        return 0;
    }

    //重写以(0, 0)为左上角得到矩形类，下载改变左上角坐标了，因为这个爆炸位图很宽，长度有14个片段的长度
    @Override
    public Rect getBitmapSrcRec() {
        Rect rect = super.getBitmapSrcRec();
        int left = (int)(level * getWidth());
        rect.offsetTo(left, 0);
        return rect;
    }

    //得到绘制完整爆炸效果需要的帧数，即28帧
    public int getExplodeDurationFrame(){
        return segment * explodeFrequency;
    }
}