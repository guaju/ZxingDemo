package com.guaju.zxingdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.view.ViewfinderView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private static final int REQUEST_CODE = 10;
    private static final int REQUEST_2WEIMA_CODE = 1;
    private ViewfinderView viewfinder_view;
    private Button bt;
    Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        bt = findViewById(R.id.bt);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.bt){
           //首先判断用户手机的版本号 如果版本大于6.0就需要动态申请权限
            //如果版本小于6.0就直接去扫描二维码
            if(Build.VERSION.SDK_INT<23){
                //说明是android6.0之前的
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                //第二个参数是请求码
                startActivityForResult(intent, REQUEST_CODE);
            }else{

                //添加动态申请权限




                //1.定义一个数组 装载需要的权限
                String[] permissions={
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA ,
                        Manifest.permission.VIBRATE
                };
                //2.判断这些权限有没有申请，如果没有申请，那么就把没有申请的权限加到另一个数组里边去
                ArrayList<String> deniedPermissions=new ArrayList<>();
                for (String permission:permissions){
                    int i = ContextCompat.checkSelfPermission(this, permission);
                    if (PackageManager.PERMISSION_DENIED==i){
                        //说明权限没有被申请
                        deniedPermissions.add(permission);
                    }

                }
                if (deniedPermissions.size()==0){
                    return;
                }
                //当你不知道数组多大的时候，就可以先创建一个集合，然后调用集合的toArray方法需要传递一个数组参数，这个数组参数的长度
                //设置成跟集合一样的长度
                String[] strings = deniedPermissions.toArray(new String[deniedPermissions.size()]);
                //3.去申请权限
                ActivityCompat.requestPermissions(MainActivity.this,strings,REQUEST_2WEIMA_CODE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    //4.去检查有没有申请成功
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_2WEIMA_CODE){
              //说明是我们为二维码申请的权限的回调
            for (int i=0;i<grantResults.length;i++){
                if (grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    //说明申请成功了
                    Toast.makeText(this, permissions[i]+"权限申请成功了", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    //第二个参数是请求码
                    startActivityForResult(intent, REQUEST_CODE);
                }else{
                    Toast.makeText(this, permissions[i]+"权限没有申请成功，为了您的体验，请下次通过该权限", Toast.LENGTH_SHORT).show();
                }
            }



        }




    }
}
