package game;

import android.graphics.Bitmap;

/**
 * 大敌机类，体积大，抗打击能力强
 */

public class BigEnemyPlane extends EnemyPlane {

    public BigEnemyPlane(Bitmap bitmap){
        super(bitmap);
        setPower(5);//大敌机抗抵抗能力为4，即需要4颗子弹才能销毁大敌机
        setValue(50);//销毁一个大敌机可以得40分
    }

}