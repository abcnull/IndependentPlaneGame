package activitys;

import cn.example.helloworld.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

/*
 * 此界面为主界面，有四个按钮分别是开始游戏，设置游戏，游戏介绍和制作人信息按钮
 * 点击开始游戏按钮可以进入GameActivity活动中，此活动绑定了一个GameView界面
 * 点击设置按钮可以进入SetActivity活动中，此活动可以调整游戏难度为三个难度，最后要点击保存返回，游戏默认难度是简单难度
 * 点击制作人信息按钮可以查看制作人相关信息，最后有个确认按钮来返回
 */

//主界面
public class MainActivity extends Activity implements Button.OnClickListener {
	
	//活动创建时执行
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//标题不可见
        setContentView(R.layout.activity_main);
    }

    //点击事件响应时执行
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.btnGame){
            startGame();//转到GameActivity活动
        }
        else if(viewId == R.id.btnSet){
        	setGame();//转到SetActivity活动
        }
        else if(viewId == R.id.btnIntro){       	
        	introGame();//转到RefActivity活动
        }
        else if(viewId == R.id.btnInf){
        	infGame();//转到InfActivity活动
        }
        else if(viewId == R.id.btnExit){
        	exitGame();
        }
    }

    //点击开始游戏时执行
    public void startGame(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);//转到游戏中
    }
    
    //点击设置游戏时执行
    public void setGame(){
    	Intent intent = new Intent(this, SetActivity.class);
    	startActivity(intent);//转到设置中
    }
    
    //点击游戏说明时执行
    public void introGame(){
    	Intent intent = new Intent(this, IntroActivity.class);
    	startActivity(intent);//转到游戏介绍中
    }
    
    //点击制作人信息时执行
    public void infGame(){
    	Intent intent = new Intent(this, InfActivity.class);
    	startActivity(intent);//转到制作人中   	
    }
    
    //点击退出游戏时执行
    public void exitGame(){
    	System.exit(0);
    }
}