package uob_todo;

import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InMemoryTodoStore implements TodoStore {

    @Override
    public List<ToDoItem> getAllItems() {
        List<ToDoItem> allItems = new ArrayList<>();
        allItems.add(new ToDoItem());
        return allItems;
    }
}
