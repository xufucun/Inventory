package cn.xufucun.udacity.inventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xufuc on 2017/12/13.
 */

public final class Utils {

    private Utils() {

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





    /**
     * 图片转byte二进制
     * @param bitmap 需要转byte的照片
     * @return
     */
    public byte[] getBitmapByte(Bitmap bitmap){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //参数1转换类型，参数2压缩质量，参数3字节流资源
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 将二进制流转换成图片（Bitmap）
     * @param temp
     * @return
     */
    public Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }

//    /**
//     * 将二进制流转换成图片（Drawable）
//     * @param temp
//     * @return
//     */
//    public Drawable getBitmapFromByte(byte[] temp){
//        if(temp != null){
//            Drawable drawable = Drawable.createFromStream(bais, "image");
//            return drawable ;
//        }else{
//            return null;
//        }
//    }

}
