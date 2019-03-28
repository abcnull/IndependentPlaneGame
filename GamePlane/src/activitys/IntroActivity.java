package activitys;

import cn.example.helloworld.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.view.View;
import android.view.Window;

/*
 * 这个界面是游戏介绍的界面，主要介绍游戏的玩法等，下面有个按钮用于返回到主界面
 */
public class IntroActivity extends Activity implements Button.OnClickListener {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//标题不可见
        setContentView(R.layout.activity_intro);
    }
	
	@Override
	public void onClick(View v){
		int viewId = v.getId();
		if(viewId == R.id.btnYes){//点击确认返回主界面
			backIntro();//返回主界面意图
		}
	}
	
	//确认信息并返回主界面
	public void backIntro(){
		Intent intent = new Intent(this, MainActivity.class); 
		startActivity(intent);
	}

}
