package academy.prog;


import java.io.IOException;
import java.util.*;

public class Main {
    private static String login;
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            login = Utils.getLogin(scanner);
            if (login==null){
                return;
            }
            Thread th = new Thread(new GetThread(login));
            th.setDaemon(true);
            th.start();
            System.out.println("Enter your message: ");
            while (true) {
                String text = scanner.nextLine();
                Message m = Utils.makeMessage(text, login);
                if (m != null) {
                    int res = m.send(Utils.getURL() + "/add");
                    if (res != 200) {
                        System.out.println("HTTP error occurred: " + res);
                        return;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
            try {
                Utils.sendRequest("/status", "POST",
                        String.format("login=%s&status=%s", login, Status.OFFLINE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
