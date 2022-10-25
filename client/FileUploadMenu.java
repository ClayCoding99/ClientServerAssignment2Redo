package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

        System.out.print("Please enter a caption: ");
        String caption = scan.next();
        data.put("caption", caption);

        System.out.print("Please enter a date: ");
        String date = scan.next();
        data.put("date", date);

        System.out.print("Please enter the file path: ");
        String filePath = scan.next();
        data.put("filePath", filePath);

        return data;
    }

    @Override
    public void buildRequest(Map<String, String> dataFromUser) {
        System.out.println("Building Request");
        String filePath = dataFromUser.get("filePath");
        String caption = dataFromUser.get("caption");
        String date = dataFromUser.get("date");

        // build the body before hand so we can get the content length
        StringBuilder bodyBuilder = new StringBuilder();
        buildBody(bodyBuilder, filePath, caption, date);

        StringBuilder reqBuilder = getHTTPRequestBuilder();

        // request line
        reqBuilder.append("POST ").append("/ ").append(VERSION).append("\r\n");

        // headers
        reqBuilder.append("User-Agent: ").append("CLI").append("\r\n");
        reqBuilder.append("Accept: ").append("*/*").append("\r\n");
        reqBuilder.append("Host: ").append(HOST).append("\r\n");
        reqBuilder.append("Accept-Encoding: gzip, deflate, br").append("\r\n");
        reqBuilder.append("Connection: ").append("keep-alive").append("\r\n");
        reqBuilder.append("Content-Type: ").append("multipart/form-data; boundary=").append(BOUNDARY).append("\r\n");
        reqBuilder.append("Content-Length: ").append(bodyBuilder.toString().length()).append("\r\n");

        // split body from head
        reqBuilder.append("\r\n");
        
        // parameters
        reqBuilder.append(bodyBuilder);
        // try {
        //     String requestString = reqBuilder.toString();
        //     byte[] requestBytes = new byte[requestString.length()];
        //     for (int i = 0; i < requestString.length(); i++) {
        //         requestBytes[i] = (byte) requestString.charAt(i);
        //         System.out.print(requestString.charAt(i));
        //     }
        //     // System.out.println(requestBytes);
        //     out.write(requestBytes);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    
    }

    @Override
    public void executeRequest(OutputStream out) {
        try {
            String requestString = getHTTPRequestBuilder().toString();
            byte[] requestBytes = new byte[requestString.length()];
            for (int i = 0; i < requestString.length(); i++) {
                requestBytes[i] = (byte) requestString.charAt(i);
            }
            System.out.println(requestBytes);
            out.write(requestBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildBody(StringBuilder bodyBuilder, String filePath, String caption, String date) {
        bodyBuilder.append("--").append(BOUNDARY).append("\r\n");

        File f = new File(filePath);
        String fileName = f.getName();
        try {
            FileInputStream fis = new FileInputStream(f);
            String fileAsString = "";
            while (fis.available() != 0) {
                fileAsString += (char)(fis.read() & 0xFF);
            }

            bodyBuilder.append("Content-Disposition: form-data; name=\"fileName\"; filename=\"").append(fileName).append("\"\r\n");

            //String fileType = fileName.split(".")[1];
            bodyBuilder.append("Content-Type: ").append("image/png").append("\r\n\r\n");
            bodyBuilder.append(fileAsString).append("\r\n");
    
            bodyBuilder.append("--").append(BOUNDARY).append("\r\n");

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bodyBuilder.append("Content-Disposition: form-data; name=\"caption\"").append("\r\n");
        bodyBuilder.append("\r\n");
        bodyBuilder.append(caption).append("\r\n");
        bodyBuilder.append("--").append(BOUNDARY).append("\r\n");

        bodyBuilder.append("Content-Disposition: form-data; name=\"date\"").append("\r\n");
        bodyBuilder.append("\r\n");
        bodyBuilder.append(date).append("\r\n");
        bodyBuilder.append("--").append(BOUNDARY).append("--\r\n");
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