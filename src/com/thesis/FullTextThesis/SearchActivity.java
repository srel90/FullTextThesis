package com.thesis.FullTextThesis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 7/6/13
 * Time: 7:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends Activity {
    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.search_activity);
        findViewById(R.id.btnsearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        findViewById(R.id.btnback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent newActivity = new Intent(SearchActivity.this,MainMenuActivity.class);
                        newActivity.putExtra("idmem", getIntent().getExtras().getString("idmem"));
                        newActivity.putExtra("name", getIntent().getExtras().getString("name"));
                        newActivity.putExtra("lname", getIntent().getExtras().getString("lname"));
                        startActivity(newActivity);
                        finish();
            }
        });
    }

    private void search() {
        final ListView listView = (ListView)findViewById(R.id.listView);
        final EditText searchstr = (EditText)findViewById(R.id.txtsearch);
        if (searchstr.length()!=0){
            String url = "http://10.0.2.2/thesis_db11/androidSearch.php";
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("searchstr", searchstr.getText().toString()));
            try {
                JSONArray data = new JSONArray(getJSONUrl(url,params));
                final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> map;
                for(int i = 0; i < data.length(); i++){
                    JSONObject c = data.getJSONObject(i);
                    map = new HashMap<String, String>();
                    map.put("order",String.valueOf(i + 1));
                    map.put("fulltext_id", c.getString("fulltext_id"));
                    map.put("fulltext_name", c.getString("fulltext_name"));
                    map.put("fulltext_details", c.getString("fulltext_details"));
                    map.put("fulltext_year", c.getString("fulltext_year"));
                    map.put("datetime_add", c.getString("datetime_add"));
                    MyArrList.add(map);
                }
                SimpleAdapter sAdap;
                sAdap = new SimpleAdapter(SearchActivity.this, MyArrList, R.layout.search_column_activity,
                        new String[] {"order","fulltext_name", "fulltext_year"}, new int[] {R.id.Colorder,R.id.Colfulltext_name, R.id.Colfulltext_year});
                listView.setAdapter(sAdap);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> myAdapter, View myView,int position, long mylng) {

                        String fulltext_id = MyArrList.get(position).get("fulltext_id");
                        String fulltext_name = MyArrList.get(position).get("fulltext_name");
                        String fulltext_details = MyArrList.get(position).get("fulltext_details");
                        String fulltext_year = MyArrList.get(position).get("fulltext_year");
                        String datetime_add = MyArrList.get(position).get("datetime_add");
                        Intent newActivity = new Intent(SearchActivity.this,FullTextDetailActivity.class);
                        newActivity.putExtra("idmem", getIntent().getExtras().getString("idmem"));
                        newActivity.putExtra("fulltext_id", fulltext_id);
                        newActivity.putExtra("fulltext_name", fulltext_name);
                        newActivity.putExtra("fulltext_details", fulltext_details);
                        newActivity.putExtra("fulltext_year", fulltext_year);
                        newActivity.putExtra("datetime_add", datetime_add);
                        startActivity(newActivity);

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getJSONUrl(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}