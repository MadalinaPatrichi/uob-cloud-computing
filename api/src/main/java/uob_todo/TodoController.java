package uob_todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TodoController {

    private final TodoStore store;

    @Autowired
    public TodoController(TodoStore store){
        this.store = store;
    }

    @RequestMapping("/todo")
    public Map<String, Object> getAllTodoItems() {
        Map<String, Object> response = new HashMap<>();
        response.put("todo_items", store.getAllItems());
        return response;
    }

}

