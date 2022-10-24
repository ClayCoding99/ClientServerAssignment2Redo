package server;

import java.net.*;
import java.io.*;

public class UploadServer {

    public static final int PORT = 8000;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Could not listen on port: '" + PORT + "'");
            System.exit(-1);
        }
        System.out.println("now listening on port '" + PORT + "'!");
        while (true) {
	        new Router(serverSocket.accept()).start();
        }
    }

}
