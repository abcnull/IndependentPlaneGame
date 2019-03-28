package game;

import activitys.GameActivity;
import activitys.MainActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.example.helloworld.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class GameView extends View {

	/*------------------------------ fields 属性部分 ------------------------------*/
	
	//画笔和尺寸变量
    private Paint paint;//画图画笔
    private Paint textPaint;//文字画笔
    private float fontSize = 12;//默认的字体大小，用于绘制左上角的文本
    private float fontSize2 = 20;//用于在Game Over的时候绘制Dialog中的文本
    private float borderSize = 2;//Game Over的Dialog的边框
    
    //集合类变量
    private List<Sprite> sprites = new ArrayList<Sprite>();//用于存储整个界面的精灵
    private List<Sprite> spritesLaterAdded = new ArrayList<Sprite>();//用于存储下一帧将要被新加入的精灵类
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();//游戏中要用到的位图集合
    //0:combatAircraft
    //1:explosion
    //2:yellowBullet
    //3:blueBullet
    //4:smallEnemyPlane
    //5:middleEnemyPlane
    //6:bigEnemyPlane
    //7:bombAward
    //8:bulletAward
    //9:pause1
    //10:pause2
    //11:bomb
    //12:bossEnemyPlane
    //13:fireBall
    //14:sideBulletAward
    //15:tailBulletAward
    //16:life
    //17:lifeAward
    //18:combatAircraft2
    //19:combatAircraft3
    //20:fire
    
    //游戏难易程度
    public static final int EASY_MODEL = 1;//简单难度
    public static final int NORMAL_MODEL = 2;//正常难度
    public static final int ELITE_MODEL = 3;//地狱难度
    private int degree = EASY_MODEL;//初始为简单难度
    
    //游戏状态变量
    public static final int STATUS_GAME_STARTED = 1;//游戏开始
    public static final int STATUS_GAME_PAUSED = 2;//游戏暂停
    public static final int STATUS_GAME_OVER = 3;//游戏结束
    public static final int STATUS_GAME_DESTROYED = 4;//游戏销毁
    private int status = STATUS_GAME_DESTROYED;//初始为销毁状态
    
    //触屏相关变量
    //触屏动作基本属性
    private long lastSingleClickTime = -1;//上次发生点击的时刻
    private long touchDownTime = -1;//触点按下的时刻
    private long touchUpTime = -1;//触点抬起的时刻
    private float touchX = -1;//触点的x坐标
    private float touchY = -1;//触点的y坐标
    //触屏动作变量
    private static final int TOUCH_MOVE = 1;//移动
    private static final int TOUCH_SINGLE_CLICK = 2;//单击
    private static final int TOUCH_DOUBLE_CLICK = 3;//双击
    //触屏事件时间相关
    private static final int singleClickDurationTime = 200;//抬起按下之间小于200ms才认为发生一次单击
    private static final int doubleClickDurationTime = 300;//两次单机之间小于300ms才认为发生一次双击
    
    //FPS相关变量
    private long lastFPSFrame = 0;//上一次计算FPS的帧数
    private long lastFPSTime = 0;//上一次计算FPS的时间
    private int fps = 0;//FPS
    
    //其他基本属性
    private Activity activity = null;
    private CombatAircraft combatAircraft = null;//战斗机
    private long score = 0;//总得分
    private long frame = 0;//总共绘制的帧数
    private Rect continueRect = new Rect();//"继续"、"重新开始"按钮的Rect矩形
    private Rect backRect = new Rect();//"主界面"按钮的Rect矩形类
    private float density = getResources().getDisplayMetrics().density;//屏幕密度        
    
    /*------------------------------ constructor 构造方法部分 ------------------------------*/
    
    //3个构造方法
    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /*------------------------------ ready 初始准备工作部分 ------------------------------*/
    
    //用来初始化两种画笔和一些字体尺码，置于构造方法中初始化，每次创建一个该类对象可以成功率先初始化
    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.GameView, defStyle, 0);
        a.recycle();
        //初始化paint
        paint = new Paint();
        //设置paint为填充
        paint.setStyle(Paint.Style.FILL);
        //设置textPaint，设置为抗锯齿，且是粗体
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        //设置textPaint颜色
        textPaint.setColor(0xff000000);
        //设置字体和边框尺码
        fontSize = textPaint.getTextSize();
        fontSize *= density;
        fontSize2 *= density;
        textPaint.setTextSize(fontSize);
        borderSize *= density;
    }

    //第一步准备工作，游戏界面的准备和难度模式的选择
    public void startReady(String model, Context context){
    	//先清理一下界面
        destroy();   
        //活动赋值
        this.activity = (Activity)context;
        //0:combatAircraft
        //1:explosion
        //2:yellowBullet
        //3:blueBullet
        //4:smallEnemyPlane
        //5:middleEnemyPlane
        //6:bigEnemyPlane
        //7:bombAward
        //8:bulletAward
        //9:pause1
        //10:pause2
        //11:bomb
        //12:bossEnemyPlane
        //13:fireBall
        //14:sideBulletAward
        //15:tailBulletAward    
        //16:life
        //17:lifeAward
        //18:combatAircraft2
        //19:combatAircraft3
        //20:fire
        int[] bitmapIds = {
                R.drawable.combatplane,     	//[0]
                R.drawable.explosion,			//[1]
                R.drawable.yellow_bullet,		//[2]
                R.drawable.blue_bullet,			//[3]
                R.drawable.smallenemyplane,		//[4]
                R.drawable.middleenemyplane,	//[5]
                R.drawable.bigenemyplane,		//[6]
                R.drawable.bombaward,			//[7]
                R.drawable.doublebulletaward,	//[8]
                R.drawable.pause1,				//[9]
                R.drawable.pause2,				//[10]
                R.drawable.bomb,				//[11]
                R.drawable.bossenemyplane,		//[12]
                R.drawable.fireball,			//[13]
                R.drawable.sidebulletaward,		//[14]
                R.drawable.tailbulletaward,		//[15]
                R.drawable.life,				//[16]
                R.drawable.lifeaward,			//[17]
                R.drawable.combatplane2,		//[18]
                R.drawable.combatplane3,		//[19]
                R.drawable.fire					//[20]
        };       
        //位图准备
        for(int bitmapId : bitmapIds){
        	//通过位图工厂来生成位图
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), bitmapId);
            //将将这些资源id匹配的位图加入到位图集合中去
            bitmaps.add(bitmap);
        }
        //难度模式的选择匹配
        if(model == "elite")
        	degree = ELITE_MODEL;//很难
        else if(model == "normal")
        	degree = NORMAL_MODEL;//一般
        else
        	degree = EASY_MODEL;//容易
        //第二步准备工作
        startWhenReady();
    }
    
    //第二步准备工作，主要是初始化战斗机，然后设置游戏状态，最后重绘
    private void startWhenReady(){
    	//初始化战斗机
        combatAircraft = new CombatAircraft(bitmaps.get(0));
        //将游戏设置为开始状态
        status = STATUS_GAME_STARTED;
        //重绘
        postInvalidate();
    }
    
    /*------------------------------ onDraw() 绘制部分 ------------------------------*/
    
    @Override
    protected void onDraw(Canvas canvas) {
        //我们在每一帧都检测是否满足延迟触发单击事件的条件
        if(isSingleClick()){
        	//若满足最近的上一次有单击事件，则检测单击事件的发生位置，来判断是否点了什么按钮会改变游戏状态
            onSingleClick(touchX, touchY);
        }
        //重写
        super.onDraw(canvas);
        //根据不同游戏状态来绘制界面
        if(status == STATUS_GAME_STARTED){
            drawGameStarted(canvas);//绘制游戏进行态
        }else if(status == STATUS_GAME_PAUSED){
            drawGamePaused(canvas);//绘制游戏暂停态
        }else if(status == STATUS_GAME_OVER){
            drawGameOver(canvas);//绘制游戏结束态
        }
    }
    
    /*------------------------------ 3中不同状态的游戏绘制部分 ------------------------------*/
    
    //绘制游戏进行态
    private void drawGameStarted(Canvas canvas){
    	//决定背景是哪一个
    	if((frame + 1) % (30 * 90) == 0){
    		Resources res = getResources();
    		TransitionDrawable imageTransitionDrawable = null;
    		if(((frame + 1) / (30 * 90)) % 2 == 1){
    			imageTransitionDrawable = new TransitionDrawable(new Drawable[]{res.getDrawable(R.drawable.bgsky),res.getDrawable(R.drawable.bgnight)});
    		}
    		else{
    			imageTransitionDrawable = new TransitionDrawable(new Drawable[]{res.getDrawable(R.drawable.bgnight),res.getDrawable(R.drawable.bgsky)});    			
    		}
    		this.setBackgroundDrawable(imageTransitionDrawable);
            imageTransitionDrawable.startTransition(4000);
    		
    	}
        //第一次绘制时，将战斗机移到Canvas最下方，在水平方向的中心
        if(frame == 0){
            float centerX = canvas.getWidth() / 2;
            float centerY = canvas.getHeight() - combatAircraft.getHeight() / 2;
            combatAircraft.centerTo(centerX, centerY);
        }
        
        //将spritesLaterAdded添加到sprites中
        if(spritesLaterAdded.size() > 0){
            sprites.addAll(spritesLaterAdded);
            spritesLaterAdded.clear();
        }
        //检查子弹超过战斗机的情况
        destroyBulletsBeyondCombatAircraft();
        //在绘制之前先移除掉已经被destroyed的Sprite
        removeDestroyedSprites();
        
        //每隔30帧随机添加Sprite
        if(frame % 30 == 0){
            createRandomSprites(canvas.getWidth());
        }
        frame++;

        //先绘制左上角和左下角图片和文字
        drawCorners(canvas);
        
        //绘制除战斗机的精灵部分：遍历sprites，绘制敌机、子弹、奖励、爆炸效果
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()){
            Sprite s = iterator.next();
            //绘制精灵类，包括敌机类，奖励道具类，子弹类，火球类，爆炸类
            if(!s.isDestroyed()){
                //在Sprite的draw方法内有可能会调用destroy方法
                s.draw(canvas, paint, this);
            }
            //我们此处要判断Sprite在执行了draw方法后是否被destroy掉了
            if(s.isDestroyed()){
                //如果Sprite被销毁了，那么从Sprites中将其移除
                iterator.remove();
            }
        }
        //绘制战斗机部分
        if(combatAircraft != null){
            //最后绘制战斗机
            combatAircraft.draw(canvas, paint, this);
            //每次通过战斗机是否被设置为销毁态来改变游戏状态
            if(combatAircraft.isDestroyed()){
                //如果战斗机被击中销毁了，那么游戏结束
                status = STATUS_GAME_OVER;
            }
            //重绘
            postInvalidate();
        }
    }
    
    //绘制游戏暂停态
    private void drawGamePaused(Canvas canvas){
    	//同绘制游戏进行态一样，先绘制游戏左上角和左下角
        drawCorners(canvas);
        //调用Sprite的toDraw方法，而非draw方法，这样就能绘制静态的精灵，由于一般在beforeDraw()中来改变精灵位置
        for(Sprite s : sprites){
            s.toDraw(canvas, paint, this);
        }
        //绘制静态的战斗机类
        if(combatAircraft != null){
            combatAircraft.toDraw(canvas, paint, this);
        }
        //绘制Dialog，显示得分
        drawScoreDialog(canvas, "继续");
        //若检测到距离最近的上一次有点击事件发生，那么就会重绘
        if(lastSingleClickTime > 0){
            postInvalidate();
        }
    }
    
    //绘制游戏结束态
    private void drawGameOver(Canvas canvas){
    	//绘制Dialog，显示得分
        drawScoreDialog(canvas, "重新开始");
        //若检测到距离最近的上一次有点击事件发生，那么就会重绘
        if(lastSingleClickTime > 0){
            postInvalidate();
        }
    }
    
    /*------------------------------ drawGameStarted()游戏进行态内的主要方法 ------------------------------*/
    
    //绘制左上角的得分和左下角炸弹的数量
    private void drawCorners(Canvas canvas){
        //绘制左上角的暂停按钮
        Bitmap pauseBitmap = status == STATUS_GAME_STARTED ? bitmaps.get(9) : bitmaps.get(10);
        RectF pauseBitmapDstRecF = getPauseBitmapDstRecF();
        float pauseLeft = pauseBitmapDstRecF.left;
        float pauseTop = pauseBitmapDstRecF.top;
        canvas.drawBitmap(pauseBitmap, pauseLeft, pauseTop, paint);
        //绘制左上角的总得分数
        float scoreLeft = pauseLeft + pauseBitmap.getWidth() + 20 * density;
        float scoreTop = fontSize + pauseTop + pauseBitmap.getHeight() / 2 - fontSize / 2;
        canvas.drawText(score + "", scoreLeft, scoreTop, textPaint);
        //绘制左下角
        if(combatAircraft != null && !combatAircraft.isDestroyed()){
            int bombCount = combatAircraft.getBombCount();
            //绘制左下角的炸弹
            Bitmap bombBitmap = bitmaps.get(11);
            float bombTop = canvas.getHeight() - bombBitmap.getHeight();
            canvas.drawBitmap(bombBitmap, 0, bombTop, paint);
            //绘制左下角的炸弹数量
            float bombCountLeft = bombBitmap.getWidth() + 10 * density;
            float bombCountTop = fontSize + bombTop + bombBitmap.getHeight() / 2 - fontSize / 2;
            canvas.drawText("X " + bombCount, bombCountLeft, bombCountTop, textPaint);
        }
        //绘制右上角生命值
        if(combatAircraft != null && !combatAircraft.isDestroyed()){
        	int life = combatAircraft.getLife();
        	//绘制生命值方法
        	drawLifes(canvas, life);
        }
        //在游戏进行态时才绘制FPS，绘制右下角FPS
        if(status == STATUS_GAME_STARTED){
	        fps = getFPS();
	        float FPSLeft = canvas.getWidth() - 50 * density;
	        float FPSTop = canvas.getHeight() - fontSize - 10 * density;
	        canvas.drawText("FPS: " + fps, FPSLeft, FPSTop, textPaint);
        }
    }
    
    //绘制右上角生命值方法
    private void drawLifes(Canvas canvas, int life){
    	Bitmap lifeBitmap = bitmaps.get(16);//得到心形图片
    	float lifeWidth = lifeBitmap.getWidth();//得到此图片宽度
    	//若生命值大于1，绘制第一颗心
    	if(life >= 1)
    		canvas.drawBitmap(lifeBitmap, canvas.getWidth() - 1 * density - lifeWidth, 15 * density, paint);
    	if(life >= 2){
    		canvas.drawBitmap(lifeBitmap, canvas.getWidth() - 2 * 1 * density - 2 * lifeWidth, 15 * density, paint);
    	}
    	if(life >= 3){
    		canvas.drawBitmap(lifeBitmap, canvas.getWidth() - 3 * 1 * density - 3 * lifeWidth, 15 * density, paint);
    	}
    }
    
    //检查子弹超过战斗机的情况，子弹超过的就销毁子弹
    private void destroyBulletsBeyondCombatAircraft(){
        if(combatAircraft != null){
        	float aircraftX = combatAircraft.getX();//战斗机X
            float aircraftY = combatAircraft.getY();//战斗机Y
            List<Bullet> aliveBullets = getAliveBullets();//得到存活子弹
            //得到在界面上存活的子弹
            for(Bullet bullet : aliveBullets){
            	//若是纵向子弹
            	if(bullet.getSpeed2() == 0){
            		//若子弹从飞机口发射
            		if(bullet.getUpOrientation()){
		                //如果战斗机跑到了子弹前面，那么就销毁子弹
		                if(aircraftY <= bullet.getY()){
		                    bullet.destroy();
		                }
            		}
            		//若自担从飞机尾发射
            		else{
            			//如果战斗机跑到子弹后面，那么就销毁子弹
            			if(aircraftY + combatAircraft.getHeight() >= bullet.getY() + bullet.getHeight()){
		                    bullet.destroy();
		                }
            		}
            	}
            	//若是横向子弹
            	else{
            		//若子弹从右边发射
            		if(bullet.getRightOrientation()){
		                //如果战斗机跑到了子弹右边，那么就销毁子弹
		                if(aircraftX + combatAircraft.getWidth() >= bullet.getX() + bullet.getWidth()){
		                    bullet.destroy();
		                }
            		}
            		//若子弹从左边发出
            		else{
            			//若果战斗机跑道子弹左边，那么就销毁子弹
            			if(aircraftX <= bullet.getX()){
		                    bullet.destroy();
		                }
            		}
            	}
            }
        }
    }
    
    //移除掉已经destroyed的Sprite
    private void removeDestroyedSprites(){
        Iterator<Sprite> iterator = sprites.iterator();
        while (iterator.hasNext()){
            Sprite s = iterator.next();
            //所有除开战斗机以外所有的精灵中只要有销毁的就从集合中移除它
            if(s.isDestroyed()){
                iterator.remove();
            }
        }
    }
    
    //生成随机的Sprite，并会把它初始化加入到下帧将要被添加到精灵集合的集合中
    private void createRandomSprites(int canvasWidth){
        Sprite sprite = null;
        int speed = 2;//用于小中大敌机下落速度
        int speed2 = 1;//用于老板敌机下落速度,和左右移动的速度
        int speed3 = 3;//用于奖励类下降速度
        //根据不同难度模式选择不同下落速度
        if(degree == EASY_MODEL)//简单
        	speed = 2;
        else if(degree == NORMAL_MODEL)//一般
        	speed = 4;
        else if(degree == ELITE_MODEL)//困难
        	speed = 8;
        //callTime表示此方法被调用的次数
        int callTime = Math.round(frame / 30);//被调用的次数
        //每隔30*50帧后出现一个Boss敌机
        if((callTime + 1) % 50 == 0){
        	boolean flag = false;//是否有Boss敌机的标志位，默认在30*50帧没有Boss敌机
        	for(Sprite s : sprites)
        		if(!s.isDestroyed() && s instanceof BossEnemyPlane){
        			flag = true;
        		}
        	//若又过了30*50帧，之前的Boss敌机还没被杀死则不会产生新的Boss敌机
        	if(flag == false){
        		sprite = new BossEnemyPlane(bitmaps.get(12));//老板敌机
        	}       	
        }
        else{
	        //每隔30*20帧产生一个奖励类精灵
	        if((callTime + 1) % 20 == 0){
	        	//随机数选取来随机选取奖励降落
	        	int[] nums = {0,1,2,3,4};
	        	int index = (int)Math.floor(nums.length*Math.random());
	        	int type = nums[index];
	        	if(type == 0){
	        		if(Math.random() < 0.5)
	        			sprite = new BombAward(bitmaps.get(7));//炸弹奖励
	        	}
	        	else if(type == 1){
	        		sprite = new BulletAward(bitmaps.get(8));//前发双子单奖励
	        	}
	        	else if(type == 2){
	        		sprite = new SideBulletAward(bitmaps.get(14));//侧边子弹奖励
	        	}
	        	else if(type == 3){
	        		sprite = new TailBulletAward(bitmaps.get(15));//尾发子弹奖励
	        	}
	        	else if(type == 4){
	        		if(Math.random() < 0.5)
	        			sprite = new LifeAward(bitmaps.get(17));//生命值奖励
	        	}
	        }
	        //每隔30帧但是排除每隔40*20帧会产生一个敌机类精灵
	        else{
	            //随机数选取来随机选取降落的敌机
	            int[] nums = {0,1,2,0,0,1,2,0,1,2,0,1,2,0,1,0,1,0,0,0};
	            int index = (int)Math.floor(nums.length*Math.random());
	            int type = nums[index];
	            if(type == 0){
	                sprite = new SmallEnemyPlane(bitmaps.get(4));//小敌机
	            }
	            else if(type == 1){
	                sprite = new MiddleEnemyPlane(bitmaps.get(5));//中敌机
	            }
	            else if(type == 2){
	                sprite = new BigEnemyPlane(bitmaps.get(6));//大敌机
	            }
	            //要是选到了小敌机就会有1/3的可能性会使小敌机速度变为原来两倍
	            if(type == 0){
	                if(Math.random() < 0.33){
	                    speed *= 2;
	                }
	            }
	        }
        }
        //下面任务是初始化这个被随机选取的精灵类，然后加到暂存的精灵集合中
        if(sprite != null){
            float spriteWidth = sprite.getWidth();//精灵宽度
            float spriteHeight = sprite.getHeight();//精灵高度
            float x = 0;//精灵X
            float y = -spriteHeight;//精灵Y
            //若该精灵属于老板敌机精灵（因为老板敌机和其他精灵在初始位置上不同，要区分对待）
            if(sprite instanceof BossEnemyPlane){
            	x = canvasWidth / 2 - spriteWidth / 2;//老板敌机X
            	y = -spriteHeight;//老板敌机Y
            }
            //若这个精灵不属于老板敌机精灵
            else{
	            x = (float)((canvasWidth - spriteWidth)*Math.random());//此精灵X
	            y = -spriteHeight;//此精灵Y
            }
            //初始化这个精灵的(x,y)
            sprite.setX(x);
            sprite.setY(y);
            //初始化精灵速度并加入到暂存精灵集合中去（不同的精灵下落速度不一样）
            if(sprite instanceof AutoSprite){
            	AutoSprite autoSprite = (AutoSprite)sprite;
            	//若此精灵属于敌机类别
            	if(sprite instanceof EnemyPlane){
            		//若此精灵属于老板敌机类别
            		if(sprite instanceof BossEnemyPlane){
            			BossEnemyPlane bossEnemyPlane = (BossEnemyPlane)sprite;
            			autoSprite.setSpeed(speed2);//设置老板敌机的降落速度
            			bossEnemyPlane.setSpeed2(speed2);//设置老板敌机的横向速度，初始是向右移动的
            		}
            		//若此精灵不属于老板敌机类别
            		else
            			autoSprite.setSpeed(speed);//设置此敌机精灵的降落速度
            	}
            	//若此精灵不属于敌机类别
            	else
            		autoSprite.setSpeed(speed3);//设置此奖励精灵的降落速度
            }
            //将这个精灵加入到暂存精灵集合中
            addSprite(sprite);
        }
    }
    
    /*------------------------------ drawGamePaused()游戏暂停态内的主要方法 ------------------------------*/
    
    //绘制对话框，对话框在暂停和结束时候出现
    private void drawScoreDialog(Canvas canvas, String operation){
        int canvasWidth = canvas.getWidth();//画布宽
        int canvasHeight = canvas.getHeight();//画布高
        //存储原始值
        float originalFontSize = textPaint.getTextSize();
        Paint.Align originalFontAlign = textPaint.getTextAlign();
        int originalColor = paint.getColor();
        Paint.Style originalStyle = paint.getStyle();
        /*
        W = 360
        w1 = 20
        w2 = 320
        buttonWidth = 140
        buttonHeight = 42
        H = 558
        h1 = 150
        h2 = 60
        h3 = 124
        h4 = 76
        */
        //设置相关边距参数
        int w1 = (int)(20.0 / 360.0 * canvasWidth);
        int w2 = canvasWidth - 2 * w1;
        int buttonWidth = (int)(140.0 / 360.0 * canvasWidth);

        int h1 = (int)(150.0 / 558.0 * canvasHeight);
        int h2 = (int)(60.0 / 558.0 * canvasHeight);
        int h3 = (int)(124.0 / 558.0 * canvasHeight);
        int h4 = (int)(76.0 / 558.0 * canvasHeight);
        int buttonHeight = (int)(42.0 / 558.0 * canvasHeight);
        //变化原点坐标
        canvas.translate(w1, h1);
        //绘制背景色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFEEEE00);
        Rect rect1 = new Rect(0, 0, w2, canvasHeight - 2 * h1);
        canvas.drawRect(rect1, paint);
        //绘制边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFFD2691E);
        paint.setStrokeWidth(borderSize);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawRect(rect1, paint);
        //绘制文本"飞机游戏分数"
        textPaint.setTextSize(fontSize2);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("飞机游戏分数", w2 / 2, (h2 - fontSize2) / 2 + fontSize2, textPaint);
        //绘制"飞机游戏分数"下面的横线
        canvas.translate(0, h2);
        canvas.drawLine(0, 0, w2, 0, paint);
        //绘制实际的分数
        String allScore = String.valueOf(getScore());
        canvas.drawText(allScore, w2 / 2, (h3 - fontSize2) / 2 + fontSize2, textPaint);
        //绘制分数下面的横线
        canvas.translate(0, h3);
        canvas.drawLine(0, 0, w2, 0, paint);
        //绘制"继续"或者"重新开始"按钮边框       
        Rect rect2 = new Rect();
        rect2.left = (w2 - 2 * buttonWidth) / 3;
        rect2.right = rect2.left + buttonWidth;
        rect2.top = (h4 - buttonHeight) / 2;
        rect2.bottom = h4 - rect2.top;
        canvas.drawRect(rect2, paint);
        //绘制"主界面"按钮边框
        Rect rect3 = new Rect();
        rect3.left = (w2 - 2 * buttonWidth) / 3 * 2 + buttonWidth;
        rect3.right = (w2 - 2 * buttonWidth) / 3 * 2 + 2 * buttonWidth;
        rect3.top = (h4 - buttonHeight) / 2;
        rect3.bottom = h4 - rect2.top;
        canvas.drawRect(rect3, paint);       
        //绘制文本"继续"或"重新开始"      
        canvas.translate(0, rect2.top);
        canvas.drawText(operation, (w2 - 2 * buttonWidth) / 3 + buttonWidth / 2, (buttonHeight - fontSize2) / 2 + fontSize2, textPaint);
        continueRect = new Rect(rect2);
        continueRect.left = w1 + rect2.left;
        continueRect.right = continueRect.left + buttonWidth;
        continueRect.top = h1 + h2 + h3 + rect2.top;
        continueRect.bottom = continueRect.top + buttonHeight;
        //绘制文本"主界面"
        canvas.drawText("主界面", (w2 - 2 * buttonWidth) / 3 * 2 + buttonWidth / 2 * 3, (buttonHeight - fontSize2) / 2 + fontSize2, textPaint);
        backRect = new Rect(rect3);
        backRect.left = w1 + rect3.left;
        backRect.right = backRect.left + buttonWidth;
        backRect.top = h1 + h2 + h3 + rect3.top;
        backRect.bottom = continueRect.top + buttonHeight;       
        //重置
        textPaint.setTextSize(originalFontSize);
        textPaint.setTextAlign(originalFontAlign);
        paint.setColor(originalColor);
        paint.setStyle(originalStyle);
    }
    
    /*------------------------------ drawGameDestroyed()游戏进结束态内的主要方法 ------------------------------*/
    
    //许多方法在上面已经写过
    
    
    /*------------------------------ 点击到某一按钮改变游戏状态的方法 ------------------------------*/
    
    //从头开始游戏
    private void restart(){
        destroyNotRecyleBitmaps();//销毁集合重元素和初始化数据
        startWhenReady();//执行第二个准备工作，不用执行第一个是因为游戏每次开始就限制性第一个准备工作，之后再也不用执行
    }
    
    //暂停游戏
    public void pause(){
        //将游戏设置为暂停状态
        status = STATUS_GAME_PAUSED;
    }

    //继续游戏
    private void resume(){
        //将游戏设置为运行状态
        status = STATUS_GAME_STARTED;
        //每次点击继续要初始化一下FPS帧数相关变量
        lastFPSFrame = frame;
        lastFPSTime = System.currentTimeMillis();
        fps = 0;
        postInvalidate();//重绘
    }
    
    //返回游戏主界面
    private void back(){
    	Intent intent = new Intent(activity, MainActivity.class);
    	activity.startActivity(intent);
    }

    /*------------------------------ destroy销毁相关方法 ------------------------------*/
    
    //销毁不再使用的位图和初始化数据，一般是游戏结束会去执行此方法，之后在执行第二个准备方法，最后进入重绘
    private void destroyNotRecyleBitmaps(){
        //将游戏设置为销毁状态
        status = STATUS_GAME_DESTROYED;
        //重置frame
        frame = 0;
        //重置得分
        score = 0;
        //重置FPS
        lastFPSFrame = 0;
        lastFPSTime = 0;
        fps = 0;
        //销毁战斗机
        if(combatAircraft != null){
            combatAircraft.destroy();
        }
        combatAircraft = null;
        //销毁敌机、子弹、奖励、爆炸、火球
        for(Sprite s : sprites){
            s.destroy();
        }
        //从集合中清除所有精灵
        sprites.clear();
    }

    //销毁掉一切，包括加再进来的图片资源，此方法一般在游戏最开始，也就是第一个准备方法中执行，为了先清理来初始化，之后不再执行
    public void destroy(){
    	//销毁与初始化
        destroyNotRecyleBitmaps();
        //释放Bitmap资源
        for(Bitmap bitmap : bitmaps){
            bitmap.recycle();
        }
        bitmaps.clear();
    }
    
    /*------------------------------ touch触屏相关方法 ------------------------------*/

    /*
     * onTouchEvent()方法和onDraw()都是被重写的方法，是用来监听触屏事件的方法，触屏时自动执行
     * 注意由于View框架中只支持触屏移动事件、触屏按下事件、触屏抬起事件这3种事件的识别
     * 所以对于单击事件和双击事件还要自己补充代码实现
     * 所以下面我的onTouchEvent()触屏事件分为两大步：1.将原有的事件类型转变成自己自定义的事件类型。2.通过不同的游戏状态和不同的自定义事件类型来执行不同的方法
     * 第一步：将原有事件类型转变为自定义事件类型，自定义有单击双击移动类型，抬起按下事件时间差小于200视作一个单击事件，两次单击事件时间差小于300视作双击事件
     * 触屏移动事件只有当抬起与按下的时间差大于200ms才能被视作触屏移动事件
     * 值得注意的是，两次单击事件的时间差指的是第二次点击抬起事件时间减去第一次点击抬起事件的时间只差，
     * 每次出现一个点击事件都会更新这个点击事件的触屏基本属性值，而这个触屏基本属性值在GameView的Field属性中已经标明了，
     * 只有当这个点击事件被断定为单击事件或者双击事件亦或者什么事件之后，lastSingleClickTime这个属性才会被重新置为-1，也就是说如果它>0时，就有一个未被确定的点击事件
     * 其实可以注意到，一个点击事件之后一段时间没再发生什么事件，那么必须要经过300ms才能判断300ms前的发生的这个事件到底是什么事件，因为不能确定它和以后的一个事件是不是可以组成一个双击事件
     * 第二步通过不同的游戏状态和不同的自定义事件类型执行不同方法，主要是改变战斗机位置的方法，这一步类似于AutoSprite类中的beforeDraw()方法，主要是不断改变(x,y)
     * 当然还有唯一的双击事件来响应界面所有敌机爆炸的方法，下面来看代码。
     */
    
    //重写触屏方法
    @Override
    public boolean onTouchEvent(MotionEvent event){
        //通过调用resolveTouchType方法，得到我们想要的事件类型
        //需要注意的是resolveTouchType方法不会返回TOUCH_SINGLE_CLICK类型
        //我们会在onDraw方法每次执行的时候，都会调用isSingleClick方法检测是否触发了单击事件
        int touchType = resolveTouchType(event);
        //若现在是游戏进行态
        if(status == STATUS_GAME_STARTED){
        	//若自定义触屏事件是触屏移动
            if(touchType == TOUCH_MOVE){
                if(combatAircraft != null){
                	//改变战斗机的位置，类似AutoSprite中的beforeDraw()方法
                    combatAircraft.centerTo(touchX, touchY);
                }
            }
            //若自定义触屏事件为双击
            else if(touchType == TOUCH_DOUBLE_CLICK){
            	//现在的状态是游戏进行态
                if(status == STATUS_GAME_STARTED){
                    if(combatAircraft != null){
                        //双击会使得战斗机使用炸弹
                        combatAircraft.bomb(this);
                    }
                }
            }
        }
        //若现在是游戏暂停态
        else if(status == STATUS_GAME_PAUSED){
        	//若最近的上一次有一次点击事件没被断定则重绘
            if(lastSingleClickTime > 0){
                postInvalidate();
            }
        }
        //若现在是游戏结束态
        else if(status == STATUS_GAME_OVER){
        	//若最近的上一次又一次点击事件没被断定则重绘
            if(lastSingleClickTime > 0){
                postInvalidate();
            }
        }
        return true;
    }

    //合成自定义的事件类型
    private int resolveTouchType(MotionEvent event){
        int touchType = -1;//要返回的自定义类型值
        int action = event.getAction();//得到原始事件动作
        touchX = event.getX();//事件X
        touchY = event.getY();//事件Y
        //若发生触屏移动原始事件
        if(action == MotionEvent.ACTION_MOVE){
            long deltaTime = System.currentTimeMillis() - touchDownTime;
            //只有当抬起和按下时间差大于200ms才被认定为自定义触屏事件
            if(deltaTime > singleClickDurationTime){
                //自定义触屏移动事件
                touchType = TOUCH_MOVE;
            }
        }
        //若发生按下原始事件
        else if(action == MotionEvent.ACTION_DOWN){
            touchDownTime = System.currentTimeMillis();//记录下按下时间
        }
        //若发生抬起
        else if(action == MotionEvent.ACTION_UP){
            touchUpTime = System.currentTimeMillis();//记录下抬起时间
            long downUpDurationTime = touchUpTime - touchDownTime;//抬起按下的时间差
            //若抬起按下时间差小于200ms则表示发生点击事件
            if(downUpDurationTime <= singleClickDurationTime){
                long twoClickDurationTime = touchUpTime - lastSingleClickTime;//计算这次单击距离上次单击的时间差
                //若两次点击事件时间差小于300ms则表示发生双击事件
                if(twoClickDurationTime <=  doubleClickDurationTime){
                	//自定义双击事件
                    touchType = TOUCH_DOUBLE_CLICK;
                    //由于已经确定最近的上一次点击事件的类型，可以重置变量
                    lastSingleClickTime = -1;
                    touchDownTime = -1;
                    touchUpTime = -1;
                }
                //若两次点击事件时间差大于300ms，则之前那次的点击一定是单击，最近的上一次点击还不能断定是不是单击，还要等300ms才能断定
                else{
                	//由于不能断定最近的上一次点击事件的类型，所以要保存lastSingleClickTime的值，只有当lastSingleClickTime值为-1初始状态时候才表示最近上一次点击事件已被断定
                    lastSingleClickTime = touchUpTime;
                }
            }
        }
        //返回自定义事件类型
        return touchType;
    }

    //在onDraw方法中调用该方法，在每一帧绘制之前都检查是不是发生了单击事件
    private boolean isSingleClick(){
    	//初始默认没发生单击事件
        boolean singleClick = false;
        //若上一次有点击事件还没被断定
        if(lastSingleClickTime > 0){           
            long deltaTime = System.currentTimeMillis() - lastSingleClickTime;//计算当前时刻距离上次发生单击事件的时间差
            //若时间差大于300ms，则最近上一次的点击事件一定是个单击事件
            if(deltaTime >= doubleClickDurationTime){
                singleClick = true;//表示发生了单击事件
                //由于最近上一次点击事件已经被断定，可以重置变量
                lastSingleClickTime = -1;
                touchDownTime = -1;
                touchUpTime = -1;
            }
        }
        //返回是否发生单击事件
        return singleClick;
    }

    //单击事件执行，看是否点击不同游戏状态下的按钮，来改变游戏状态
    private void onSingleClick(float x, float y){
    	//若现在是游戏进行态
        if(status == STATUS_GAME_STARTED){
        	//看是否点击了暂停
            if(isClickPause(x, y)){
            	//暂停游戏
                pause();
            }
        }
        //若现在是游戏暂停态
        else if(status == STATUS_GAME_PAUSED){
        	//看是否点击了继续游戏
            if(isClickContinueButton(x, y) || isClickPause(x, y)){
                //继续游戏
                resume();
            }
            //看是否点击了返回主界面
            else if(isClickBackButton(x,y)){
            	//返回主界面
            	back();
            }
        }
        //若现在是游戏结束态
        else if(status == STATUS_GAME_OVER){
        	//看是否点击了重新开始
            if(isClickRestartButton(x, y)){
                //重新开始
                restart();
            }
            //看是否点击了返回主界面
            else if(isClickBackButton(x,y)){
            	//返回主界面
            	back();
            }
        }
    }
    
    //是否点在了战斗机这个图形中
    private boolean isClickCombatAircraft(float x, float y){
    	RectF combatAircraftRectF = getCombatAircraftDstRecF();
    	return combatAircraftRectF.contains(x, y);//Rect自带判断点是否在矩形内的方法
    }

    //是否单击了左上角的暂停按钮
    private boolean isClickPause(float x, float y){
        RectF pauseRecF = getPauseBitmapDstRecF();
        return pauseRecF.contains(x, y);
    }

    //是否单击了暂停状态下的“继续”按钮
    private boolean isClickContinueButton(float x, float y){
        return continueRect.contains((int)x, (int)y);
    }

    //是否单击了结束状态下的“重新开始”按钮
    private boolean isClickRestartButton(float x, float y){
        return continueRect.contains((int)x, (int)y);
    }

    //是否点击了“主界面”按钮
    private boolean isClickBackButton(float x, float y){
    	return backRect.contains((int)x, (int)y);
    }
    
    //得到战斗机的Rect
    private RectF getCombatAircraftDstRecF(){
    	Bitmap combatAircraftBitmap = bitmaps.get(0);
    	RectF recF = new RectF();
    	recF.left = combatAircraft.getX();//左
    	recF.top = combatAircraft.getY();//上
    	recF.right = recF.left + combatAircraftBitmap.getWidth();//右
    	recF.bottom = recF.top + combatAircraftBitmap.getHeight();//下
    	return recF;
    }
    
    //得到暂停位图的Rect并已经做好初始化了
    private RectF getPauseBitmapDstRecF(){
        Bitmap pauseBitmap = status == STATUS_GAME_STARTED ? bitmaps.get(9) : bitmaps.get(10);
        RectF recF = new RectF();
        recF.left = 15 * density;//左
        recF.top = 15 * density;//上
        recF.right = recF.left + pauseBitmap.getWidth();//右
        recF.bottom = recF.top + pauseBitmap.getHeight();//下
        return recF;
    }

    /*-------------------------------public 基本方法-----------------------------------*/

    //向Sprites尾部中添加Sprite
    public void addSprite(Sprite sprite){
        spritesLaterAdded.add(sprite);
    }
    
    //添加得分
    public void addScore(int value){
        score += value;
    }
    
    //获取得分
    private long getScore(){
        return score;
    }

    //得到游戏状态
    public int getStatus(){
        return status;
    }
    
    //得到密度
    public float getDensity(){
        return density;
    }
    
    //得到FPS
    public int getFPS(){
    	long nowTime = System.currentTimeMillis();
    	//若当前和上一次计算成功FPS时间差大于1000ms时
    	if(nowTime - lastFPSTime >= 1000){
    		//计算(int)FPS
    		float secs = (nowTime - lastFPSTime) / 1000f;//时间差是多少秒
    		fps = Math.round((frame - lastFPSFrame) / secs);//(int)FPS为每秒多少帧
    		//更新上次的FPS时间和帧数
    		lastFPSFrame = frame;
    		lastFPSTime = nowTime;
    	}
    	return fps;
    }

    /*-------------------------------public Bitmap相关方法-----------------------------------*/
    
    //得到黄色子弹位图
    public Bitmap getYellowBulletBitmap(){
        return bitmaps.get(2);
    }

    //得到蓝色子弹位图
    public Bitmap getBlueBulletBitmap(){
        return bitmaps.get(3);
    }

    //得到火球位图
    public Bitmap getFireBallBitmap(){
    	return bitmaps.get(13);
    }
    
    //得到爆炸位图
    public Bitmap getExplosionBitmap(){
        return bitmaps.get(1);
    }
    
    //得到火焰位图
    public Bitmap getFireBitmap(){
    	return bitmaps.get(20);
    }
    
    //得到原始combatAircraft
    public Bitmap getCombatAircraftBitmap(){
    	return bitmaps.get(0);
    }
    
    //得到黑色combatAircraft2
    public Bitmap getCombatAircraft2Bitmap(){
    	return bitmaps.get(18);
    }
    
    //得到绿色combatAircraft3
    public Bitmap getComabtAircraft3Bitmap(){
    	return bitmaps.get(19);
    }
    
    /*-------------------------------public List相关方法-----------------------------------*/

    //获取处于活动状态的敌机
    public List<EnemyPlane> getAliveEnemyPlanes(){
        List<EnemyPlane> enemyPlanes = new ArrayList<EnemyPlane>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof EnemyPlane){
                EnemyPlane sprite = (EnemyPlane)s;
                enemyPlanes.add(sprite);
            }
        }
        return enemyPlanes;
    }

    //获得处于活动状态的炸弹奖励
    public List<BombAward> getAliveBombAwards(){
        List<BombAward> bombAwards = new ArrayList<BombAward>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof BombAward){
                BombAward bombAward = (BombAward)s;
                bombAwards.add(bombAward);
            }
        }
        return bombAwards;
    }

    //获取处于活动状态的双发子弹奖励
    public List<BulletAward> getAliveBulletAwards(){
        List<BulletAward> bulletAwards = new ArrayList<BulletAward>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof BulletAward){
                BulletAward bulletAward = (BulletAward)s;
                bulletAwards.add(bulletAward);
            }
        }
        return bulletAwards;
    }
    
    //获取处于活动状态的侧边子弹奖励
    public List<SideBulletAward> getAliveSideBulletAwards(){
        List<SideBulletAward> sideBulletAwards = new ArrayList<SideBulletAward>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof SideBulletAward){
                SideBulletAward sideBulletAward = (SideBulletAward)s;
                sideBulletAwards.add(sideBulletAward);
            }
        }
        return sideBulletAwards;
    }
    
    //获取处于活动状态的尾部子弹奖励
    public List<TailBulletAward> getAliveTailBulletAwards(){
        List<TailBulletAward> tailBulletAwards = new ArrayList<TailBulletAward>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof TailBulletAward){
                TailBulletAward tailBulletAward = (TailBulletAward)s;
                tailBulletAwards.add(tailBulletAward);
            }
        }
        return tailBulletAwards;
    }

    //获取处于活动状态的生命值奖励
    public List<LifeAward> getAliveLifeAwards(){
        List<LifeAward> lifeAwards = new ArrayList<LifeAward>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof LifeAward){
                LifeAward lifeAward = (LifeAward)s;
                lifeAwards.add(lifeAward);
            }
        }
        return lifeAwards;
    }
    
    //获取处于活动状态的子弹
    public List<Bullet> getAliveBullets(){
        List<Bullet> bullets = new ArrayList<Bullet>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof Bullet){
                Bullet bullet = (Bullet)s;
                bullets.add(bullet);
            }
        }
        return bullets;
    }
    
    //获取处于活动状态的火球
    public List<FireBall> getAliveFireBalls(){
        List<FireBall> fireBalls = new ArrayList<FireBall>();
        for(Sprite s : sprites){
            if(!s.isDestroyed() && s instanceof FireBall){
                FireBall fireBall = (FireBall)s;
                fireBalls.add(fireBall);
            }
        }
        return fireBalls;
    }
    
    //获取处于活动状态的战斗机
    public CombatAircraft getCombatAircraft(){    	
    	return combatAircraft;
    }
}