package com.zsl.onestep;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity {

    FloatingActionButton fab;
    TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        initToolbar();

        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化ToolBar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("OneStep Demo");
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tv_message = (TextView) findViewById(R.id.scroll_tv_message);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                goSystemBrowser("https://github.com/yy1300326388/OneStep");
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        getSendData();
    }

    /**
     * 获取从其他app发送过来的数据
     */
    private void getSendData() {
        tv_message.setText("One Step Demo \n\nData:\n");
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {//文字
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    tv_message.append(sharedText + "\n");
                }
            } else if (type.startsWith("image/")) {//单张图片和文字
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    tv_message.append(sharedText + "\n");
                }
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                tv_message.append(imageUri.getPath() + "\n");

            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {//多张图片
            if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                for (Uri uri : imageUris) {
                    tv_message.append(UriToFilePath(uri) + "\n");
                }
            }
        }
    }

    /**
     * url转为FilePath
     *
     * @param imageUri Uri
     * @return 返回最终文件的实际路径
     */
    private String UriToFilePath(Uri imageUri) {
        if (imageUri != null) {
            String imagePath = imageUri.getPath();
            if (ContentResolver.SCHEME_CONTENT.equals(imageUri.getScheme())) {
                imagePath = getStringPathFromURI(this, imageUri);
            }
            return imagePath;
        }
        return null;
    }

    /**
     * 从ContentResolver 中或得到文件的实际地址
     *
     * @param context    上下文
     * @param contentUri Uri
     * @return 返回最终文件的实际路径
     */
    public String getStringPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 去系统浏览器打开url
     *
     * @param url url
     */
    private void goSystemBrowser(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }
}
