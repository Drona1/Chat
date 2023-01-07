package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter your login: ");
            String login = scanner.nextLine();

            Thread th = new Thread(new GetThread(login));
            th.setDaemon(true);
            th.start();

            System.out.println("Enter your message: ");
            while (true) {
                String text = scanner.nextLine();
                if (text.isEmpty()) break;
                Message m = makeMessage(text, login);
                if (m != null) {
                    int res = m.send(Utils.getURL() + "/add");
                    if (res != 200) { // 200 OK
                        System.out.println("HTTP error occurred: " + res);
                        return;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Message makeMessage(String text, String login) {
        Message m;
        if (text.startsWith("@")) {
            int beginString = text.indexOf(" ");
            if (beginString == -1) {
                beginString = text.length();
            }
            String to = text.substring(1, beginString);
            m = new Message(login, to, text.substring(beginString == text.length()
                    ? beginString
                    : beginString + 1));
        } else if (text.equals("/users")) {
            printUsersList();
            m = null;
        } else {
            m = new Message(login, text);
        }
        return m;
    }

    private static void printUsersList() {
        try {
            URL url = new URL(Utils.getURL() + "/users");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setDoInput(true);
            try (InputStream is = http.getInputStream()) {
                byte[] buf = responseBodyToArray(is);
                String strBuf = new String(buf, StandardCharsets.UTF_8);
                Gson gson = new GsonBuilder().create();
                Set<String> set = gson.fromJson(strBuf, HashSet.class);
                System.out.println(set);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected static byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;
        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
