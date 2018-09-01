package uob_todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

@RestController
public class TodoController {

    @Autowired
    private final TodoRepository todoSource;

    @Autowired
    public TodoController(TodoRepository todoSource){
        this.todoSource = todoSource;
    }

    @GetMapping("/todos/{id}")
    public TodoItem getTodo(@PathVariable("id") Long id) throws Exception {
        return todoSource.findById(id).orElseThrow(() -> new NotFoundException("item not found"));
    }

    @GetMapping("/todos")
    public Iterable<TodoItem> listTodos() {
        return todoSource.findAll();
    }

    @PostMapping("/todos")
    public TodoItem createTodo(@RequestBody TodoItem item) {
        return todoSource.save(item);
    }

    @PostMapping("/todos/{id}")
    public TodoItem updateTodo(@PathVariable("id") Long id, @RequestBody TodoItem item) throws Exception {
        TodoItem existingItem = todoSource.findById(id).orElseThrow(() -> new NotFoundException("item not found"));
        existingItem.setCompleted(item.isCompleted());
        existingItem.setTitle(item.getTitle());
        existingItem = todoSource.save(existingItem);
        return existingItem;
    }

}

