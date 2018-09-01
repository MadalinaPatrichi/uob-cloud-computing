package uob_todo;

import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<TodoItem, Long> {
}
