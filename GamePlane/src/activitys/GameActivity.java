package activitys;

import java.io.IOException;

import cn.example.helloworld.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Window;

import game.GameView;

/*
 * 这个Activity比较特殊，其中有一个GameView，在此进行游戏
 * 开始游戏是通过startReady()方法进入gameview内部的
 * 注意传了一个model参数，这个参数在SetActivity类中的声明有讲
 * 其实GameView中的startReady()方法并不是真正的绘制游戏的方法
 * 真正绘制游戏的方法是GameView中的onDraw()，它是自动执行
 * 可是startReady()方法却是每次开启游戏必须要做的准备工作，执行完后还要做其他准备，然后才能重绘onDraw()
 */

//游戏界面
public class GameActivity extends Activity {

    private GameView gameView;
    public static String model = "easy";//公有静态String变量表示游戏难度"easy""normal""elite"
    public static boolean sound = true;//游戏背景声音，默认打开
    private MediaPlayer mediaPlayer;

    //创建时执行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//标题不可见
        setContentView(R.layout.activity_game);        
        gameView = (GameView)findViewById(R.id.gameView);             
        gameView.startReady(model, this);//进入GameView中
    }

    //可见时执行
    @Override
    protected void onStart(){
    	super.onStart();
    	if(sound && gameView != null)//若同意开启声音
        	playMusic();
    }
    
    //获取焦点时执行
    @Override
    protected void onResume(){
    	super.onResume();
    }
    
    //失去焦点时执行
    @Override
    protected void onPause() {
        super.onPause();
        if(gameView != null)
            gameView.pause();//活动卡住不能获取焦点，游戏被暂停       
    }

    //不可见时执行
    @Override
    protected void onStop(){
    	super.onStop();
    	if(mediaPlayer != null)
    		mediaPlayer.release();//销毁媒体音乐
    }
    
    //销毁活动时执行
    @Override
    protected void onDestroy() {
        super.onDestroy();       
        if(gameView != null)
            gameView.destroy();//活动被意外销毁，游戏被销毁
        gameView = null;
    }
    
    //在此活动界面播放音乐 
    private void playMusic(){   	       
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
}