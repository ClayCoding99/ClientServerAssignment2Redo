package server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
public class HttpServletResponse {

   private String version = "HTTP/1.1";
   private String status = "200";
   private String statusPhrase = "OK";

   private Map<String, String> headers = new HashMap<>();

   private ByteArrayOutputStream writer = new ByteArrayOutputStream();
   private ByteArrayOutputStream outputStream = null;

   public HttpServletResponse(ByteArrayOutputStream outputStream) {
      this.outputStream = outputStream;
   }

   public ByteArrayOutputStream getOutputStream() {
      return this.outputStream;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public void setContentLength(int contentLength) {
      headers.put("Content-Length", String.valueOf(contentLength));
   }

   public void setContentType(String contentType) {
      headers.put("Content-Type", contentType);
   }

   public void sendRedirect(String route) {
      this.headers.put("Location", "localhost:8000" + route);
   }

   public void addHeader(String key, String value) {
      headers.put(key, value);
   }

   public PrintWriter getWriter() {
      return new PrintWriter(this.writer);
   }

   public void buildResponse() {
      try {
         // build the response head line
         outputStream.write((version + " " + status + " " + statusPhrase + "\r\n").getBytes());

         // build the headers:
         for (Map.Entry<String, String> header : this.headers.entrySet()) {
            outputStream.write((header.getKey() + ": " + header.getValue() + "\r\n").getBytes());
         }

         // separate the head and the body
         outputStream.write("\r\n".getBytes());

         // build the body
         outputStream.write(writer.toByteArray());

      } catch (IOException e) {
         System.out.println("Could not parse response!");
         e.printStackTrace();
      }

   }

}