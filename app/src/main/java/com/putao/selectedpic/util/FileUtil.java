package com.putao.selectedpic.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by GZK on 2015/10/14.
 */
public class FileUtil {
    private static final String RootFolder = "demo";
    private static final String ImageFolder = "img";
    private static final String AccountImageFolder = "account";
    private static final String AccountImageTmpFolder = "accountTmp";

    /**
     * 获取用户头像本地报讯路径 文件夹
     *
     * @return string
     */
    public static String getAccountImageFolder() {
        String temp = getImageFolder() + File.separator + AccountImageFolder;
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取用户头像本地报讯路径 临时目录
     *
     * @return string
     */
    public static String getAccountImageTempFolder() {
        String temp = getImageFolder() + File.separator + AccountImageTmpFolder;
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }


    /**
     * 获取图像 文件夹 路径
     *
     * @return string
     */
    public static String getImageFolder() {
        String temp = getRootFolder() + File.separator + ImageFolder;
        File file = new File(temp);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取根存储文件夹
     */
    public static String getRootFolder() {
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(folder + File.separator + RootFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取当前时间戳
     */
    //@SuppressLint("SimpleDateFormat")
    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(date);
    }

    /**
     * 系统生成文件名
     */
    public static String getImgName() {
        String name = getTimeStamp() + ".jpg";
        return name;
    }

    /**
     * 删除该目录下的文件
     *
     * @param path path
     */
    public static void delFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }


    public static String saveUserImgFile(Context context, Bitmap bmp) {
        String name = getImgName();
        String dirFile = getAccountImageFolder();//获取存放路径
        String filePath = dirFile + File.separator + name;

        if (context != null && bmp != null && filePath != null) {
            saveImgFile(context, bmp, filePath);
        }
        return filePath;
    }

    /**
     * 保存图片
     *
     * @param bmp     需保存的bitmap
     * @param imgPath 保存图片的路径及名称
     * @return 保存后的文件路径
     */
    public static void saveImgFile(Context context, Bitmap bmp, String imgPath) {
        ContentValues cv = new ContentValues();

        cv.put(Media.DATA, imgPath);
        Uri imageFileUri = context.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, cv);

        try {
            if (imageFileUri != null) {
                OutputStream imageFileOS = context.getContentResolver().openOutputStream(imageFileUri);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, imageFileOS);
                imageFileOS.flush();
                imageFileOS.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
