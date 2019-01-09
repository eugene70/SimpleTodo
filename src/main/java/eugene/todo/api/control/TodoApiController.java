package eugene.todo.api.control;

import java.util.List;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eugene.todo.api.service.TodoApiService;
import eugene.todo.api.vo.TodoVo;

@RestController
public class TodoApiController {

	private final Log log = LogFactory.getLog(TodoApiController.class);
	
	@Autowired
	TodoApiService service;
	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	public String root() {
		return "{\"Hello\":\"World!\"}";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/api/todos/{id}")
	public ResponseEntity<TodoVo> 
			getTodo(@PathVariable("id") String id) {
		
		log.info("getTodo " + id);
		
		TodoVo vo = service.getTodo(id);
		if (vo == null) {
			return new ResponseEntity<TodoVo>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<TodoVo>(vo, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/api/todos/")
	public ResponseEntity<List<TodoVo>> 
			getTodos(@RequestParam long offset,
					 @RequestParam long limit) {
		
		log.info("getTodos " + offset + " " + limit);
		
		List<TodoVo> voList = service.getTodoList(offset, limit);
		return new ResponseEntity<List<TodoVo>>(voList, HttpStatus.OK);
	}
}
