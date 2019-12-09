[TOC]

**[中文](https://github.com/abcnull/IndependentPlaneGame) | [Blog](https://blog.csdn.net/abcnull/article/details/103453758)**

# IndependentPlaneGame

## GitHub Source

GitHub source: [IndependentPlaneGame](https://github.com/abcnull/IndependentPlaneGame)

## Brief

This project was completed long ago.Before i use Android Studio to complete this project. This is a Plane Game without mainstream game engine instead of this is android come with graphics package. Canvase in graphics package takes the main task. In general, through extend relationshit almost class will extend Sprite Class. This peoject provide lots of features

- Author: **abcnull**
- Csdn Blog: **https://blog.csdn.net/abcnull**
- GitHub: **https://github.com/abcnull**
- E-Mail: **abcnull@qq.com**

Welcome to **Watch**，**Star** and **Fork**！

## Key Code

## 关键代码

1. Collision detection:

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

2. Add music:

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

3. FPS display:

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

4. Picture fade in and out:

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

5. Random enemies:

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

6. Click, double click and mouse move:
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


## Features

- 4 types bullets, plane can get deferrent types of bullets by picking up props
- 4 types enemies, include boss enemy
- Enemy can not only impact plane but also hit plane by fire ball like boss enemy
- Night fade into day and day fade into night
- Vivid game screen
- Vivid game music
- Adjustable game difficulty
- Plane have HP and it can pick up HP props
- Plane can pick up bomb props
- Using points system
- Screen displays FPS

## To Optimize

- Game screen can be optimized
- The types of enemies can be added
- Props can be added
- Muti language can take into account
- Muti music take into account
- Checkpoint system
- Credit system
- Etc

## Screenshot

[Screenshot 1](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-50-41-709_cn.example.hel.jpg)

[Screenshot 2](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-50-45-160_cn.example.hel.jpg)

[Screenshot 3](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-50-58-874_cn.example.hel.jpg)

[Screenshot 4](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-51-03-318_cn.example.hel.jpg)

[Screenshot 5](https://github.com/abcnull/Image-Resources/blob/master/IndependentPlaneGame/Screenshot_2019-12-09-00-51-08-523_cn.example.hel.jpg)

- Author: **abcnull**
- Csdn Blog: **https://blog.csdn.net/abcnull**
- GitHub: **https://github.com/abcnull**
- E-Mail: **abcnull@qq.com**

Welcome to **Watch**，**Star** and **Fork**！
