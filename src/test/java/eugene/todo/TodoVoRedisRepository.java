package eugene.todo;

import org.springframework.data.repository.CrudRepository;

import eugene.todo.vo.TodoVo;

public interface TodoVoRedisRepository extends CrudRepository<TodoVo, String> {

}
