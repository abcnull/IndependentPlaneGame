package game;

import android.graphics.Bitmap;

/**
 * 中敌机类，体积中等，抗打击能力中等
 */

public class MiddleEnemyPlane extends EnemyPlane {

    public MiddleEnemyPlane(Bitmap bitmap){
        super(bitmap);
        setPower(3);//中敌机抗抵抗能力为2，即需要2颗子弹才能销毁中敌机
        setValue(30);//销毁一个中敌机可以得20分
    }

}