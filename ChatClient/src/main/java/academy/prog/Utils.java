package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Utils {
    private static final String URL = "http://127.0.0.1";
    private static final int PORT = 8080;

    public static String getURL() {
        return URL + ":" + PORT;
    }

    protected static String getLogin(Scanner scanner) {
        String login;
        String choose;
        while (true) {
            System.out.println();
            System.out.println("1. Enter '1' to login");
            System.out.println("2. Enter '2' to register");
            System.out.println("3. Enter '3' to exit");
            choose = scanner.nextLine();
            if (choose.matches("[123]")) {
                switch (choose) {
                    case "1" -> {
                        login = tryAuth(scanner, "/login");
                        if (login != null) {
                            return login;
                        }
                    }
                    case "2" -> tryAuth(scanner, "/reg");
                    case "3" -> {
                        return null;
                    }
                }
            } else {
                System.out.println("Wrong data, try again");
            }
        }
    }

    private static String tryAuth(Scanner scanner, String endPoint) {
        System.out.println("Enter your login: ");
        String login = scanner.nextLine();
        System.out.println("Enter your password: ");
        String pass = scanner.nextLine();
        String answer = null;
        try {
            answer = sendRequest(endPoint, "POST",
                    String.format("login=%s&pass=%s", login, pass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (answer != null) {
            System.out.println(answer);
            if (answer.equals("Success")) {
                return login;
            }
        } else {
            System.out.println("Connection error");
        }

        return null;

    }

    protected static String sendRequest(String endPoint,
                                        String requestMethod) throws IOException {
        return sendRequest(endPoint, requestMethod, null);
    }

    protected static String sendRequest(String endPoint, String requestMethod,
                                        String parameters) throws IOException {
        java.net.URL url = new URL(Utils.getURL() + endPoint);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(requestMethod);
        http.setDoInput(true);

        if (requestMethod.equals("POST")) {
            http.setDoOutput(true);
            http.setRequestProperty("Content-Length",
                    Integer.toString(parameters.getBytes().length));
            try (OutputStream os = http.getOutputStream()) {
                os.write(parameters.getBytes(StandardCharsets.UTF_8));
            }
        }
        http.connect();
        try (InputStream is = http.getInputStream()) {
            byte[] buf = responseBodyToArray(is);
            if (http.getResponseCode() == 200) {
                return new String(buf, StandardCharsets.UTF_8);
            } else {
                return null;
            }
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

    protected static Message makeMessage(String text, String login) throws IOException {
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
        } else if (text.startsWith("/")) {
            doServiceAction(text, login);
            m = null;
        } else {
            m = new Message(login, text);
        }
        return m;
    }

    private static void doServiceAction(String command, String login) throws IOException {
        switch (command) {
            case "/users" -> {
                Gson gson = new GsonBuilder().create();
                Type type = new TypeToken<Map<String, Status>>() {
                }.getType();
                Map<String, Status> users = gson.fromJson(
                        Utils.sendRequest("/users", "GET"), type);
                users.forEach((key, value) -> System.out.print(String.format("%-10s%s%n", key, value)));
            }
            case "/available", "/avail" -> {
                String answer = sendRequest("/status", "POST",
                        String.format("login=%s&status=%s", login, Status.AVAILABLE));
                System.out.println(answer);
            }
            case "/unavailable", "/una" -> {
                String answer = sendRequest("/status", "POST",
                        String.format("login=%s&status=%s", login, Status.UNAVAILABLE));
                System.out.println(answer);
            }
            case "/exit" -> {
                sendRequest("/status", "POST",
                        String.format("login=%s&status=%s", login, Status.OFFLINE));
                System.exit(0);
            }
        }
    }
}
