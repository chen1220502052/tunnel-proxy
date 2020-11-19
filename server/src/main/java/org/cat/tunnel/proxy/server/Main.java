package org.cat.tunnel.proxy.server;

import org.cat.tunnel.proxy.server.http.HttpServer;

public class Main {

    public static void main(String[] args){
        try {
            new HttpServer().run();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
