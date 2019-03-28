package game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.List;

/**
 * 战斗机类，可以通过交互改变位置
 */
public class CombatAircraft extends Sprite {
	//战斗机基本属性
    private boolean collide = false;//标识战斗机是否被击中
    private int bombAwardCount = 0;//可使用的炸弹数
    private int life = 3;//飞机生命值为3条命
    private Explosion leftFireExplosion;//战斗机左边喷射效果
    private Explosion rightFireExplosion;//战斗机右边喷射效果

    private boolean single = true;//标识是否发的是单一的子弹
    //双发子弹相关
    private boolean doubleShoot = ! single;//标识是否发的是单一的子弹
    private int doubleBulletTime = 0;//当前已经用双子弹绘制的次数
    private int maxDoubleBulletTime = 140;//使用双子弹最多绘制的次数
    
    //侧边子弹相关
    private boolean sideShoot = false;//标识是否发的是旁边发射的子弹
    private int sideBulletTime = 0;//当前已经用旁边子弹绘制的次数
    private int maxSideBulletTime = 140;//使用旁边子弹最多绘制的次数
    
    //尾部子弹相关
    private boolean tailShoot = false;//标识是否发的是尾部的子弹
    private int tailBulletTime = 0;//当前已经用尾部子弹绘制的次数
    private int maxTailBulletTime = 140;//使用尾部子弹最多绘制的次数
    
    //战斗机被碰撞后闪烁相关
    private long beginFlushFrame = 0;//被撞时的帧数
    private int flushDurationFrame = 30;//闪烁持续帧数多少帧

    //战斗机最后一次被碰撞后延迟时间变为结束态相关
    private boolean lastCollidedFlag = false;//是否是最后一次别撞
    private long frameWhenLastCollide = 0;//最后一次被撞是在多少帧

    //构造方法
    public CombatAircraft(Bitmap bitmap){
        super(bitmap);
        
    }

    /*
     * 这个beforeDraw()重写了Sprite的同名方法，其实Sprite中的beforeDraw()是空的，这算第一次绘制beforeDraw()
     * 战斗机的beforeDraw()中主要确保战斗机在整个画布中和战斗机每个几帧会发射一个子弹，这个子弹视情况而选择不同子弹类型
     * 其实这个发射子弹的方法fight()就是将某一类型子弹加入到暂存集合中便于下一次绘制，子弹有重写beforeDraw()方法实现不断改变位置
     */
    
    //beforeDraw()
    @Override
    protected void beforeDraw(Canvas canvas, Paint paint, GameView gameView) {
        if(!isDestroyed()){
            //确保战斗机完全位于Canvas范围内
            validatePosition(canvas);
            //在第1帧时战斗机就加入火焰
            	fire(gameView);
            //每隔5帧发射一粒子弹
            if(getFrame() % 5 == 0){
                fight(gameView);
            }
        }
    }

    /*
     * 这个afterDraw()重写了Sprite中的的同名方法，其实Sprite中的afterDraw()方法是空的，这算是第一次绘制afterDraw()
     * 在战斗机的afterDraw()中主要是是否要被敌机碰撞，是否要被敌机的火球碰撞，是否接到了奖励道具三大块。
     * 战斗机的afterDraw()主要是在下一帧绘出
     */
    
