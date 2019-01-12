package eugene.todo.web.service;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import eugene.todo.vo.TodoVo;

@Service
public class TodoWebService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	RestTemplate restTemplate = new RestTemplate();
	
	public TodoVo[] getTodos(int offset, int limit) {
		TodoVo[] todos = restTemplate.getForObject(
				"http://localhost:8080/api/1.0/todos/?offset="+offset+"&limit="+limit, 
				TodoVo[].class);
        return todos;
	}

	public long getTodoCount() {
		return restTemplate.getForObject("http://localhost:8080/api/1.0/todos/count", Long.class);
	}

	public TodoVo post(String id, String name, String refs) {
		TodoVo vo = new TodoVo();
		vo.setId(id);
		vo.setName(name);
		Arrays.asList(refs.split(",")).stream()
									  .map(ref -> ref.trim()) // 공백제거 
									  .filter(ref -> !ref.equals(id)) // 자기 참조 방지 
									  .forEach(ref -> {vo.addRef(ref);});
        return restTemplate.postForObject("http://localhost:8080/api/1.0/todos/", vo, TodoVo.class);
	}

}
