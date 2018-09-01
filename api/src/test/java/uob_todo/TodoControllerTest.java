package uob_todo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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
        this.mvc.perform(get("/todo").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"todo_items\": []}"));
    }


    @Test
    public void getAllTodoItems__two() throws Exception {

        todoSource.save(new ToDoItem("Bop it"));
        todoSource.save(new ToDoItem("Twist it"));

        this.mvc.perform(get("/todo").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"todo_items\": [{\"title\":\"Bop it\",\"done\":false},{\"title\":\"Twist it\",\"done\":false}]}"));
    }

}