    //afterDraw()
    @Override
    protected void afterDraw(Canvas canvas, Paint paint, GameView gameView){
        if(isDestroyed()){
            return;
        }
        //当战斗机被撞时,要经历28帧也就是一个爆炸图完全绘制完毕的时间，才能被设置为销毁态，因为GameView中是通过战斗机是否处于销毁态来决定是否结束游戏的
        if(lastCollidedFlag == true){
        	if(getFrame() - frameWhenLastCollide >= 28)
        		destroy();
        }     
        //在飞机当前还没有被敌机击中时，要判断下一帧绘制时是否将要被敌机击中
        if(!collide){
            List<EnemyPlane> enemies = gameView.getAliveEnemyPlanes();
            //检测敌机碰撞
            for(EnemyPlane enemyPlane : enemies){
                Point p = getCollidePointWithOther(enemyPlane);//得到碰撞交点
                //若有碰撞
                if(p != null){
                	(this.life)--;//生命值减少1
                	this.beginFlushFrame = getFrame();//得到被撞时帧数
                	setBitmap(gameView.getCombatAircraft2Bitmap());//改变飞机位图
                	//敌方单位碰撞销毁
                	crash(gameView, enemyPlane);
                	if(this.life <= 0){
	                    //执行战斗机和敌机爆炸效果，主要是将爆炸位图加入到集合中
	                    explode(gameView);
	                    break;
                	}
                }
            }
            //红色闪烁
            if(beginFlushFrame > 0){//开始闪烁的标志            	
            	if(getFrame() - beginFlushFrame > flushDurationFrame){
            		//重置
            		beginFlushFrame = 0;//重置开始闪烁帧数
            		setBitmap(gameView.getCombatAircraftBitmap());//重置战斗机位图
            		
            	}           	
            }
        }       
        //在飞机当前还没有被敌机子弹击中时，要判断是否将要被敌机火球击中
        if(!collide){
        	List<FireBall> fireBalls = gameView.getAliveFireBalls();
        	//检测火球碰撞
            for(FireBall fireBall : fireBalls){
                Point p = getCollidePointWithOther(fireBall);//得到碰撞点
                //若有碰撞
                if(p != null){
                	(this.life)--;//生命值减少1
                	this.beginFlushFrame = getFrame();//得到被撞时帧数
                	setBitmap(gameView.getCombatAircraft2Bitmap());//改变飞机位图
                	//敌方单位碰撞销毁
                	crash(gameView, fireBall);
                	if(this.life <= 0){
	                    fireBall.destroy();//销毁火球
	                    explode(gameView);//将战斗机爆炸位图加入到集合中
	                    break;
                	}
                }
            }
            //红色闪烁
            if(beginFlushFrame > 0){//开始闪烁的标志
            	if(getFrame() - beginFlushFrame > flushDurationFrame){
            		//重置
            		beginFlushFrame = 0;//重置开始闪烁帧数
            		setBitmap(gameView.getCombatAircraftBitmap());//重置战斗机位图
            	}           	
            }
        }      
        //在没有被击中的情况下检查是否获得了道具
        if(!collide){
            //检查是否获得炸弹道具
            List<BombAward> bombAwards = gameView.getAliveBombAwards();
            for(BombAward bombAward : bombAwards){
                Point p = getCollidePointWithOther(bombAward);
                //若得到炸弹奖励道具
                if(p != null){                   	
                    bombAwardCount++;//炸弹数量加1
                    bombAward.destroy();//炸弹奖励销毁
                }
            }
            //检查是否获得双发子弹道具
            List<BulletAward> bulletAwards = gameView.getAliveBulletAwards();
            for(BulletAward bulletAward : bulletAwards){
                Point p = getCollidePointWithOther(bulletAward);
                //若得到双发子弹奖励道具
                if(p != null){               	
                    bulletAward.destroy();//双发子弹奖励销毁
                    single = false; //设为双发模式
                    doubleBulletTime = 0;//更新双发模式已用次数
                }
            }
            //检查是否获得旁侧子弹道具
            List<SideBulletAward> sideBulletAwards = gameView.getAliveSideBulletAwards();
            for(SideBulletAward sideBulletAward : sideBulletAwards){
                Point p = getCollidePointWithOther(sideBulletAward);
                //若得到旁侧子弹奖励道具
                if(p != null){               	
                    sideBulletAward.destroy();//旁侧子弹奖励销毁
                    sideShoot = true; //设为旁侧发射模式
                    sideBulletTime = 0;//更新旁侧发射模式已用次数
                }
            }          
            //检查是否获得尾部子弹道具
            List<TailBulletAward> tailBulletAwards = gameView.getAliveTailBulletAwards();
            for(TailBulletAward tailBulletAward : tailBulletAwards){
                Point p = getCollidePointWithOther(tailBulletAward);
                //若得到尾部子弹奖励道具
                if(p != null){               	
                    tailBulletAward.destroy();//尾部子弹奖励销毁
                    tailShoot = true; //设为尾部子弹发射模式
                    tailBulletTime = 0;//更新旁侧发射模式已用次数
                }
            }
            //检查是否获得生命值道具
            List<LifeAward> lifeAwards = gameView.getAliveLifeAwards();
            for(LifeAward lifeAward : lifeAwards){
                Point p = getCollidePointWithOther(lifeAward);
                //若得到生命值奖励道具
                if(p != null){
                	this.beginFlushFrame = getFrame();//得到被撞时帧数
                	setBitmap(gameView.getComabtAircraft3Bitmap());//改变飞机位图
                    lifeAward.destroy();//尾部子弹奖励销毁
                    if(this.life < 3)
                    	this.life++;
                }
            }
            //绿色闪烁
            if(beginFlushFrame > 0){//开始闪烁的标志            	
            	if(getFrame() - beginFlushFrame > flushDurationFrame){
            		//重置
            		beginFlushFrame = 0;//重置开始闪烁帧数
            		setBitmap(gameView.getCombatAircraftBitmap());//重置战斗机位图
            		
            	}           	
            }
        }
    }
    
    //确保战斗机完全位于Canvas范围内，若不在强行移到画布中
    private void validatePosition(Canvas canvas){
    	//超过左边
        if(getX() < 0){
            setX(0);
        }
        //超过上边
        if(getY() < 0){
            setY(0);
        }
        RectF rectF = getRectF();
        int canvasWidth = canvas.getWidth();
        //超过右边
        if(rectF.right > canvasWidth){
            setX(canvasWidth - getWidth());
        }
        int canvasHeight = canvas.getHeight();
        //超过下边
        if(rectF.bottom > canvasHeight){
            setY(canvasHeight - getHeight());
        }
    }

