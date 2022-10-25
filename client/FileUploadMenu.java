package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
    public void buildRequest(Map<String, String> dataFromUser, PrintWriter writer) {
        String filePath = dataFromUser.get("filePath");
        String caption = dataFromUser.get("caption");
        String date = dataFromUser.get("date");

        // request line
        writer.append("POST ").append("/ ").append(VERSION).append("\r\n").flush();

        // headers
        writer.append("User-Agent: ").append("CLI").append("\r\n").flush();
        writer.append("Accept: ").append("*/*").append("\r\n").flush();
        writer.append("Host: ").append(HOST).append("\r\n").flush();
        writer.append("Accept-Encoding: gzip, deflate, br").append("\r\n").flush();
        writer.append("Connection: ").append("keep-alive").append("\r\n").flush();
        writer.append("Content-Type: ").append("multipart/form-data; boundary=").append(BOUNDARY).append("\r\n").flush();

        // split body from head
        writer.append("\r\n").flush();
        
        // parameters
        writer.append("--").append(BOUNDARY).append("\r\n").flush();

        File f = new File(filePath);
        String fileName = f.getName();
        try {
            FileInputStream fis = new FileInputStream(f);
            String fileAsString = "";
            while (fis.available() != 0) {
                fileAsString += (char)(fis.read() & 0xFF);
            }

            writer.append("Content-Disposition: form-data; name=\"fileName\"; filename=\"").append(fileName).append("\"\r\n").flush();

            //String fileType = fileName.split(".")[1];
            writer.append("Content-Type: ").append("image/png").append("\r\n\r\n").flush();
            writer.append(fileAsString).append("\r\n").flush();
    
            writer.append("--").append(BOUNDARY).append("\r\n").flush();

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        writer.append("Content-Disposition: form-data; name=\"caption\"").append("\r\n").flush();
        writer.append("\r\n").flush();
        writer.append(caption).append("\r\n").flush();
        writer.append("--").append(BOUNDARY).append("\r\n").flush();

        writer.append("Content-Disposition: form-data; name=\"date\"").append("\r\n").flush();
        writer.append("\r\n").flush();
        writer.append(date).append("\r\n").flush();
        writer.append("--").append(BOUNDARY).append("--\r\n").flush();
        
        writer.close();

        System.out.println("almost done building response");
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