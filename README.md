[TOC]

**[English](https://github.com/abcnull/IndependentPlaneGame/blob/master/README_en.md) | [博客](https://blog.csdn.net/abcnull/article/details/103453758)**
# IndependentPlaneGame

## 介绍

此项目完成比较久远，当初使用 Android Studio 完成。此项目完成的是一个飞机游戏，没有用到主流的有引擎，直接使用的是 android 自带的 graphics 图形包，使用了其中的 canvase 来进行图形界面的绘制。大体上通过继承关系，所有单位都会继承一个 sprite 精灵对象，然后还有许多其他小功能。

- 框架作者：**abcnull**
- csdn 博客：**https://blog.csdn.net/abcnull**
- github：**https://github.com/abcnull**
- e-mail：**abcnull@qq.com**

欢迎大家 **Watch**，**Star** 和 **Fork**！

## 关键代码

1. 碰撞检测：

    ```java
    //得到碰撞点
    public Point getCollidePoint(Sprite s){
        //碰撞点初始为0
        Point p = null;
        //得到第一个碰撞精灵位图的RectF类
        RectF rectF1 = getRectF();
        //得到第二个碰撞精灵位图的RectF类
        RectF rectF2 = s.getRectF();
        //新的RectF
        RectF rectF = new RectF();
        //通过setIntersect()方法得到两精灵是否相交的布尔值
        boolean isIntersect = rectF.setIntersect(rectF1, rectF2);
        //如果两精灵相交
        if(isIntersect){
            //得到交点
            p = new Point(Math.round(rectF.centerX()), Math.round(rectF.centerY()));
        }
        //返回交点
        return p;
    }
    ```

2. 添加音效：

    ```java
    //播放音乐 
    private void playMusic(){   
        MediaPlayer mediaPlayer;           
        try{                
            //创建音乐媒体对象
            mediaPlayer = MediaPlayer.create(this, R.raw.backmusic);
            //准备音乐媒体
            mediaPlayer.prepare();       
        }catch (IllegalStateException e){    
            e.printStackTrace();
        }catch (IOException e){ 
            e.printStackTrace();
        }
        //播放音乐媒体
        mediaPlayer.start();        
        //为mediaPlayer对象添加完成时间监听器，用于当音乐播放完毕后重新开始播放音乐
        mediaPlayer.setOnCompletionListener(
            new OnCompletionListener(){                
                public void onCompletion(MediaPlayer mp){   
                    playMusic();
                }
            }
        ); 
    }
    ```

3. 帧数显示：

    ```java
    //得到FPS
    public int getFPS(){
        //得到当前时刻
        long nowTime = System.currentTimeMillis();
        //若当前和上一次计算成功FPS的时候时间差大于1000ms时
        if(nowTime - lastTime >= 1000){
            //计算(int)FPS
            float secs = (nowTime - lastTime) / 1000f;//时间差(s)
            fps = Math.round((frame - lastFrame) / secs);//FPS帧率(int)四舍五入后
            //更新上次的FPS时间和帧数
            lastFrame = frame;//更新上一次帧数
            lastTime = nowTime;//更新上一次时间
        }
        return fps;
    }
    ```

4. 黑天白天渐变：

    ```java
    //得到此View下的资源
    Resource res = getResource();
    //图片渐变对象
    TransitionDrawable imageTransitionDrawable = null;
    imageTransitionDrawable = new TransitionDrawable(
        new Drawable[]{
            res.getDrawable(R.drawable.bg1),
            res.getDrawable(R.drawable.bg2)
        }
    );
    //设置背景图片为渐变图片
    this.setBackgroundDrawable(imageTransitionDrawable);
    //经过4000ms的图片渐变过程
    imageTransitionDrawable.startTransition(4000);
    ```

5. 敌机随机出现实现：

    ```java
    int[] nums = {0,1,2,3};
    /*Math.random()取0-1浮点数，
     *floor是向下取整，
     *index是数组下表，
     *type是数组中的元素
     */
    int index = (int)Math.floor(nums.length*Math.random());//随机数组下标
    int type = nums[index];//数组中随机数
    if(type == 0)
    {
        //执行方法1
    }
    else if(type == 1){
        //执行方法2
    }
    else if(type == 2){
        //执行方法3
    }
    else if(type == 3){
        //执行方法4             
    }
    ```

6. 实现单机双击和触屏移动：

    ```java
    /*
     * 在View开发框架下你要重写一下触屏事件onTouchEvent(MotionEvent event)，
     * 其中的MotionEvent event可以得到在View框架下原始的触屏动作,
     * 我们要做的是，把原始的触屏动作换为自定义触屏动作，
     * 然后根据不同自定义的触屏动作来执行不同的方法，
     * 这个转换为自定义触屏动作的过程可以封装成一个方法，之后在onTouchEvent()中调用即可
     */

    //重写触屏方法
    @Override
    public boolean onTouchEvent(MotionEvent even{
        //通过调用customTouchType方法，得到我们自定义的事件类型    
        int touchType = customTouchType(event);
        //若自定义触屏事件是触屏移动
        if(touchType == TOUCH_MOVE) {
            //当发生触屏移动事件，执行某方法
        }
        //若自定义触屏事件为双击事件    
        else if(touchType == TOUCH_DOUBLE_CLICK){
            //当发生双击事件，执行某方法
        }
    }

    //合成自定义的事件类型
    private int customTouchType(MotionEvent event){
        //要返回的自定义事件类型值      
        int touchType = -1;
        //得到原始触屏事件动作
        int action = event.getAction(); 
        //若发生触屏移动原始动作    
        if(action == MotionEvent.ACTION_MOVE){
            //得到当前时刻与上一次按下时刻时间差
            long deltaTime = System.currentTimeMillis() - touchDownTime;
            //只有当抬起和按下时间差大于200ms才被认定为自定义触屏事件
            if(deltaTime > singleClickDurationTime){
                //自定义触屏移动事件
                touchType = TOUCH_MOVE;
            }
        }
        //若发生按下原始事件
        else if(action == MotionEvent.ACTION_DOWN){
            //记录下按下时间
            touchDownTime = System.currentTimeMillis();
        }
        //若发生抬起原始事件
        else if(action == MotionEvent.ACTION_UP){
            //记录下抬起时间
            touchUpTime = System.currentTimeMillis();
            //抬起按下的时间差
            long downUpDurationTime = touchUpTime - touchDownTime;
            //若抬起按下时间差小于200ms则表示发生点击事件
            if(downUpDurationTime <= singleClickDurationTime){
                //计算这次抬起事件距离上次点击的时间差
                long twoClickDurationTime = touchUpTime - lastSingleClickTime;
                //若两次点击事件时间差小于300ms则表示发生双击事件
                if(twoClickDurationTime <=  doubleClickDurationTime){
                    //自定义双击事件
                    touchType = TOUCH_DOUBLE_CLICK;
                    //由于已经确定最近的上一次点击事件的类型，可以重置变量
                    lastSingleClickTime = -1;
                    touchDownTime = -1;
                    touchUpTime = -1;
                }
                //若两次点击事件时间差大于300ms，则之前那次的点击一定是单击，
                //而这次触屏点击需要经过300ms后才能判断类型到底是不是双击
                else{
                    //保存本次点击事件时刻
                    lastSingleClickTime = touchUpTime;
                }
            }
        }
        //返回自定义事件类型
        return touchType;
    }
    ```

## 特色

- 四种类型子弹，并且可以通过接道具的形式改变子弹形式
- 4 中类型敌机，还存在 boss 敌机
- 敌机不仅可以与本机相撞，而且 boss 敌机还可以投射炸弹
- 存在黑夜和白天的渐变
- 画面效果较为生动
- 音乐动感力十足
- 游戏难度可以调整，音效可以调整
- 本机存在生命值，并且可以接生命值道具
- 本机可以接受炸弹道具
- 采取积分制
- 界面帧数显示

## 待优化

- 画面还可以再调优
- 游戏中飞机类型可以增多
- 道具可以增多
- 实现多语言切换
- 音乐多选择
- 设置关卡制
- 积分兑换道具等设定
- 其他

## 界面

[截图 1](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-50-41-709_cn.example.hel.jpg)

[截图 2](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-50-45-160_cn.example.hel.jpg)

[截图 3](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-50-58-874_cn.example.hel.jpg)

[截图 4](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-51-03-318_cn.example.hel.jpg)

[截图 5](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-51-08-523_cn.example.hel.jpg)

- 框架作者：**abcnull**
- csdn 博客：**https://blog.csdn.net/abcnull**
- github：**https://github.com/abcnull**
- e-mail：**abcnull@qq.com**

欢迎大家 **Watch**，**Star** 和 **Fork**！