    //战斗机火焰添加到精灵组中
    private void fire(GameView gameView){
    	//如果战斗机被撞击了或销毁了，那么不会发射火焰
        if(collide || isDestroyed()){
            return;
        }
        //左右火焰中心坐标
        float leftCenterX = getX() + getWidth() / 4;
        float leftCenterY = getY() + getHeight() + gameView.getFireBitmap().getHeight() / 2 - 1;
        float rightCenterX = getX() + getWidth() / 4 * 3;
        float rightCenterY = leftCenterY;       
        //最开始绘制战斗机时将左右喷射效果都加入到精灵集合中，便于下次绘制
        if(getFrame() == 1){
        	Bitmap fireBitmap = gameView.getFireBitmap();//得到火焰位图
        	leftFireExplosion = new Explosion(fireBitmap);//左侧火焰
        	rightFireExplosion = new Explosion(fireBitmap);//右侧火焰         	
        	//加入到精灵集合中
        	gameView.addSprite(leftFireExplosion);
        	gameView.addSprite(rightFireExplosion);
        }
        //每次循环绘制时，若左右火焰爆炸效果存在（注意他俩是唯一的）则改变他俩的坐标
        if(leftFireExplosion != null && rightFireExplosion != null){
	        //改变左右火焰坐标值
	        leftFireExplosion.centerTo(leftCenterX, leftCenterY);//初始化leftFire(x,y)
	    	rightFireExplosion.centerTo(rightCenterX, rightCenterY);//初始化rightFire(x,y)
        }
    }
    
    //发射子弹
    public void fight(GameView gameView){
        //如果战斗机被撞击了或销毁了，那么不会发射子弹
        if(collide || isDestroyed()){
            return;
        }
        //初始子弹(x,y)
        float x = getX() + getWidth() / 2;
        float y = getY() - 5;       
        //先判断战斗机前方是单双发哪种模式
        //若是头部单发模式
        if(single){
          //单发模式下发射单发黄色子弹
          Bitmap yellowBulletBitmap = gameView.getYellowBulletBitmap();
          Bullet yellowBullet = new Bullet(yellowBulletBitmap);
          yellowBullet.moveTo(x, y);//初始化(x,y)
          yellowBullet.setSpeedAndUpOrientation(-10, true);//初始化速度
          gameView.addSprite(yellowBullet);//加入集合                      
        }
        //若是头部双发模式
        else{
          //双发模式下发射两发蓝色子弹
          float offset = getWidth() / 4;
          float leftX = x - offset;
          float rightX = x + offset;         
          Bitmap blueBulletBitmap = gameView.getBlueBulletBitmap();
          //双发子弹的左子弹
          Bullet leftBlueBullet = new Bullet(blueBulletBitmap);
          leftBlueBullet.moveTo(leftX, y);//初始化(x,y)
          leftBlueBullet.setSpeedAndUpOrientation(-10, true);//初始化速度
          gameView.addSprite(leftBlueBullet);//加入集合
          //双发子弹的右子弹
          Bullet rightBlueBullet = new Bullet(blueBulletBitmap);
          rightBlueBullet.moveTo(rightX, y);//初始化(x,y)
          rightBlueBullet.setSpeedAndUpOrientation(-10, true);//初始化速度
          gameView.addSprite(rightBlueBullet);//加入集合
          //判断双发子弹是否用完
          doubleBulletTime++;
          //若双发子弹用完
          if(doubleBulletTime >= maxDoubleBulletTime){
              single = true; //双发模式结束
              doubleBulletTime = 0;//双发次数回原
          }
        }       
        //若是侧边发射模式
        if(sideShoot){
        	//左侧子弹坐标
        	float leftX = getX() - 5;
        	float leftY = getY() + getHeight() / 2;
        	//右侧子弹坐标
        	float rightX = getX() + getWidth() + 5;
        	float rightY = getY() + getWidth() / 2;       	
        	Bitmap blueBulletBitmap = gameView.getBlueBulletBitmap();
        	//旋转子弹的位图，用于侧边发射子弹图形绘制
        	//左子弹旋转的位图 blueBulletBitmap1
        	Matrix matrix = new Matrix();
        	matrix.setRotate(90);
        	Bitmap blueBulletBitmap1 = Bitmap.createBitmap(blueBulletBitmap, 0, 0, 
        			blueBulletBitmap.getWidth(), blueBulletBitmap.getHeight(), matrix, false);
        	//右子弹旋转的位图 blueBulletBitmap2
        	matrix.reset();
        	matrix.setRotate(-90);
        	Bitmap blueBulletBitmap2 = Bitmap.createBitmap(blueBulletBitmap, 0, 0, 
        			blueBulletBitmap.getWidth(), blueBulletBitmap.getHeight(), matrix, false);
        	//左侧射出子弹
            Bullet leftBlueBullet = new Bullet(blueBulletBitmap1);
            leftBlueBullet.moveTo(leftX, leftY);//初始化(x,y)
            leftBlueBullet.setSpeed2AndRightOrientation(-10, false);//初始化速度
            gameView.addSprite(leftBlueBullet);//加入集合
            //右侧射出子弹
            Bullet rightBlueBullet = new Bullet(blueBulletBitmap2);
            rightBlueBullet.moveTo(rightX, rightY);//初始化(x,y)
            rightBlueBullet.setSpeed2AndRightOrientation(10, true);//初始化速度
            gameView.addSprite(rightBlueBullet);//加入集合
            //判断侧边子弹模式是否结束
            sideBulletTime++;
            //若侧边子弹用完
            if(sideBulletTime >= maxSideBulletTime){
                sideShoot = false; //此模式结束
                sideBulletTime = 0;//次数回原
            }
        }       
        //若是尾部发射模式
        if(tailShoot){
        	//尾部子弹坐标
        	float downX = x;
        	float downY = y + 5 + getHeight() + 5;
        	Bitmap blueBulletBitmap = gameView.getBlueBulletBitmap();
        	//尾部子弹发射子弹
            Bullet blueBullet = new Bullet(blueBulletBitmap);
            blueBullet.moveTo(downX, downY);//初始化(x,y)
            blueBullet.setSpeedAndUpOrientation(10, false);//初始化速度
            gameView.addSprite(blueBullet);//加入集合         
            //判断尾部子弹模式是否结束
            tailBulletTime++;
            //若尾部子弹用完
            if(tailBulletTime >= maxTailBulletTime){
                tailShoot = false; //此模式结束
                tailBulletTime = 0;//次数回原
            }
        }  
    }

