package edu.wpi.teamc.languageHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

// THIS IS NOT WORKING, FIGURE IT OUT LATER
public class TranslatorAPI {

  public static String translate(String langFrom, String langTo, String text) throws IOException {
    // INSERT YOU URL HERE
    String urlStr =
        "https://script.google.com/macros/s/AKfycby-mvvc0Y0enQ0eIM4QIY-3iwP1DniJgJE5jXv_87nkXLLK4KroykkC2chst486ftNw/exec"
            + "?q="
            + URLEncoder.encode(text, "UTF-8")
            + "&target="
            + langTo
            + "&source="
            + langFrom;
    URL url = new URL(urlStr);
    StringBuilder response = new StringBuilder();
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestProperty("User-Agent", "Mozilla/5.0");
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();
    return response.toString();
  }
}
