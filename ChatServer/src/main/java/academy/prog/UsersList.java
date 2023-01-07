package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashSet;
import java.util.Set;

public class UsersList {
    private static final UsersList usrList = new UsersList();

    private final Gson gson;
    private final Set<String> set = new HashSet<>();

    public static UsersList getInstance() {
        return usrList;
    }

    private UsersList() {
        gson = new GsonBuilder().create();
    }

    public synchronized void add(String user) {
        set.add(user);
    }
    public synchronized String toJSON() {
        return gson.toJson(set);
    }
}
