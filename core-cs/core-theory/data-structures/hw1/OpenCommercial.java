/* OpenCommercial.java */

import java.io.*;
import java.net.*;

/**
 * A class that provides a main function to read five lines of a commercial
 * Web page and print them in reverse order, given the name of a company.
 */

class OpenCommercial {

  /**
   * Prompts the user for the name X of a company (a single string), opens
   * the Web site corresponding to www.X.com, and prints the first five lines
   * of the Web page in reverse order.
   * 
   * @param arg is not used.
   * @exception Exception thrown if there are any problems parsing the
   *                      user's input or opening the connection.
   */
  public static void main(String[] arg) throws Exception {

    BufferedReader keyboard;
    String inputLine;
    keyboard = new BufferedReader(new InputStreamReader(System.in));

    try {
      System.out.print("Please enter the name of a company (without spaces): ");
      System.out.flush(); /* Make sure the line is printed immediately. */
      inputLine = keyboard.readLine();

      if ("".equals(inputLine)) {
        throw new IOException("InputLine cannot be ''");
      }

      URI uri = new URI(String.format("http://www.%s.com", inputLine));
      URL url = uri.toURL();

      try {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);

        int responseCode = connection.getResponseCode();

        if (responseCode == 301) {
          System.out.println("Resource changed the location, redirecting..., Response Code: " + responseCode);
          String newLocation = connection.getHeaderField("Location");
          System.out.println("Redirected to: " + newLocation);
          URL newUrl = new URI(newLocation).toURL();
          HttpURLConnection newConnection = (HttpURLConnection) newUrl.openConnection();
          newConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
          newConnection.setConnectTimeout(2000);
          newConnection.setReadTimeout(2000);
          int newResponseCode = newConnection.getResponseCode();
          System.out.println("New Response Code: " + newResponseCode);

          try (BufferedReader br = new BufferedReader(new InputStreamReader(newConnection.getInputStream()))) {
            System.out.println("Reading first few lines from the website:");
            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < 5) {
              System.out.println(line);
              count++;
            }
          }

        } else if (responseCode != 200) {
          throw new IOException("Response code: " + responseCode);
        } else {
          System.out.println("Response Code: " + responseCode);

          try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            System.out.println("Reading first few lines from the website:");
            String line;
            int count = 0;

            while ((line = br.readLine()) != null && count < 5) {
              System.out.println(line);
              count++;
            }
          }
        }

      } catch (IOException e) {
        System.err.println(e.getMessage());
      }

    } catch (IOException | URISyntaxException e) {
      System.err.println(e.getMessage());
    }
  }
}
