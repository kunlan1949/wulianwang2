package com.example.temp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private String N;
    private String P;

    private int status;
    private String AccessToken;
    private String msg;
    private TextView responseText;
    private String responseData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Button login=findViewById(R.id.login);
        responseText=findViewById(R.id.responseText);
        CheckBox rp=null;
        EditText name=findViewById(R.id.name);
        EditText password=findViewById(R.id.password);
        N=name.getText().toString();
        P=password.getText().toString();
        rp=findViewById(R.id.checkbox);
        rp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    showResponse(responseData);
                    Toast.makeText(Login.this,"记住密码",Toast.LENGTH_SHORT).show();
                }
            }
        });





        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttp(N,P);
                if(status==0) {
                    Intent intent= new Intent();
                    intent.setClass(Login.this,MainActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("AccessToken","06A1101D4FFB10084408B9421A7E65BCDB6E744E0B39B33FB7C43EC52377335788CC51938CFBED4C095F581CE42D6ECB93163390D376CE7A26FC55BC148E43DE5C42D452165E71BC12F8AFEC854DE67F2AD63BA3A57AC646A80FF5BD4392879436728DFC6F6E15390645A3A90AE644AAF76CDC7F6D7FDA2DADDA217A6A60AD1A802F5477D9081F8550A0381F966A9129C0E30BC9B95FA3FC25956F378C12CAD47EDADEA4BD305E95D862457A40EA3813CA620115E58AB94E7AE5EBE98F6F44200AFBDC4035B39625FA205AD7583FDAC1CF36418F9B249D3C6002E62AF3C8DFCE64672C75AD37D9B368752A910D13B471");//压入数据    
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Toast.makeText(Login.this, "登陆成功!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Login.this, "登陆失败,请检查用户名和密码!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    /************************************************向服务器发送登录请求**************************************************************/
    public void sendRequestWithOkHttp(final String Account, final String password) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();   //定义一个OKHttpClient实例
                    RequestBody requestBody = new FormBody.Builder()
                            .add("Account", Account)
                            .add("Password", password)
                            .add("IsRememberMe", "true")
                            .build();
                    Log.d("Account", Account);
                    Log.d("Password", password);
                    //实例化一个Respon对象，用于发送HTTP请求
                    Request request = new Request.Builder()
                            .url("http://api.nlecloud.com/Users/Login")             //设置目标网址
                            .post(requestBody)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();  //获取服务器返回的数据
                    if (response.body() != null) {
                        responseData = response.body().string();//存储服务器返回的数据
                        Log.d("data", responseData);
                        parseJSONWithGSON(responseData);


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showResponse(final String response) {
        //在子线程中更新UI
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                responseText.setText(response);
            }
        });
    }
    private void parseJSONWithGSON(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Login.App>() {}.getType();
        App app = gson.fromJson(json, type);
        status = app.getStatus(); // 获取登录状态
        AccessToken = app.getResultObj().getAccessToken(); //获取返回的确定设备标识的字符串

        msg = app.getMsg();
        Log.d("AccessToken_data",AccessToken);
        Log.d("status", String.valueOf(status));
    }



    public static class App {
        private Login.App.ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private  int UserID;
            private String UserName;
            private String Email;
            private String Telphone;
            private Boolean Gender;
            private int CollegeID;
            private String CollegeName;
            private String RoleName;
            private int RoleID;
            private String AccessToken;
            private String ReturnUrl;
            private String DataToken;

            public String getAccessToken() {
                return AccessToken;
            }
        }

        public int getStatus() {
            return Status;
        }

        public int getStatusCode() {
            return StatusCode;
        }

        public String getMsg() {
            return Msg;
        }

        public void setStatus(int status) {
            Status = status;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }

        public Object getErrorObj() {
            return ErrorObj;
        }

        public Login.App.ResultObj getResultObj() {
            return ResultObj;
        }
    }




}
