package uob_todo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uob_todo.api.TodoItem;
import uob_todo.api.TodoRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TodoRepository todoSource;

    @Test
    public void getAllTodoItems__empty() throws Exception {
        this.mvc.perform(get("/api/todos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }


    @Test
    public void getAllTodoItems__two() throws Exception {

        todoSource.save(new TodoItem("Bop it"));
        todoSource.save(new TodoItem("Twist it"));

        this.mvc.perform(get("/api/todos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"title\":\"Bop it\",\"completed\":false},{\"title\":\"Twist it\",\"completed\":false}]"));
    }

    @Test
    public void getTodo__notfound() throws Exception {
        this.mvc.perform(get("/api/todos/99999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
                .andExpect(content().json("{\"error\":\"item not found\"}"));
    }
}
