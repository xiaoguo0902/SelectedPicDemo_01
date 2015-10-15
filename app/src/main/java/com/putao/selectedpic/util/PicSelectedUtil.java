package com.putao.selectedpic.util;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by GZK on 2015/10/14.
 */
public class PicSelectedUtil {
    public static final int requestcode_camera = 100;//相机拍照
    public static final int requestcode_album = 101;//本地相册
    public static final int requestcode_zoom = 102;//剪裁图片
    public static final String TAG = PicSelectedUtil.class.getSimpleName();

    /**
     * 调用系统相机
     *
     * @param context context
     * @param obj     是activity调用或则 fragment调用
     * @return uri
     */
    public static Uri doTakePhoto(Context context, Object obj, int requestCode) {
        Uri mPhotoUri = null;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "存储卡不可用", Toast.LENGTH_SHORT).show();
            return mPhotoUri;
        }
        String filePath = FileUtil.getAccountImageTempFolder() + File.separator + FileUtil.getImgName();
        Log.e(TAG, "======filePath=====" + filePath);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            ContentValues contentValues = new ContentValues(2);
            contentValues.put(MediaStore.Images.Media.DATA, filePath);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            mPhotoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues);
            Log.e(TAG, "========mPhotoUri======" + mPhotoUri);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            if (obj instanceof Activity) {
                ((Activity) obj).startActivityForResult(intent, requestCode);
            } else if (obj instanceof Fragment) {
                ((Fragment) obj).startActivityForResult(intent, requestCode);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        }
        return mPhotoUri;
    }


    /**
     * 从本地选择图片
     *
     * @param context     context
     * @param requestCode 结果码
     */
    public static void choosePhoto(Context context, Object obj, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            if (obj instanceof Activity) {
                ((Activity) obj).startActivityForResult(intent, requestCode);
            } else if (obj instanceof Fragment) {
                ((Fragment) obj).startActivityForResult(intent, requestCode);
            } else {
                ((Activity) context).startActivityForResult(intent, requestCode);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "找不到相册", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 裁剪图片
     *
     * @param context     context
     * @param uri         图片uri
     * @param width       height 裁剪长宽大小
     * @param requestCode 结果码
     */
    public static void startPhotoZoom(Context context, Object obj, Uri uri, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", width);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("return-data", true);
        if (obj instanceof Activity) {
            ((Activity) obj).startActivityForResult(intent, requestCode);
        } else if (obj instanceof Fragment) {
            ((Fragment) obj).startActivityForResult(intent, requestCode);
        } else {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
    }


    /**
     * 选择照片后结束
     * 这个方法 在4.4后获取不到路径
     *
     * @param data data
     */
    public static Uri afterChoosePic(Context context, Intent data) {

        // 获取图片的路径：
        String[] proj = {MediaStore.Images.Media.DATA};
        // 好像是android多媒体数据库的封装接口，具体的看Android文档
        Cursor cursor = ((Activity) context).managedQuery(data.getData(), proj, null, null, null);
        if (cursor == null) {
            Toast.makeText(context, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
            return null;
        }
        // 按我个人理解 这个是获得用户选择的图片的索引值
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        // 将光标移至开头 ，这个很重要，不小心很容易引起越界
        cursor.moveToFirst();
        // 最后根据索引值获取图片路径
        String path = cursor.getString(columnIndex);

        if (path != null && (path.endsWith(".png") || path.endsWith(".PNG") ||
                path.endsWith(".jpg") || path.endsWith(".JPG"))) {
            return Uri.parse("file:///" + path); // 将绝对路径转换为URL
        } else {
            Toast.makeText(context, "上传的图片仅支持png或jpg格式", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 选择相册照片后获取图片绝对路径
     *
     * @param uri      uri
     * @param activity activity
     * @return string path
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String uri2filePath(Uri uri, Activity activity) {

        String path = "";
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = activity.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel,
                    new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    projection, null, null, null);
            int columnIndex = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return path;
    }

}
