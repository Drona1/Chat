package academy.prog;

import java.util.ArrayList;
import java.util.List;

public class JsonMessages {
    private final List<Message> list = new ArrayList<>();

    public JsonMessages(List<Message> sourceList, int fromIndex, String login) {
        for (int i = fromIndex; i < sourceList.size(); i++) {
            String currentTo = sourceList.get(i).getTo();
            if (currentTo==null || currentTo.equals(login)||
                    sourceList.get(i).getFrom().equals(login)) {
                list.add(sourceList.get(i));
            }else{
                list.add(null);
            }
        }
    }
}
