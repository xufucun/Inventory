package cn.xufucun.udacity.inventory;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xufuc on 2017/12/13.
 */

public final class ToastUtil {

    private ToastUtil() {

    }

    private static Toast mToast = null;

    public static void show(Context context, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.setText(message);
        }
        mToast.show();
    }
}
