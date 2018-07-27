package com.example.user.chatapp_client.chat_feature;

import android.os.AsyncTask;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.JsonObject;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;


public class MainActivity extends AppCompatActivity {

    public Handler mainHandler;

    public class StartSocket implements Callable<Socket>{
        String ip;
        Integer port;
        Socket socket;

        public StartSocket(String ip, Integer port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public Socket call() {
            try {
                socket = new Socket(ip, port);
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView mTextView = (TextView) findViewById(R.id.text);
        final EditText editText = (EditText) findViewById(R.id.editText);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        String hostName = "192.168.45.158";
        int portNumber = 5555, ok = 0;
        final SocketContainer socketContainer;
        try {
            socketContainer = new SocketContainer(pool.submit(new StartSocket(hostName, portNumber)).get());
            new GetMessage().execute(new PackageGet(socketContainer, mTextView ));
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length()>10) {
                        new SendMessage().execute(new PackageSend(charSequence.toString(), socketContainer));
                        editText.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }
}
class SocketContainer{
    Socket socket;

    public SocketContainer(Socket socket) {
        this.socket = socket;
    }
}

class PackageUI{
    TextView view;
    String string;

    public PackageUI(TextView view, String string) {
        this.view = view;
        this.string = string;
    }
}

class PackageGet {
    SocketContainer soc;
    TextView view;

    public PackageGet(SocketContainer soc, TextView view) {
        this.soc = soc;
        this.view = view;
    }
}

class PackageSend{
    String string;
    SocketContainer socketContainer;

    public PackageSend(String string, SocketContainer socketContainer) {
        this.string = string;
        this.socketContainer = socketContainer;
    }
}


class GetMessage extends AsyncTask<PackageGet, PackageUI, JSONObject> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(PackageGet... params) {
        while (true) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(params[0].soc.socket.getInputStream()));

                //JSONObject object = new JSONObject(String.valueOf(in.read()));
                publishProgress(new PackageUI(params[0].view, in.readLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.println(Log.VERBOSE, "tagpemata", "JUNS AICIA BOSS");
        }
    }

    @Override
    protected void onProgressUpdate(PackageUI... values) {
        super.onProgressUpdate(values);
        values[0].view.setText(values[0].string);
    }
}

class SendMessage extends AsyncTask<PackageSend, Integer, Integer>{
    PrintWriter out;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Integer doInBackground(PackageSend... packageSends) {
        try {
            out = new PrintWriter(packageSends[0].socketContainer.socket.getOutputStream(), true);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user", "android1");
                jsonObject.put("message", packageSends[0].string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            out.println(jsonObject.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}