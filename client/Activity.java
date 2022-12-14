package client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import server.UploadServer;

public class Activity {

   public static void main(String[] args) throws IOException {
      new Activity().onCreate();
   }
   public Activity() { }

   public void onCreate() {
      try {
         List<Menu> menus = new ArrayList<>();
         menus.add(new FileUploadMenu());
   
         Scanner scan = new Scanner(System.in);
         int userInput = -1;
         do {

            // show menu options
            System.out.println("Menus:");
            for (Menu m : menus) {
               m.show();
            }
      
            // get the menu the user selected and use it
            userInput = scan.nextInt();
            Menu menuToUse = null;
            for (Menu m : menus) {
               if (m.getStartKey() == userInput) {
                  menuToUse = m;
                  break;
               }
            }
      
            // if we have a valid menu, do the stuff
            if (menuToUse != null) {
               // build the request
               menuToUse.buildRequest(menuToUse.getDataFromUser(scan));

               // set up the socket and send the request to the server

               // Assignment 2 port : UploadServer.PORT
               Socket socket = new Socket("localhost", UploadServer.PORT);
               BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
               OutputStream out = socket.getOutputStream();

               menuToUse.executeRequest(out);
               //socket.shutdownOutput();

               menuToUse.handleResponse(in);
               //socket.shutdownInput();

               socket.close();
            } else {
               System.out.println("Invalid menu!");
            }
   
            System.out.println("Do you want to go to another menu? Yes (1) No (0): ");
            userInput = scan.nextInt();   
         } while (userInput != 0);
         scan.close();
         System.out.println("Goodbye.");
      } catch (SocketException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
}
