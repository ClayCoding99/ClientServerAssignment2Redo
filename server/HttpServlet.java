package server;

import java.io.IOException;

import exceptions.ServerException;

public abstract class HttpServlet {

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServerException, IOException { 
      return; 
   } 

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServerException, IOException { 
      return; 
   }

}