package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FileUploadMenu extends Menu {

    private static final int startKey = 1;

    public FileUploadMenu() {
        super(startKey);
    }

    @Override
    public void show() {
        System.out.println("File upload menu (1)");
    }
    
    @Override
    public Map<String, String> getDataFromUser(Scanner scan) {
        Map<String, String> data = new HashMap<>();

        String caption = "";
        String date = "";
        String filePath = "";

        scan.reset();
        do {
            System.out.print("Please enter a caption: ");
            caption = scan.nextLine();
            data.put("caption", caption);
        } while (!Validator.isValidCaption(caption));

        do {
            System.out.print("Please enter a date: ");
            date = scan.next();
            data.put("date", date);
        } while (!Validator.isValidDate(date));

        do {
            System.out.print("Please enter the file path: ");
            filePath = scan.next();
            data.put("filePath", filePath);
        } while (!Validator.isValidFilePath(filePath));

        return data;
    }

    @Override
    public void buildRequest(Map<String, String> dataFromUser) {
        String filePath = dataFromUser.get("filePath");
        String caption = dataFromUser.get("caption");
        String date = dataFromUser.get("date");
        setRequestString(HttpRequestBuilder.buildMultipartRequest(filePath, caption, date));
    }

    @Override
    public void executeRequest(OutputStream out) {
        try {
            String requestString = getRequestString();
            byte[] requestBytes = new byte[requestString.length()];
            for (int i = 0; i < requestString.length(); i++) {
                requestBytes[i] = (byte) requestString.charAt(i);
            }
            out.write(requestBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleResponse(BufferedReader in) {
        String fileName = null;
        System.out.println("File names returned from the server: ");
        try {
            // move until we reach the body of the response
            while (in.readLine() != "\r\n\r\n" && !in.readLine().isEmpty());
            // read the body of the response
            while ((fileName = in.readLine()) != null) {
                System.out.println(fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}