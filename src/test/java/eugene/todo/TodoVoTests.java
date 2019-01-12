package eugene.todo;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eugene.todo.vo.TodoVo;

@SpringBootTest
public class TodoVoTests {

	@Autowired
	TodoVoRedisRepository todovoRedisRepository;
	
	@Test
	public void newTodo() {
		TodoVo vo = new TodoVo();
		vo.setId("1");
		vo.setName("할일1");
		vo.setCreDt("");
		vo.setModDt("");
		todovoRedisRepository.save(vo);
	}
}
