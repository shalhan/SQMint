package com.example.shalhan4.sqmint.ui.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.shalhan4.sqmint.ui.job.Job;
import com.example.shalhan4.sqmint.ui.job.JobPresenter;
import com.example.shalhan4.sqmint.ui.job.JobView;
import com.example.shalhan4.sqmint.ui.user.UserPresenter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shalhan4 on 10/1/2017.
 */

public class ServerPresenter {
    ServerView mServerView;
    SharedPreferences sharedPreferences;
    Context context;
    String ipAddress, username, password;

    //SharedPreferences
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String TOKEN_TYPE = "TOKEN_TYPE";
    public static final String ADMIN_ID = "ADMIN_ID";

    public ServerPresenter(ServerView serverView)
    {
        this.mServerView = serverView;
    }

    public String getAccessToken()
    {
        return this.sharedPreferences.getString(TOKEN_TYPE, null) + " " + this.sharedPreferences.getString(ACCESS_TOKEN, null);
//        Log.i("TOKEN TYPE", this.sharedPreferences.getString(ACCESS_TOKEN, null));
    }

    public String getAdminId(){
        return this.sharedPreferences.getString(ADMIN_ID, null);
    }

    public void setServerContext(Context context) {
        this.context = context;
    }

    public void startApi()
    {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        new SQMintApi().execute("http://192.168.43.13:53293/api/server/" + this.getAdminId()); //laptop shalhan koneksi kosan
        Log.i("ADMIN ID ==> ", this.getAdminId());
    }

    public void deleteServer(int id)
    {
        Log.i("SERVER ID ", id + "");
        new SQMintApiDelete().execute("http://192.168.43.13:53293/api/server/remove/" + id + "/" + this.getAdminId());
    }

    public void addServer(String ipAddress, String username, String password)
    {
        this.ipAddress = ipAddress;
        this.username = username;
        this.password = password;
        if(this.username.equals("") || this.ipAddress.equals("") || this.password.equals(""));
        else
            new SQMintApiPost().execute("http://192.168.43.13:53293/api/server/connect");
    }

    public class SQMintApi extends AsyncTask<String, String, List<Server> > {

        protected List<Server> doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", getAccessToken());
                Log.i("ACCESS TOKEN ", getAccessToken());
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String result = stringBuilder.toString();
                    List<Server> serverList = new ArrayList<>();
                    if(!result.equals(""))
                    {
                        Log.i("SERVER LIST => ", result);
                        JSONArray jobArray = new JSONArray(result);
                        int length = jobArray.length();
                        for(int i = 0; i < length; i++)
                        {
                            JSONObject jobObject = jobArray.getJSONObject(i);
                            Server servers = new Server();
                            servers.setId(jobObject.getInt("id"));
                            servers.setIpAddress(jobObject.getString("ipAddress"));
                            servers.setUsername(jobObject.getString("userId"));
                            serverList.add(servers);
                        }

                    }
                    return serverList;

                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(List<Server> response) {
            super.onPostExecute(response);
            if(response != null)
                mServerView.setServerListAdapter(response);
        }
    }
    public class SQMintApiDelete extends AsyncTask<String, String, String > {

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded" );
                urlConnection.setRequestProperty("Authorization", getAccessToken());
                urlConnection.setRequestMethod("DELETE");
                if(urlConnection.getResponseCode() == 204)
                    return "SUCCESS";
                else
                    return "FAILED";


            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response.equals("SUCCESS"))
            {
                Log.i("DELETE SUKSES ==>", response);
                mServerView.deleteSuccess();
            }
            else
            {
                Log.i("DELETE SUKSES ==>", response);
                mServerView.deleteFailed();
            }
            Log.i("DELETE SERVER LIST ==>", response);
        }
    }
    public class SQMintApiPost extends AsyncTask<String, String, String > {

        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("charset", "utf-8");
                urlConnection.setRequestProperty("Authorization", getAccessToken());
                urlConnection.setDoInput(true);
                try
                {
                    String userAuth = "userId=" + URLEncoder.encode(username, "UTF-8") +
                            "&password=" + URLEncoder.encode(password, "UTF-8") +
                            "&ipaddress=" + URLEncoder.encode(ipAddress, "UTF-8");
                    Log.i("START OUTPUT STREAM", username + " " + password + " " + ipAddress + " " + userAuth);

                    DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
                    os.writeBytes(userAuth);
                    Log.i("HARUSNYA 204", urlConnection.getResponseCode() + "");
                    os.flush();
                    os.close ();

                    int code = 204;
                    String response;
                    if(urlConnection.getResponseCode() == 204)
                    {
                        response = "SUCCESS";
                    }
                    else
                    {
                        response = "FAILED";
                    }

                    Log.i("INI HASILNYA", response + urlConnection.getResponseCode());

                    return response;
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if(response.equals("SUCCESS"))
            {
                Log.i("ADD SUKSES ==>", response);
                mServerView.addServerSuccess();
            }
            else
            {
                Log.i("ADD SUKSES ==>", response);
                mServerView.addServerFailed();
            }
            Log.i("ADD SERVER LIST ==>", response);
        }
    }


}