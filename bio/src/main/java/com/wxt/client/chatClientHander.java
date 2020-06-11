package com.wxt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class chatClientHander implements Runnable{

    private chatClient chatClient;
    public  chatClientHander(chatClient chatClient)
    {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        BufferedReader reader = null;

        try {
            while (true) {
                reader = new BufferedReader(new InputStreamReader(System.in));
                String msg = reader.readLine();
                this.chatClient.send(msg);
                if ("quit".equals(msg)) {
                    break;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
