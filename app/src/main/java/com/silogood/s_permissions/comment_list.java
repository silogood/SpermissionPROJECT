package com.silogood.s_permissions;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Comment_list extends AppCompatActivity implements View.OnClickListener {

    private final String URL = "http://spermission.dothome.co.kr/comment.php";
    AsyncHttpClient client;
    HttpResponse httpResponse;

    ListView listView1;
    CommentAdapter adapter;
    Button send;
    EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);

        client = new AsyncHttpClient();
        httpResponse = new HttpResponse();
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(this);
        comment = (EditText) findViewById(R.id.comment);

        listView1 = (ListView) findViewById(R.id.list1);
        adapter = new CommentAdapter(this, R.layout.comment_list, new ArrayList<Comment>());
        listView1.setAdapter(adapter);

        load();


  //      adapter.add(new Comment("딸기", "꼭지가 마르지 않고 진한 푸른색을 띠는 것이 좋다."));
    }

    public void load() {
        RequestParams params = new RequestParams();
        params.put("google_id", getIntent().getStringExtra("google_id"));
        params.put("package_app_name", getIntent().getStringExtra("package_app_name"));
        params.put("package_name", getIntent().getStringExtra("package_name"));
        params.put("process", "load");

        client.post(URL, params, httpResponse);
    }

    @Override
    public void onClick(View v) {

        if(comment.getText().toString().equals("")) {
            Toast.makeText(Comment_list.this,"Rewrite!",Toast.LENGTH_LONG).show();
            return;
        }
        if(comment.getText().toString().length()>100) {
            Toast.makeText(Comment_list.this,"Too Long!",Toast.LENGTH_LONG).show();
            return;
        }

        adapter.clear();

        RequestParams params = new RequestParams();
        params.put("google_id", getIntent().getStringExtra("google_id"));
        params.put("package_app_name", getIntent().getStringExtra("package_app_name"));
        params.put("package_name", getIntent().getStringExtra("package_name"));
        params.put("comment", comment.getText().toString());
        params.put("process", "send");

        comment.setText("");

        client.post(URL, params, httpResponse);
    }

    private class CommentAdapter extends ArrayAdapter<Comment> {
        int resource;

        public CommentAdapter(Context context, int resource,
                           List<Comment> objects) {
            super(context, resource, objects);
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                LayoutInflater li = getLayoutInflater();
                itemView = li.inflate(this.resource, null);
            }

            Comment item = getItem(position);

            if (item != null) {
                TextView textView1 = (TextView) itemView.findViewById(R.id.comment);
                TextView textView2 = (TextView) itemView.findViewById(R.id.date);

                textView1.setText(item.getComment());
                textView2.setText(item.getDate());
            }

            return itemView;
        }
    }

    public class Comment {
        private String comment;
        private String date;

        public Comment(String comment, String date) {
            this.comment = comment;
            this.date = date;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }

    /* 통신 결과를 받아서 처리할 클래스 - inner class 형태로 정의한다. */
    public class HttpResponse extends JsonHttpResponseHandler {

        ProgressDialog dialog;

        /* 통신은 이루어 졌으나 서버에서 에러코드를 반환할 경우 호출된다.
         * @param stateCode	상태코드 (HTTP 상태코드 값이 전달된다. 404, 500)
         * @param header	HTTP Header
         * @param error		에러정보 객체
         */
        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            // TODO Auto-generated method stub
//            String errMsg = "State Code: " + statusCode + "\n";
//            errMsg += "Error Message: " + throwable.getMessage();


//            Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
            super.onFailure(statusCode, headers, responseString, throwable);
        }

        /* 통신 접속 실패시 호출된다.
         * @param stateCode	상태코드 (0이 전달된다.)
         * @param header	HTTP Header
         * @param error		에러정보 객체
         */
        @Override
        public void onFailure(int statusCode, Header[] headers,
                              Throwable throwable, JSONObject errorResponse) {
            // TODO Auto-generated method stub
 //           String errMsg = "State Code: " + statusCode + "\n";
//            errMsg += "Error Message: " + throwable.getMessage();

//			tv1.setText(errMsg);
            Toast.makeText(getApplicationContext(), "Please check your Internet connection.", Toast.LENGTH_SHORT).show();
            super.onFailure(statusCode, headers, throwable, errorResponse);
        }

        /* 통신 성공시 호출된다.
         * @param stateCode	상태코드
         * @param header	HTTP Header
         * @param response	서버의 응답 내용
         */
        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              JSONObject response) {
            // TODO Auto-generated method stub
            super.onSuccess(statusCode, headers, response);

            try {

                JSONArray results = response.getJSONArray("products");
                JSONObject re;

                int total = results.length();

                for(int i=0; i<total;i++) {
                    re = results.getJSONObject(i);
                    String comment = re.getString("comment");
                    String comment_day = re.getString("comment_day");
                    adapter.add(new Comment(comment, comment_day));
                }
                //			tv1.setText(statusCode+"/"+name+"/"+type);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        /* 성공, 실패 여부에 상관 없이 통신이 종료되면 실행된다. */
        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            super.onFinish();
            dialog.dismiss();
            dialog = null;
        }

        /* 통신 시작시에 실행된다. */
        @Override
        public void onStart() {
            // TODO Auto-generated method stub
            dialog = new ProgressDialog(Comment_list.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
            super.onStart();
        }

    } // end class
}
