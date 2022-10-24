package server;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpServletRequest {

   private InputStream inputStream = null;

   private String method = null;
   private String url = null;
   private String version = null; 

   private String boundary = null;

   private Map<String, String> headers = new HashMap<>();
   private Map<String, String> parameters = new HashMap<>();

   private static final String HEAD_BODY_SEP = "\r\n\r\n";
   private static final String BOUNDARY_PROTOCOL_HYPENS = "--";

   public HttpServletRequest(InputStream inputStream) {
      this.inputStream = inputStream;
      String wholeStream = inputStreamToString(inputStream);
      //System.out.println("This is the whole stream: ");
      System.out.println(wholeStream);
      try {
         // separate the head and body of the request
         String[] headAndBody = separateHeadAndBody(wholeStream);

         // split the head of the request by line
         String[] headSplitByLine = headAndBody[0].split("\r\n");

         // parse the request line 
         String requestLineString = headSplitByLine[0];
         parseRequestLine(requestLineString);

         // parse the headers
         String[] headersSplitByLine = Arrays.copyOfRange(headSplitByLine, 1, headSplitByLine.length);
         parseHeaders(headersSplitByLine);

         // parse the body if there is one
         if (headAndBody.length > 1) {
            // if the boundary is not null, we have form data we need to parse
            if (boundary != null) {
               parseFormDataBody(headAndBody[1]);
            } else {
               parseBody(headAndBody[1]);
            }
         }
      } catch (Exception e) {
         System.out.println("400, failed to parse request!");
      } finally {
         // test to see if everything was parsed properly
         System.out.println("These are the headers:\n");
         for (Map.Entry<String, String> header : headers.entrySet()) {
            System.out.println(header.getKey() + " " + header.getValue());
         }

         System.out.println("\nThese are the parameters:\n");
         for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            System.out.println(parameter.getKey() + " " + parameter.getValue());
         }
      }
   }

   private void parseRequestLine(String requestLine) {
      String[] requestData = requestLine.split(" ");
      this.method = requestData[0];
      this.url = requestData[1];
      this.version = requestData[2];
   }

   private void parseHeaders(String[] headersSplitByLine) {
      for (String header : headersSplitByLine) {
         String key = header.split(":")[0];
         String val = header.split(":")[1];

         // get the boundary if we have form data
         if (key.contains("Content-Type")) {
            if (val.contains("multipart/form-data")) {
               this.boundary = val.split(";")[1].split("=")[1];
               headers.put(key, val.split(";")[0]);
               continue;
            }
         }

         headers.put(key, val);
      }
   }

   private void parseFormDataBody(String bodyString) {
      String[] separatedByBoundary = bodyString.split(BOUNDARY_PROTOCOL_HYPENS + boundary + "\r\n");

      System.out.println("separated BY BOUNDARY:");
      for (String s : separatedByBoundary) {
         System.out.println(s);
      }

      // start at 1 because it separated some whitespace before the first boundary as well
      for (int i = 1; i < separatedByBoundary.length; i++) {

         String currentFormData = separatedByBoundary[i];

         String head = currentFormData.split(HEAD_BODY_SEP)[0];
         String body = currentFormData.split(HEAD_BODY_SEP)[1];

         String[] headSeparatedByLine = head.split("\r\n");
         
         // parse the content disposition
         String contentDispositionString = headSeparatedByLine[0];
         String contentDispositionInfo = contentDispositionString.split(":")[1];
         String[] contentDispositionValues = contentDispositionInfo.split(";");

         // get the key
         String keyWithQuotes = contentDispositionValues[1].split("=")[1];
         String key = keyWithQuotes.substring(1, keyWithQuotes.length() - 1).trim();

         // if content disposition values is greater than 2, then we have a file we need to deal with
         if (contentDispositionValues.length > 2) {
            // ignore 1 because we don't care about storing the key 'content dispositon' and 'form-data'

            // get the file path
            String filePathWithQuotes = contentDispositionValues[2].split("=")[1];
            String filePath = filePathWithQuotes.substring(1, filePathWithQuotes.length() - 1).trim();

            // the next part is the content type (for this assignment, we don't need to figure out what kind of file this is, so we can skip it)

            // map the key to the filePath
            parameters.put(key, filePath);
            // map the filePath to the body
          
            // weird parsing had /r/n attached to the end. Also, the protocol hypens are in the end instead of the front for the last one, so this also gets rid of that problem

            if (i == separatedByBoundary.length - 1) {
               parameters.put(filePath, body.substring(0, body.length() - 2));
            } else {
               parameters.put(filePath, body);
            }
         } else {
            // if we don't have a file, simply map the key and body
            
            // weird parsing had /r/n attached to the end. Also, the protocol hypens are in the end instead of the front for the last one, so this also gets rid of that problem
            if (i == separatedByBoundary.length - 1) {
               parameters.put(key, body.substring(0, body.length() - 2));
            } else {
               parameters.put(key, body);
            }
         }
      }
   }

   private void parseBody(String body) {
      String[] bodyLines = body.split("\r\n");
      for (String line : bodyLines) {
         String[] keyValue = line.split(":");
         parameters.put(keyValue[0], keyValue[1]);
      }
   }

   private String[] separateHeadAndBody(String wholeStream) {
      for (int i = 0; i < wholeStream.length() - HEAD_BODY_SEP.length() + 1; i++) {
         if (wholeStream.substring(i, i + HEAD_BODY_SEP.length()).equals(HEAD_BODY_SEP)) {
            String[] headBodySep = {wholeStream.substring(0, i), wholeStream.substring(i + HEAD_BODY_SEP.length())};
            return headBodySep;
         }
      }
      System.out.println("failed to separate head and body of request!");
      return new String[] {wholeStream};
   }

   private String inputStreamToString(InputStream inputStream) {
      String wholeStream = "";
      try {
         while (inputStream.available() != 0) {
            wholeStream += (char)(inputStream.read() & 0xFF);
         }
      } catch (IOException e) {
         System.out.println("Failed to convert input stream to a string!");
         e.printStackTrace();
      }
      return wholeStream;
   }

   public InputStream getInputStream() {
      return inputStream;
   }

   public String getMethod() {
      return this.method;
   }

   public String getURL() {
      return this.url;
   }

   public String getVersion() {
      return this.version;
   }

   public String getHeader(String key) {
      return headers.get(key);
   }

   public String getParameter(String key) {
      return parameters.get(key);
   }

   public byte[] getImageBytes(String fileName) {
      String image = parameters.get(fileName);
      byte[] imageBytes = new byte[image.length()];
      for (int i = 0; i < image.length(); i++) {
         imageBytes[i] = (byte) image.charAt(i);
      }
      return imageBytes; 
   }

}