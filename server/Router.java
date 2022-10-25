package server;

import java.net.*;

import exceptions.ServerException;

import java.io.*;

public class Router extends Thread {

   private Socket socket = null;
   private Class<?> clazz;

   public Router(Socket socket) {
      super("ServerThread");
      this.socket = socket;
   }

   @Override
   public void run() {
      try {
         InputStream in = socket.getInputStream(); 
         ByteArrayOutputStream baos = new ByteArrayOutputStream(); 

         HttpServletRequest req = new HttpServletRequest(in);
         HttpServletResponse res = new HttpServletResponse(baos);

         // delcare the upload servlet dynamically using reflection
         clazz = Class.forName("servlets.UploadServlet");
         HttpServlet httpServlet = (HttpServlet) clazz.getConstructor().newInstance();
         
         try {
            switch (req.getMethod()) {
               case "POST" :
                  httpServlet.doPost(req, res);
                  break;
               case "GET" :
                  httpServlet.doGet(req, res);
                  break;   
            }
         } catch (ServerException e) {
            System.out.println(e.getMessage());
         }

         OutputStream out = socket.getOutputStream(); 
         res.buildResponse();
         out.write(res.getOutputStream().toByteArray());
         socket.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

}