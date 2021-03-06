package com.panda_doc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.panda_doc.main.R;
import com.panda_doc.conf.Conf;
import com.panda_doc.conf.Constants;
import com.panda_doc.qq.QQSDKListener;
import com.panda_doc.version.CheckVersionDialogFragment;
import com.panda_doc.wechat.WeChatSdk;
import com.tencent.tauth.Tencent;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends FragmentActivity {

    private static String TAG_DIALOG = "TASK_FRAGE";

    private static final String TAG = MainActivity.class.getName();
//    /**
//     * wechat
//     */
//    private ImageButton wechatLogin;
    WeChatSdk weChatSdk;
    /**
     * QQ
     */
    private ImageButton qqLogin;
    QQSDKListener qqsdkListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

//        /** 微信登陆*/
//        wechatLogin = (ImageButton) findViewById(R.id.login_wechat);
//        wechatLogin.setImageResource(R.drawable.icon48_wx_button);
//        wechatLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                weChatSdk = new WeChatSdk(MainActivity.this);
//                /** 微信登陆*/
//
//                weChatSdk.loginWeChat();
//            }
//        });

        /** QQ登录*/
        qqLogin = (ImageButton) findViewById(R.id.login_qq);
        qqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** QQ登录*/
                qqsdkListener = new QQSDKListener(MainActivity.this);
                qqsdkListener.loginQQ();
            }
        });

        checkPermission();
//        versionCheck();
    }

    /**
     * 版本检测
     */
    private void versionCheck() {
        //TODO 整合为1个Activity后，这里的硬编码 en需要改掉 从userInfoViewModule中读取
        String moreUrl = Conf.URL_DOC_CONTENT_PRE + Constants.LAN_ZH_CN + "/" + Conf.URL_VERSION;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, moreUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String version = jsonObject.getString(Conf.KEY_VERSION);
                            String apk_url = jsonObject.getString(Conf.KEY_APK_URL);
                            String apk_name = jsonObject.getString(Conf.KEY_APK_NAME);

                            String packageVersionName = MainActivity.this.getPackageManager().
                                    getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
                            if (!version.equals(packageVersionName)) {
                                showUpdateUI(apk_url, apk_name);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    private void showUpdateUI(String apkUrl, String apkName) {
        CheckVersionDialogFragment fragment = new CheckVersionDialogFragment();
        fragment.setApkUrl(this, apkUrl, apkName);

        /** 替换 fr.showNowe()方法, 原方法会报异常*/
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(fragment, TAG_DIALOG);
        ft.commitAllowingStateLoss();
    }

    private void checkPermission() {
        int permissionCheck_0 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck_0 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSIONS_REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == Constants.PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.please_give_me_storage_permission), Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == Constants.PERMISSIONS_REQUEST_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.please_give_me_storage_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * QQ登陆回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "-->onActivityResult " + requestCode + " resultCode=" + resultCode);
        if (requestCode == com.tencent.connect.common.Constants.REQUEST_LOGIN ||
                requestCode == com.tencent.connect.common.Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqsdkListener);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}


