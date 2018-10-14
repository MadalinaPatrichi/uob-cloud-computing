package uob_todo.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uob_todo.api.exceptions.BadRequestException;
import uob_todo.api.exceptions.NotFoundException;

@RestController
@RequestMapping(path = "/api/todos")
public class TodoController {

    private final TodoRepository todoSource;

    /*
    This function runs a cpu/memory penalty in order to generate some fake load. This is used to drive metrics
    examples and demonstrate auto-scaling of pods/containers.
     */
    private void runPenaltyIfRequired() {
        SCryptPasswordEncoder encoder = new SCryptPasswordEncoder(
                1 << 14,
                8,
                1,
                32,
                64
        );
        encoder.encode("");
    }

    @Autowired
    public TodoController(TodoRepository todoSource){
        this.todoSource = todoSource;
    }

    @GetMapping("/{id}")
    public TodoItem getTodo(@PathVariable("id") Long id) throws Exception {
        this.runPenaltyIfRequired();
        return todoSource.findById(id).orElseThrow(() -> new NotFoundException("item not found"));
    }

    @GetMapping()
    public Iterable<TodoItem> listTodos() {
        this.runPenaltyIfRequired();
        return todoSource.findAll();
    }

    @PostMapping()
    public TodoItem createTodo(@RequestBody TodoItem item) throws Exception {
        this.runPenaltyIfRequired();
        if (item.getTitle().equals("")) {
            throw new BadRequestException("empty 'title'");
        }
        return todoSource.save(item);
    }

    @PutMapping("/{id}")
    public TodoItem updateTodo(@PathVariable("id") Long id, @RequestBody TodoItem item) throws Exception {
        this.runPenaltyIfRequired();
        if (item.getTitle().equals("")) {
            throw new BadRequestException("empty 'title'");
        }
        TodoItem existingItem = todoSource.findById(id).orElseThrow(() -> new NotFoundException("item not found"));
        existingItem.setCompleted(item.isCompleted());
        existingItem.setTitle(item.getTitle());
        existingItem = todoSource.save(existingItem);
        return existingItem;
    }

}
