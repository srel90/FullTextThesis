package com.thesis.FullTextThesis;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
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

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 7/6/13
 * Time: 7:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryActivity extends Activity {
    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
    ListView listView = null;
    private Handler handler = new Handler();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.history_activity);
        listView = (ListView)findViewById(R.id.listView);
        String idmem = getIntent().getExtras().getString("idmem");
        findViewById(R.id.btnexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }) ;
        String url = "http://10.0.2.2/thesis_db11/androidHistoryDownload.php";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("idmem", idmem));
        try {
            JSONArray data = new JSONArray(getJSONUrl(url,params));
            HashMap<String, String> map;
            for(int i = 0; i < data.length(); i++){
                JSONObject c = data.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("order",String.valueOf(i + 1));
                map.put("filefulltext_id", c.getString("filefulltext_id"));
                map.put("fulltext_id", c.getString("fulltext_id"));
                map.put("filefulltext_name", c.getString("filefulltext_name"));
                map.put("filefulltext_details", c.getString("filefulltext_details"));
                map.put("filefulltext_size", c.getString("filefulltext_size"));
                map.put("datetime_download", c.getString("datetime_download"));
                MyArrList.add(map);
            }
            SimpleAdapter sAdap;
            sAdap = new SimpleAdapter(HistoryActivity.this, MyArrList, R.layout.fulltextdetaillisthistory_activity,
                    new String[] {"order","filefulltext_name", "filefulltext_size","datetime_download"}, new int[] {R.id.Colorder,R.id.Colfilefulltext_name, R.id.Colfullfilefulltext_size,R.id.Coldatetime_download});
            listView.setAdapter(sAdap);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    startDownload(position);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void startDownload(final int position) {

        Runnable runnable = new Runnable() {
            int Status = 0;

            public void run() {

                String urlDownload = "http://10.0.2.2/thesis_db11/filesattach/"+MyArrList.get(position).get("filefulltext_name").toString();
                String InsertHistory = "http://10.0.2.2/thesis_db11/androidInsertHistoryFullTextDetail.php";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("idmem", getIntent().getExtras().getString("idmem")));
                params.add(new BasicNameValuePair("filefulltext_id",MyArrList.get(position).get("filefulltext_id").toString()));
                getJSONUrl(InsertHistory, params);


                int count = 0;
                try {

                    URL url = new URL(urlDownload);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();

                    int lenghtOfFile = conexion.getContentLength();
                    Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                    InputStream input = new BufferedInputStream(url.openStream());

                    // Get File Name from URL
                    String fileName = urlDownload.substring(urlDownload.lastIndexOf('/')+1, urlDownload.length() );
                    File appname = new File(Environment.getExternalStorageDirectory()+"/Download");
                    if(!appname.exists()){
                        appname.mkdir();
                    }
                    OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Download/"+fileName);

                    byte data[] = new byte[1024];
                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        Status = (int)((total*100)/lenghtOfFile);
                        output.write(data, 0, count);

                        // Update ProgressBar
                        handler.post(new Runnable() {
                            public void run() {
                                updateStatus(position,Status);
                            }
                        });

                    }
                    output.flush();
                    output.close();
                    input.close();

                } catch (Exception e) {}


            }
        };
        new Thread(runnable).start();
    }

    private void updateStatus(int index,int Status){

        View v = listView.getChildAt(index - listView.getFirstVisiblePosition());

        // Update ProgressBar
        ProgressBar progress = (ProgressBar)v.findViewById(R.id.progressBar);
        progress.setProgress(Status);

        TextView txtStatus = (TextView)v.findViewById(R.id.ColStatus);
        txtStatus.setPadding(10, 0, 0, 0);
        txtStatus.setText(String.valueOf(Status)+"%");

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