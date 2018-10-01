package com.example.temp.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class yaoDao {
    private Sql helper;
    public yaoDao(Context context){
        //创建Dao时，创建Helper
        helper=new Sql(context);
    }
    public List queryAll(){
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor c=db.query("yao",null,null,null,null,null,null);
        List list=new ArrayList<>();
        while(c.moveToNext()){
            //可以根据列名获取索引
            long id=c.getLong(c.getColumnIndex("id"));
            String value=c.getString(1);
            String time=c.getString(3);
            list.add(new Yao(id,value,time));
        }
        c.close();
        db.close();
        return list;
    }
}