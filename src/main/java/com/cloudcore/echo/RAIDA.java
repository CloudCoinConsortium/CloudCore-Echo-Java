package com.cloudcore.echo;

/*
  Copyright (c) 2018 Ben Ward, 07/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.time.*;

public class RAIDA {


    // Fields

    public int RAIDANumber;
    public String fullUrl;

    public long msServer; // milliseconds for server
    public String ms = ""; //total milliseconds

    public String lastJsonRaFromServer = null;
    public String lastHtml = "empty";//ticket, fail, error
    public String status = "unknown"; //Unknown, slow or ready
    public String msg = "";//message from server


    // Constructors

    /** Constructor for objects of class RAIDA. */
    public RAIDA(int RAIDANumber) {
        this.RAIDANumber = RAIDANumber;
        switchToRealHost();
    }


    // Methods

    /** Sets the RAIDA to the production server. */
    public void switchToRealHost() {
        this.fullUrl = "https://raida" + RAIDANumber + ".cloudcoin.global/service/";
    }

    /** Sets the RAIDA to the test server. */
    public void switchToFakeHost() {
        this.fullUrl = "https://raida" + RAIDANumber + "-net2.cloudcoin.global/service/";
    }

    /** Pings a RAIDA server, and records the result and ping times. */
    public String echo() {
        String html = "";
        String url = this.fullUrl + "echo";
        long start;

        try {
            html = getHtml(url);
            this.lastJsonRaFromServer = html;

            start = System.nanoTime();

            JSONObject json = new JSONObject(html);
            this.msg = json.getString("message");
            this.msServer = (long) (1000f * Float.valueOf(msg.substring(msg.lastIndexOf('=') + 2)));
        } catch (IOException | JSONException | NumberFormatException e) {
            this.status = "error";
            start = System.nanoTime();
            System.out.println("Error: html response does not contain a valid message response: " + html);
            e.printStackTrace();
        }

        boolean isReady = html.contains("ready");
        this.status = (isReady) ? "ready" : "error";

        long end = System.nanoTime();
        this.ms = "" + new DecimalFormat("####.###").format((end - start) * 0.000001f);

        return this.status;
    }

    /** Connects to a URL and returns the HTML output. */
    public String getHtml(String url_in) throws IOException {
        int c;
        URL cloudCoinGlobal = new URL(url_in);
        URLConnection conn = cloudCoinGlobal.openConnection();
        conn.setReadTimeout(10000); //set for two seconds
        conn.setReadTimeout(10000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        InputStream input = conn.getInputStream();

        StringBuilder sb = new StringBuilder();

        while (((c = input.read()) != -1)) {
            sb.append((char) c);
        }
        input.close();
        this.lastHtml = sb.toString();
        return sb.toString();
    }
}
