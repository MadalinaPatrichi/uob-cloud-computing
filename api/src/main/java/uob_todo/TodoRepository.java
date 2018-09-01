package uob_todo;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoRepository extends CrudRepository<ToDoItem, Long> {

    List<ToDoItem> findByDone(boolean done);

}
