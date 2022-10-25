package client;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.Map;
import java.util.Scanner;

public abstract class Menu {

    private final int startKey; 
    private String requestString;

    public Menu(int key) {
        this.startKey = key;
    }

    public abstract void show();
    public abstract Map<String, String> getDataFromUser(Scanner scan);
    public abstract void buildRequest(Map<String, String> dataFromUser);
    public abstract void executeRequest(OutputStream out);
    public abstract void handleResponse(BufferedReader in);

    protected int getStartKey() {
        return this.startKey;
    }

    protected String getRequestString() {
        return this.requestString;
    }

    protected void setRequestString(String requestString) {
        this.requestString = requestString;
    }

}
