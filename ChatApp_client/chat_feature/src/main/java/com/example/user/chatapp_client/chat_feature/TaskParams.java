package com.example.user.chatapp_client.chat_feature;

import java.net.Socket;

public class TaskParams {
    String msg;
    Socket socket;

    TaskParams(String msg,Socket socket){
        this.msg = msg;
        this.socket = socket;
    }

    Socket getSocket(){
        return socket;
    }

    String getMsg(){
        return msg;
    }
}
