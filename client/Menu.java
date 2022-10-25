package client;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

import server.UploadServer;

public abstract class Menu {

    private final int startKey; 
    private StringBuilder httpRequestBuilder;

    public static final String VERSION = "HTTP/1.1";
    public static final String HOST = "localhost:" + UploadServer.PORT;
    public static final String BOUNDARY = "12345abcde";

    public Menu(int key) {
        this.startKey = key;
        this.httpRequestBuilder = new StringBuilder();
    }

    public abstract void show();
    public abstract Map<String, String> getDataFromUser(Scanner scan);
    public abstract void buildRequest(Map<String, String> dataFromUser, PrintWriter writer);
    public abstract void handleResponse(BufferedReader in);

    protected int getStartKey() {
        return this.startKey;
    }

    protected StringBuilder getHTTPRequestBuilder() {
        return this.httpRequestBuilder;
    }

}
