package com.example.fixit;

import android.widget.Toast;

import java.io.*;
import java.net.*;

public class SocketHandler extends Thread
{
    private Socket socket;
    private InputStreamReader reader;
    private OutputStreamWriter writer;
    private HttpURLConnection con;
    private URL url;

    public SocketHandler()
    {

    }

    @Override
    public void run() {
        super.run();
        try {
            String strUrl = "http://192.168.32.235:5000/api/data";
            url = new URL(strUrl);

            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            String data = "{\"key\": \"Device is connected\"}";
            con.setDoOutput(true);

            writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close();


//            socket = new Socket("DESKTOP-7NU7H4Q", 333);
//            reader = new DataInputStream(socket.getInputStream());
//            String data = reader.readLine();
//            reader.close();
//            socket.close();

        } catch (IOException e) {
            System.out.println("-------------------------------------------------" +
                    "\n-------------------------------------------------" +
                    "\n------------------------------------------" +
                    "\n--------------------------------------------------" + e.getMessage());
        }
    }
}
