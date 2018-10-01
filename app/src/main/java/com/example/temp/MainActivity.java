package com.example.temp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.temp.Sqlite.Sql;
import com.example.temp.Sqlite.Yao;
import com.example.temp.Sqlite.yaoDao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class MainActivity extends Activity {

    private float value;
    private String v;
    private String time;
    private String AccessToken;
    private Sql sql;
    private List list;
    private String url="http://api.nlecloud.com/devices/13067/Sensors/panao_temperature";
    private Handler handler;
    private String ti;
    private yaoDao dao;
    private MyAdapter2 myAdapter2;
    private ListView listView;
    private Boolean yesorno=false;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*********************************************************  传入数据AccesssToken  ******************************************************************/
        Bundle bundle = getIntent().getExtras();                               //得到传过来的bundle  
        assert bundle != null;
        AccessToken = bundle.getString("AccessToken");                    //读出数据

        /*********************************************************  发送请求得到value  ******************************************************************/
        sendRequestWithOkHttp();
        /*********************************************************  创建数据库  ******************************************************************/
        sql=new Sql(this);

        /*********************************************************  延时重复获取数据  ******************************************************************/
//开启
        SQLiteStudioService.instance().start(this);


        Button start=findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesorno=true;
            }
        });
        Button stop=findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesorno=false;
            }
        });

        handler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 4) {
                    /*** 写执行的代码*/
                    sendRequestWithOkHttp();
                    getNewTime();
                    Log.d("Value", v);
                    Log.d("Time", time);
                    Log.d("Time_true", ti);
                    /****
                     存储value,time,true_time三个值
                     ****/
                    if(yesorno) {
                        insertData(sql.getReadableDatabase(), v, time, ti);
                        list = dao.queryAll();
                        myAdapter2 = new MyAdapter2();
                        listView = findViewById(R.id.list_show);
                        listView.setAdapter(myAdapter2);
                        myAdapter2.notifyDataSetChanged(); // 刷新界面
                    }else {
                        Toast.makeText(MainActivity.this,"已停止记录",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // (1) 使用handler发送消息
                Message message=new Message();
                message.what=4;
                handler.sendMessage(message);
                getNewTime();
                TextView textView=findViewById(R.id.tim);
                textView.setText(ti);
            }
        },0,5000);

        /*********************************************************  刷新列表  ******************************************************************/
        dao=new yaoDao(this);
        list=dao.queryAll();
        myAdapter2=new MyAdapter2();
        listView=findViewById(R.id.list_show);
        listView.setAdapter(myAdapter2);

        myAdapter2.notifyDataSetChanged(); // 刷新界面
    }

    /*********************************************************  列表适配器  ******************************************************************/
    public class MyAdapter2 extends BaseAdapter {

        public int getCount() {                   // 获取条目总数
            return list.size();
        }

        public Object getItem(int position) { // 根据位置获取对象
            return list.get(position);
        }

        public long getItemId(int position) { // 根据位置获取id
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // 重用convertView
            View item = convertView != null ? convertView : View.inflate(
                    getApplicationContext(), R.layout.item, null);
            // 获取该视图中的TextView
            TextView id =item.findViewById(R.id.num);
            TextView val=item.findViewById(R.id.value);
            TextView ti =item.findViewById(R.id.true_time);

            // 根据当前位置获取Account对象

            final Yao handle = (Yao) list.get(position);
            // 把Account对象中的数据放到TextView中
            id.setText(handle.getId()+"");
            val.setText(handle.getValue());
            ti.setText(handle.getTrue_time());
            return item;
        }
    }


    /************************************************获取当前时间**************************************************************/
    public String getNewTime(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        ti=simpleDateFormat.format(date);
        Log.d("当前日期时间",""+simpleDateFormat.format(date));
        return simpleDateFormat.format(date);
    }

    /*********************************************************  请求语句  ******************************************************************/
    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
                HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //在这里进行异常情况处理
                    }
                    @Override
                    public void onResponse(Call call, @NonNull Response response) throws IOException {
                        //得到服务器返回的具体内容
                        String responseData = response.body().string();
                        parseJSONWithGSON(responseData);
                        //显示UI界面，调用的showResponse方法
                        // showResponse(responseData);
                    }
                }, AccessToken);
            }
        }).start();
    }

    public void insertData(SQLiteDatabase sqLiteDatabase,String value,String time,String time_true){
        ContentValues values=new ContentValues();
        values.put("value",value);
        values.put("time",time);
        values.put("true_time",time_true);
        sqLiteDatabase.insert("yao",null,values);
    }


    @SuppressLint("HandlerLeak")
    private void parseJSONWithGSON(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Data>() {}.getType();
        Data data = gson.fromJson(json, type);
        time=data.getResultObj().getRecordTime();
        value = (float) data.getResultObj().getValue();
        v=String.valueOf(value);
//        ArrayList list=new ArrayList();
//        list.add(time);
//        list.add(value);




        TextView temp=findViewById(R.id.temp);
        temp.setText(v);
        Log.d("Value",v);
    }
    public static class Data {
        private ResultObj ResultObj;
        private int Status;
        private int StatusCode;
        private String Msg;
        private Object ErrorObj;
        public static class ResultObj{
            private String ApiTag;
            private byte Groups;
            private byte Protocol;
            private String Name;
            private String createData;
            private byte TransType;
            private byte DataType;
            private Object TypeAttrs;
            private int DevicesID;
            private String SensorType;
            private float Value;
            private String RecordTime;
            //传感器
            private String Unit;
            //执行器
//            private byte OperType;
//            private String OperTypeAttrs;


            public String getRecordTime() {
                return RecordTime;
            }

            public Object getValue() {
                return Value;
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

        public Object getErrorObj() {
            return ErrorObj;
        }

        public Data.ResultObj getResultObj() {
            return ResultObj;
        }
    }
    /*********************************************************  摧毁机制  ******************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        sql.close();
        finish();
        SQLiteStudioService.instance().stop();
    }
}
