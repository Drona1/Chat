package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n; // /get?from=n
    private String login;

    public GetThread(String login) {
        this.login=login;
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    @Override
    public void run() { // WebSockets
        try {
            while ( ! Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get");
                String parameters = String.format("from=%d&login=%s",n,login);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setDoInput(true);
                http.setRequestProperty("Content-Length", Integer.toString(parameters.getBytes().length));
                try ( OutputStream os = http.getOutputStream()){
                    os.write(parameters.getBytes(StandardCharsets.UTF_8));
                }
                http.connect();
                int cod = http.getResponseCode();
                if (cod==200) {
                    printMessage(http);
                }
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void printMessage(HttpURLConnection http) throws IOException {
        try (InputStream is = http.getInputStream()) {
            byte[] buf = Main.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);

            JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
            if (list != null) {
                for (Message m : list.getList()) {
                    if (m != null) {
                        System.out.println(m);
                    }
                    n++;
                }
            }
        }
    }
}
