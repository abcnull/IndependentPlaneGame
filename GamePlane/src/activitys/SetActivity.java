package activitys;

import cn.example.helloworld.R;
import game.GameView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/*
 * 此界面为设置游戏界面，有四个按钮，分别是设置游戏简单，设置游戏一般，设置游戏困难，还有保存并返回按钮
 * 原理是在GameActivity活动中有个公有静态变量String mode，用来保存游戏是哪种难度模式
 * 而点击那三中难度模式只不过是改变model这个公有静态变量的值，而点击保存并返回按钮就可以返回到主界面MainActivity中去
 * 在主界面又可以开始游戏到GameActivity活动中
 * 由于GameView在GameActivity中，可以通过调用GameView的startReady()方法来开启游戏
 * 注意startReady()其中传了一个model的难度参数以此来设置游戏难度
 * 在游戏的View中有这样的degree变量来判断游戏难度，默认为简单，匹配传过来的model来调整难度
 */

//设置难度界面
public class SetActivity extends Activity implements Button.OnClickListener {
	
	private CheckBox btnSound; 
	
	//创建时执行
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//标题不可见
        setContentView(R.layout.activity_set);
        btnSound = (CheckBox)findViewById(R.id.btnSound);
    }
	
	//可见时执行
	@Override
	protected void onStart(){
		super.onStart();
		if(GameActivity.sound)//若声音有
			btnSound.setChecked(true);//设置checkbox打勾
		else//若声音无
			btnSound.setChecked(false);//设置checkbox不打勾
	}
	
	//点击时执行
	@Override
    public void onClick(View v) {
		//每当检测到有个点击事件发生，会创建一个指向主界面的意图
		Intent intent = new Intent(this, MainActivity.class);
        int viewId = v.getId();
        if(viewId == R.id.btnBack){
        	backSet(intent);//会转到主界面
        }
        else if(viewId == R.id.btnEasy){
            setEasy();//简单
        }
        else if(viewId == R.id.btnNormal){
        	setNormal();//一般
        }
        else if(viewId == R.id.btnElite){
        	setElite();//困难
        }
        else if(viewId == R.id.btnSound){
        	setSound();
        }
    }
	
	//返回到主界面
	public void backSet(Intent intent){
        startActivity(intent);//转到主界面
	}
	
	//设置简单难度
	public void setEasy(){
		GameActivity.model = "easy";
		Toast.makeText(SetActivity.this,"您已设置简单", Toast.LENGTH_SHORT).show();
	}

	//设置一般难度
	public void setNormal(){
		GameActivity.model = "normal";
		Toast.makeText(SetActivity.this,"您已设置普通", Toast.LENGTH_SHORT).show();
	}
	
	//设置地狱难度
	public void setElite(){
		GameActivity.model = "elite";
		Toast.makeText(SetActivity.this,"您已设置困难", Toast.LENGTH_SHORT).show();
	}
	
	//设置声音
	public void setSound(){
		GameActivity.sound = !GameActivity.sound;
	}
}
