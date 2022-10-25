package client;

import java.io.File;
import java.io.FileInputStream;

import server.UploadServer;

public class HttpRequestBuilder {

    public static final String VERSION = "HTTP/1.1";
    public static final String HOST = "localhost:" + UploadServer.PORT;
    public static final String BOUNDARY = "12345abcde";

    private HttpRequestBuilder() {}

    public static String buildMultipartRequest(String filePath, String caption, String date) {
        // build the body before hand so we can get the content length
        StringBuilder bodyBuilder = new StringBuilder();
        buildBody(bodyBuilder, filePath, caption, date);

        StringBuilder reqBuilder = new StringBuilder();

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
        
        // body (parameters)
        reqBuilder.append(bodyBuilder);

        return reqBuilder.toString();
    }

    private static void buildBody(StringBuilder bodyBuilder, String filePath, String caption, String date) {
        bodyBuilder.append("--").append(BOUNDARY).append("\r\n");

        // parse the file
        buildFile(bodyBuilder, filePath);

        // parse the caption
        bodyBuilder.append("Content-Disposition: form-data; name=\"caption\"").append("\r\n");
        bodyBuilder.append("\r\n");
        bodyBuilder.append(caption).append("\r\n");
        bodyBuilder.append("--").append(BOUNDARY).append("\r\n");

        // parse the date
        bodyBuilder.append("Content-Disposition: form-data; name=\"date\"").append("\r\n");
        bodyBuilder.append("\r\n");
        bodyBuilder.append(date).append("\r\n");
        bodyBuilder.append("--").append(BOUNDARY).append("--\r\n");
    }

    private static void buildFile(StringBuilder bodyBuilder, String filePath) {
        File f = new File(filePath);
        String fileName = f.getName();
        try {
            // read the file and store it as a string
            FileInputStream fis = new FileInputStream(f);
            String fileAsString = "";
            while (fis.available() != 0) {
                fileAsString += (char)(fis.read() & 0xFF);
            }
            fis.close();

            // add content disposition
            bodyBuilder.append("Content-Disposition: form-data; name=\"fileName\"; filename=\"").append(fileName).append("\"\r\n");
            // add the content type
            buildContentType(bodyBuilder, filePath);

            // add the file string as the body
            bodyBuilder.append(fileAsString).append("\r\n");

            bodyBuilder.append("--").append(BOUNDARY).append("\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void buildContentType(StringBuilder bodyBuilder, String filePath) {
        int index = 0;
        while (filePath.charAt(index) != '.') {
            index++;
        }
        String fileType = filePath.substring(index + 1, filePath.length());
        String contentType = "?";
        switch (fileType) {
            case "png":
                contentType = "image/png";
                break;
            case "jpg":
            case "jpeg":
                contentType = "image/jpeg";
                break;
            case "txt":
                contentType = "file/text";    
                break;    
        }
        bodyBuilder.append("Content-Type: ").append(contentType).append(fileType).append("\r\n\r\n");
    }
    
}
