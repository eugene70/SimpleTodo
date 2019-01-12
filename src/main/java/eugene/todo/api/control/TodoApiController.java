package eugene.todo.api.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eugene.todo.api.service.TodoApiService;
import eugene.todo.vo.TodoVo;

@RestController
public class TodoApiController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	TodoApiService service;
	
	/**
	 * 서비스 기동 여부 테스트를 위한 API
	 * {"Hello":"World!"} 를 반환한다.
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api")
	public String root() {
		return "{\"Hello\":\"World!\"}";
	}

	/**
	 * 할일 한 건을 생성하는 API
	 * 
	 * @return id
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/api/1.0/todos/")
	public ResponseEntity<TodoVo> 
			postTodo(@RequestBody TodoVo vo) {
		logger.info("API postTodo {}", vo);
		TodoVo resultVo = service.saveTodo(vo);
		if (resultVo == null) {
			return new ResponseEntity<TodoVo>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<TodoVo>(resultVo, HttpStatus.OK);
	}
	
	/**
	 * 할일 한 건을 id로 조회하는 API
	 * @param id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/1.0/todos/{id}")
	public ResponseEntity<TodoVo> 
			getTodo(@PathVariable("id") String id) {
		
		logger.info("API getTodo " + id);
		
		TodoVo vo = service.getTodo(id);
		if (vo == null) {
			return new ResponseEntity<TodoVo>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<TodoVo>(vo, HttpStatus.OK);
	}
	
	/**
	 * 할 일 목록을 조회하는 API
	 * 할일이 id순으로 정렬하여 목록을 제공한다.
	 * @param offset 목록의 부분을 조회하기 위한 오프셋 값(n번째 할일 부터)
	 * @param limit 목록의 부분을 조회하기 위한 제한 값(m건만 조회)
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/1.0/todos/")
	public ResponseEntity<List<TodoVo>> 
			getTodos(@RequestParam long offset,
					 @RequestParam long limit) {
		
		logger.info("API getTodos " + offset + " " + limit);
		
		List<TodoVo> voList = service.getTodoList(offset, limit);
		return new ResponseEntity<List<TodoVo>>(voList, HttpStatus.OK);
	}
	
	/**
	 * 할일의 전체 갯수를 구하는 API
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/api/1.0/todos/count")
	public ResponseEntity<Long> count() {
		
		logger.info("API count");
		
		return new ResponseEntity<Long>(service.count(), HttpStatus.OK);
	}
	/**
	 * 할일 한 건을 수정하는 API
	 * @param id
	 * @return
	 */
	@RequestMapping(method = {RequestMethod.PUT}, value = "/api/1.0/todos/{id}")
	public ResponseEntity<TodoVo> 
			edit(@PathVariable("id") String id,
				 @RequestBody TodoVo vo) {
		logger.info("API edit " + vo);
		vo.setId(id); //ID는 수정할 수 없음
		TodoVo resultVo = service.saveTodo(vo);
		if (resultVo == null) {
			return new ResponseEntity<TodoVo>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<TodoVo>(resultVo, HttpStatus.OK);
	}	
	
	/**
	 * 할일 한 건의 일부 값을 수정하는 API
	 * parameter로 completed 값이 들어오면 완료/취소 처리를 한다.
	 * @return id
	 */
	@RequestMapping(method = RequestMethod.PATCH, value = "/api/1.0/todos/{id}")
	public ResponseEntity<Map<String, Object>> 
			patchTodo(@PathVariable("id") String id,
					  @RequestParam(required=false) String completed
					) {
		logger.info("API patchTodo id={}, completed={}", id, completed);
		Map<String, Object> map = new HashMap<String, Object>();
		String msg = "";
		TodoVo vo = service.getTodo(id);
		if (vo == null) {
			return new ResponseEntity<Map<String, Object>>(HttpStatus.NOT_FOUND);
		}
		if (completed != null) {
			vo.setComplete(Boolean.valueOf(completed));
			msg = service.completeTodo(vo);
			map.put("todo", vo);
			map.put("msg", msg);
		}
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
	

	/**
	 * 할일 한 건을 id로 삭제하는 API
	 * @param id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/api/1.0/todos/{id}")
	public ResponseEntity<String> 
			remove(@PathVariable("id") String id) {
		
		logger.info("API remove " + id);
		
		service.remove(id);
		return new ResponseEntity<String>("OK", HttpStatus.OK);
	}
}
