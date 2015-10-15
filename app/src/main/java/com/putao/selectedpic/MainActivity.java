package com.putao.selectedpic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.putao.selectedpic.util.FileUtil;
import com.putao.selectedpic.util.PicSelectedUtil;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ImageView imDemo;
    private Button btnSelectedPic;
    private Dialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDialog = createDialog(this);
        imDemo = (ImageView) findViewById(R.id.im_demo);
        btnSelectedPic = (Button) findViewById(R.id.btnSelected);
        btnSelectedPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiaolog();
            }
        });
    }


    private Dialog createDialog(Context context) {
        Dialog mLoginDialog = new Dialog(context,R.style.custom_dialog);
      /*  if (styleId > 0) {
            mLoginDialog = new Dialog(context, styleId);
        } else {
            mLoginDialog = new Dialog(context, R.style.custom_dialog);
        }
*/
        Window win = mLoginDialog.getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.x = 0;
        params.y = 0;
        win.setAttributes(params);
        mLoginDialog.setContentView(R.layout.select_pic);

        LinearLayout outSideDialog = (LinearLayout) mLoginDialog.findViewById(R.id.selectpicdialog_head);
        Button btnCamera = (Button) mLoginDialog.findViewById(R.id.selectpicdialog_paizhao);
        Button btnalbum = (Button) mLoginDialog.findViewById(R.id.selectpicdialog_frompic);
        Button btnCanel = (Button) mLoginDialog.findViewById(R.id.selectpicdialog_cancel);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canelDialog();
                mPhotoUri= PicSelectedUtil.doTakePhoto(MainActivity.this, MainActivity.this, PicSelectedUtil.requestcode_camera);
            }
        });
        btnalbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PicSelectedUtil.choosePhoto(MainActivity.this, MainActivity.this, PicSelectedUtil.requestcode_album);
                canelDialog();
            }
        });
        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canelDialog();


            }
        });
        outSideDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canelDialog();


            }
        });
        return mLoginDialog;
    }

    private void showDiaolog() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void canelDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    private Uri mPhotoUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == PicSelectedUtil.requestcode_camera) {
                Uri uri = null;
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                }
                if (uri == null && mPhotoUri != null) {
                    uri = mPhotoUri;
                }
                if (uri != null) {
                    PicSelectedUtil.startPhotoZoom(this, this,
                            uri, 200, 200, PicSelectedUtil.requestcode_zoom);
                }
            } else if (requestCode == PicSelectedUtil.requestcode_album) {
                if (data != null && data.getData() != null) {
                    Uri uri = data.getData();
                    String path = PicSelectedUtil.uri2filePath(uri, this);
                    Log.e(TAG, "==剪裁=path===" + path);
                    Uri newUri = Uri.parse("file:///" + path); // 将绝对路径转换为URL
                    PicSelectedUtil.startPhotoZoom(this, this,
                            newUri, 200, 200, PicSelectedUtil.requestcode_zoom);
                } else {
                    Log.e(TAG, "===选择本地图片失败===");
                }

            } else if (requestCode == PicSelectedUtil.requestcode_zoom) {
                if (data != null) {
                    Bitmap photoBitmap = data.getParcelableExtra("data");
                    if (photoBitmap != null) {
                        imDemo.setImageBitmap(photoBitmap);
                        FileUtil.saveUserImgFile(this, photoBitmap);
                    } else {
                        Log.e(TAG, "===剪裁图片失败===");
                    }
                }
            }
        }
    }
}
