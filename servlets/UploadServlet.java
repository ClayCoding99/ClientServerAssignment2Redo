package servlets;

import server.HttpServlet;
import server.HttpServletRequest;
import server.HttpServletResponse;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import exceptions.ServerException;

public class UploadServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServerException, IOException {
      PrintWriter writer = response.getWriter();
   
      response.setContentType("text/html");

      writer.write("<!DOCTYPE html>\r\n");
      writer.write("<html>\r\n");
      writer.write("    <head>\r\n");
      writer.write("        <title>File Upload Form</title>\r\n");
      writer.write("    </head>\r\n");
      writer.write("    <body>\r\n");
      writer.write("<h1>Upload file</h1>\r\n");
      writer.write("<form method=\"POST\" action=\"/\" enctype=\"multipart/form-data\">\r\n");
      writer.write("<input type=\"file\" name=\"fileName\"/><br/><br/>\r\n");
      writer.write("Caption: <input type=\"text\" name=\"caption\"<br/><br/>\r\n");
      writer.write("<br />\r\n");
      writer.write("Date: <input type=\"date\" name=\"date\"<br/><br/>\r\n");
      writer.write("<br />\r\n");
      writer.write("<input type=\"submit\" value=\"Submit\"/>\r\n");
      writer.write("</form>\r\n");
      writer.write("</body>\r\n");
      writer.write("</html>\r\n");

      response.setStatus("200");
      writer.close();
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServerException, IOException {
      System.out.println("in do post!");

      String caption = request.getParameter("caption");
      String date = request.getParameter("date");
      String fileName = request.getParameter("fileName");

      System.out.println("date: " + date);
      System.out.println("caption: " + caption);
      System.out.println("fileName: " + fileName);

      // write the image to the file
      byte[] imageBytes = request.getImageBytes(fileName);
      FileOutputStream fos = new FileOutputStream(new File("./images/" + fileName));
      fos.write(imageBytes);
    
      // get the file names in the image file as a json if invoked by CLI or html list if browser
      String listing = null;
      if (request.getHeader("User-Agent").equals(" CLI")) {
         response.setContentType("text/json");
         listing = getSortedListingJSON("./images/");
      } else {
         response.setContentType("text/html");
         listing = getSortedListingHTML("./images/");
      }

      // write the file names to the client
      Writer writer = response.getWriter();
      writer.write(listing);
      writer.close();
      response.setStatus("200");
      response.sendRedirect("/upload");
   }

   private String getSortedListingJSON(String directoryPath) {
      List<String> sortedChld = getSortedFiles(directoryPath);
      String jsonString = "{";
      for (int i = 0; i < sortedChld.size(); i++) {
         jsonString += i + ": " + sortedChld.get(i);
         if (i != sortedChld.size() - 1) {
            jsonString += " ,";
         }
      }
      jsonString += "}";
      return jsonString;
   }

   private String getSortedListingHTML(String directoryPath) {
      List<String> sortedChld = getSortedFiles(directoryPath);
      StringBuilder htmlBuilder = new StringBuilder();

      htmlBuilder.append("<!DOCTYPE html>\r\n");
      htmlBuilder.append("<html>\r\n");
      htmlBuilder.append("<head>\r\n");
      htmlBuilder.append("<title>File listing</title>\r\n");
      htmlBuilder.append("</head>\r\n");
      htmlBuilder.append("<body>\r\n");

      htmlBuilder.append("<ul>\r\n");
      for (String s : sortedChld) {
         htmlBuilder.append("<li>").append(s).append("</li>").append("\r\n");
      }

      htmlBuilder.append("</ul>\r\n");
      htmlBuilder.append("</body>\r\n");
      htmlBuilder.append("</html>\r\n");

      return htmlBuilder.toString();
   }

   private List<String> getSortedFiles(String directoryPath) {
      File directory = new File(directoryPath);
      String[] chld = directory.list();
      List<String> sortedChld = Arrays.asList(chld).stream().sorted((a, b) -> a.toLowerCase().compareTo(b.toLowerCase())).collect(Collectors.toList());
      return sortedChld;
   }

}