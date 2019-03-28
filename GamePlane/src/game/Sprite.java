package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 精灵类，所有其他用于绘制的类的基类
 */

public class Sprite {
    private boolean visible = true;//可视性
    private float x = 0;//X
    private float y = 0;//Y
    private float collideOffset = 0;//碰撞检测的长度额度
    private Bitmap bitmap = null;//位图
    private boolean destroyed = false;//是否被撞
    private int frame = 0;//绘制的次数

    //构造方法
    public Sprite(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    //绘制方法
    public final void draw(Canvas canvas, Paint paint, GameView gameView){
        frame++;
        beforeDraw(canvas, paint, gameView);
        toDraw(canvas, paint, gameView);
        afterDraw(canvas, paint, gameView);
    }

    //beforeDraw()绘制
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView){}

    //toDraw()绘制
    public void toDraw(Canvas canvas, Paint paint, GameView gameView){
        if(!destroyed && this.bitmap != null && getVisibility()){
            //将Sprite绘制到Canvas上
            Rect srcRef = getBitmapSrcRec();
            RectF dstRecF = getRectF();
            //canvas.drawBitmap(this.bitmap, x, y, paint);
            canvas.drawBitmap(bitmap, srcRef, dstRecF, paint);
        }
    }

    //afterDraw()绘制
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView){}
    
    //得到位图
    public Bitmap getBitmap(){
        return bitmap;
    }
    
    //设置位图
    public void setBitmap(Bitmap bitmap){
    	this.bitmap = bitmap;
    }

    //设置可视性
    public void setVisibility(boolean visible){
        this.visible = visible;
    }

    //得到可视性
    public boolean getVisibility(){
        return visible;
    }

    //设置X
    public void setX(float x){
        this.x = x;
    }

    //得到X
    public float getX(){
        return x;
    }

    //设置Y
    public void setY(float y){
        this.y = y;
    }

    //得到Y
    public float getY(){
        return y;
    }

    //得到精灵位图宽度
    public float getWidth(){
        if(bitmap != null){
            return bitmap.getWidth();
        }
        return 0;
    }

    //得到精灵位图高度
    public float getHeight(){
        if(bitmap != null){
            return bitmap.getHeight();
        }
        return 0;
    }

    //去移动
    public void move(float offsetX, float offsetY){
        x += offsetX;
        y += offsetY;
    }

    //移动至
    public void moveTo(float x, float y){
        this.x = x;
        this.y = y;
    }

    //以(cneterX. centerY)为位图中心得到的(x, y)坐标
    public void centerTo(float centerX, float centerY){
        float w = getWidth();
        float h = getHeight();
        x = centerX - w / 2;
        y = centerY - h / 2;
    }

    //得到位图RectF
    public RectF getRectF(){
        float left = x;
        float top = y;
        float right = left + getWidth();
        float bottom = top + getHeight();
        RectF rectF = new RectF(left, top, right, bottom);
        return rectF;
    }

    //得到以(0, 0)为左上角的位图的Rect
    public Rect getBitmapSrcRec(){
        Rect rect = new Rect();
        rect.left = 0;
        rect.top = 0;
        rect.right = (int)getWidth();
        rect.bottom = (int)getHeight();
        return rect;
    }

    //得到在加上碰撞检测额度后的RectF，实际上就是RectF，因为碰撞检测长度额度我设为0了
    public RectF getCollideRectF(){
        RectF rectF = getRectF();
        rectF.left -= collideOffset;
        rectF.right += collideOffset;
        rectF.top -= collideOffset;
        rectF.bottom += collideOffset;
        return rectF;
    }

    //得到碰撞点
    public Point getCollidePointWithOther(Sprite s){
        Point p = null;
        RectF rectF1 = getCollideRectF();
        RectF rectF2 = s.getCollideRectF();
        RectF rectF = new RectF();
        boolean isIntersect = rectF.setIntersect(rectF1, rectF2);
        if(isIntersect){
            p = new Point(Math.round(rectF.centerX()), Math.round(rectF.centerY()));
        }
        return p;
    }

    //销毁精灵：位图空，置销毁态
    public void destroy(){
        bitmap = null;
        destroyed = true;
    }

    //返回是否被销毁
    public boolean isDestroyed(){
        return destroyed;
    }

    //得到精灵绘制的帧数
    public int getFrame(){
        return frame;
    }
}