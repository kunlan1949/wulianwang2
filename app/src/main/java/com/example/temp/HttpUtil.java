package com.example.temp;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {

    public static void sendOkHttpRequest(final String address, final okhttp3.Callback callback,final String UserAccessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .addHeader("AccessToken",UserAccessToken)
                .build();
        client.newCall(request).enqueue(callback);

    }
//    private void sendWithOkHttp() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //在子线程中执行Http请求，并将最终的请求结果回调到okhttp3.Callback中
//                HttpUtil.sendOkHttpRequest(url_data, new okhttp3.Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        //在这里进行异常情况处理
//                    }
//                    @Override
//                    public void onResponse(Call call, @NonNull Response response) throws IOException {
//                        //得到服务器返回的具体内容
//                        String responseData = response.body().string();
//                        JSONWithGSON(responseData);
//                        //显示UI界面，调用的showResponse方法
//                        showResponse(responseData);
//                    }
//                }, AccessToken);
//            }
//        }).start();
//    }
//    private void JSONWithGSON(String json) {
//        Gson gson = new Gson();
//        java.lang.reflect.Type type = new TypeToken<MM>() {}.getType();
//        MM mm = gson.fromJson(json, type);
//        Va=mm.ResultObj.getDatas().getValue();
//        Log.d("Va",String.valueOf(Va));
//    }
//    public static class MM{
//        private ResultObj ResultObj;
//        private int Status;
//        private int StatusCode;
//        private String Msg;
//        private Object ErrorObj;
//        public static class ResultObj{
//            private int DeviceID;
//            private String Name;
//            private Datas Datas;
//            private  static  class  Datas{
//                private String ApiTag;
//                private Object Value;
//                private String RecordTime;
//
//                public Object getValue() {
//                    return Value;
//                }
//            }
//
//            public MM.ResultObj.Datas getDatas() {
//                return Datas;
//            }
//        }
//        public int getStatus() {
//            return Status;
//        }
//
//        public int getStatusCode() {
//            return StatusCode;
//        }
//
//        public String getMsg() {
//            return Msg;
//        }
//
//        public Object getErrorObj() {
//            return ErrorObj;
//        }
//
//        public MM.ResultObj getResultObj() {
//            return ResultObj;
//        }
//
//    }
}
