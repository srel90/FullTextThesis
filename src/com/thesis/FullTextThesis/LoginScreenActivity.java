package com.thesis.FullTextThesis;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 7/6/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginScreenActivity extends Activity {
    static{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.loginscreen_activity);
        final EditText txtUser = (EditText)findViewById(R.id.txtusername);
        final EditText txtPass = (EditText)findViewById(R.id.txtpassword);
        final AlertDialog.Builder ad = new AlertDialog.Builder(this);

        findViewById(R.id.btnlogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://10.0.2.2/thesis_db11/androidChecklogin.php";
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", txtUser.getText().toString()));
                params.add(new BasicNameValuePair("password", txtPass.getText().toString()));
                String resultServer  = getHttpPost(url,params);
                String strStatusID = "0";
                String strMemberID = "0";
                String strMemberName = "";
                String strMemberlName = "";
                String strError = "Unknow Status!";
                JSONObject c;
                try {
                    c = new JSONObject(resultServer);
                    strStatusID = c.getString("StatusID");
                    strMemberID = c.getString("MemberID");
                    strMemberName=c.getString("MemberName");
                    strMemberlName=c.getString("MemberlName");
                    strError = c.getString("Error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(strStatusID.equals("0"))
                {
                    // Dialog
                    ad.setTitle("Error! ");
                    ad.setIcon(android.R.drawable.btn_dialog);
                    ad.setPositiveButton("Close", null);
                    ad.setMessage(strError);
                    ad.show();
                    txtUser.setText("");
                    txtPass.setText("");
                }
                else
                {
                    Intent newActivity = new Intent(LoginScreenActivity.this,MainMenuActivity.class);
                    newActivity.putExtra("idmem", strMemberID);
                    newActivity.putExtra("name", strMemberName);
                    newActivity.putExtra("lname", strMemberlName);
                    startActivity(newActivity);
                    finish();
                }
            }
        });
        findViewById(R.id.btnexit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                moveTaskToBack(true);
            }
        });
    }

    public String getHttpPost(String url,List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}