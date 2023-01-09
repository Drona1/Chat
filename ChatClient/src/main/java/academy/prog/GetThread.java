package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n;
    private final String login;

    public GetThread(String login) {
        this.login = login;
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                String messages = Utils.sendRequest("/get", "POST",
                        String.format("from=%d&login=%s", n, login));
                printMessage(messages);
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void printMessage(String messages){
        JsonMessages list = gson.fromJson(messages, JsonMessages.class);
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