    //战斗机和敌机爆炸方法
    private void explode(GameView gameView){
    	//由于是在afterDraw()检测到了碰撞，要在下一帧绘出
        if(!collide){
            collide = true;//置为碰撞
            setVisibility(false);//置为不可见      
            //销毁左右喷射效果
            leftFireExplosion.destroy();
            rightFireExplosion.destroy();
            //战斗机爆炸效果类加入到下次精灵绘制的集合中
            float centerX = getX() + getWidth() / 2;
            float centerY = getY() + getHeight() / 2;
            Explosion explosion = new Explosion(gameView.getExplosionBitmap());
            explosion.centerTo(centerX, centerY);//初始化(x,y)
            gameView.addSprite(explosion);//加入集合         
            //此帧中战斗机将要被碰撞，记录下当前帧，便于在28帧之后成功结束游戏
            lastCollidedFlag = true;
            frameWhenLastCollide = getFrame();            
        }
    }

    //敌机被撞的销毁方法
    private void crash(GameView gameView, EnemyPlane enemyPlane){
    	float centerX = enemyPlane.getX() + getWidth() / 2;
        float centerY = enemyPlane.getY() + getHeight() / 2;
        Explosion explosion = new Explosion(gameView.getExplosionBitmap());
        explosion.centerTo(centerX, centerY);//初始化(x,y)
        gameView.addSprite(explosion);//加入集合
        enemyPlane.destroy();//敌机销毁
    }
    
    //火球被撞销毁方法
    private void crash(GameView gameView, FireBall fireBall){
    	fireBall.destroy();//销毁火球
    }
    
    //获取可用的炸弹数量
    public int getBombCount(){
        return bombAwardCount;
    }

    //战斗机使用炸弹
    public void bomb(GameView gameView){
        if(collide || isDestroyed()){
            return;
        }
        //若有炸弹
        if(bombAwardCount > 0){
        	//敌方战斗机全部爆炸
            List<EnemyPlane> enemyPlanes = gameView.getAliveEnemyPlanes();
            for(EnemyPlane enemyPlane : enemyPlanes){
            	//界面精灵集合中的所有敌机全爆炸
                enemyPlane.explode(gameView);
            }
            //敌方发射的火球全部爆炸
            List<FireBall> fireBalls = gameView.getAliveFireBalls();
            for(FireBall fireBall : fireBalls){
            	//界面精灵集合中的所有火球全爆炸
            	fireBall.explode(gameView);
            }
            //炸弹数自减
            bombAwardCount--;
        }
    }

    //返回是否碰撞
    public boolean isCollide(){
        return collide;
    }

    //设置碰撞属性
    public void setNotCollide(){
        collide = false;
    }
    
    //得到战斗机的生命值
    public int getLife(){
    	return this.life; 
    }
}