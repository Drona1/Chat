package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class Users {
    private static final Users usrList = new Users();

    private final Gson gson;
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, Status> status = new HashMap<>();

    private Users() {
        gson = new GsonBuilder().create();
    }

    public static Users getInstance() {
        return usrList;
    }


    public boolean registerUser(String login, String pass) {
        if (users.containsKey(login)) {
            return false;
        }
        users.put(login, pass);
        return true;
    }

    public boolean setStatus(String login, Status newStatus) {
        if (!checkOnline(login)) {
            return false;
        }
        status.put(login, newStatus);
        return true;
    }

    private void setAvailable(String login) {
        status.put(login, Status.AVAILABLE);
    }

    public boolean checkOnline(String login) {
        if (!status.containsKey(login)) {
            return false;
        }
        return status.get(login) != Status.OFFLINE;
    }

    public boolean authorizeUser(String login, String pass) {
        if (login != null && users.containsKey(login) && users.get(login).equals(pass)) {
            setAvailable(login);
            return true;
        }
        return false;
    }

    private Map<String, Status> getListUsers() {
        Map<String, Status> usersList = new HashMap<>();
        status.forEach((key, value) -> {
                    if (value != Status.OFFLINE) {
                        usersList.put(key, value);
                    }
                }
        );
        return usersList;
    }

    public synchronized String toJSON() {
        return gson.toJson(getListUsers());
    }
}
