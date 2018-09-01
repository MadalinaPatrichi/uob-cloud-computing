package uob_todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TodoController {

    @Autowired
    private final TodoRepository todoSource;

    @Autowired
    public TodoController(TodoRepository todoSource){
        this.todoSource = todoSource;
    }

    @RequestMapping("/todo")
    public Map<String, Object> getAllTodoItems() {
        List<ToDoItem> items = new ArrayList<>();
        todoSource.findAll().forEach(items::add);
        Map<String, Object> response = new HashMap<>();
        response.put("todo_items", items);
        return response;
    }

}

