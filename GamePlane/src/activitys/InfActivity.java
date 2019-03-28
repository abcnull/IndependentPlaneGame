package activitys;

import cn.example.helloworld.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/*
 * 此界面是显示制作信息相关，较普通
 * 目前做到有textview控件显示基本制作人信息，还有确认的按钮来返回到主界面
 * 后续会添加更多信息
 */

//制作信息相关界面
public class InfActivity extends Activity implements Button.OnClickListener {

	//创建时执行
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//标题不可见
		setContentView(R.layout.activity_inf);
	}

	//点击时执行
	@Override
	public void onClick(View v){
		int viewId = v.getId();
		if(viewId == R.id.btnOk){
			backInf();//会转到主界面
		}
	}

	//确认并返回主界面
	public void backInf(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
