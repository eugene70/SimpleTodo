package eugene.todo.api.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import eugene.todo.vo.TodoVo;

@Service
public class TodoApiService {
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, String> hashOperations;

	@Resource(name="redisTemplate")
	private ZSetOperations<String, String> zSetOperations;
	
	SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private TodoDao dao;

	@PostConstruct
	public void init() {

		// 모든 레코드를 삭제
		getTodoList(0, 0).forEach(todo->remove(todo.getId()));
		
		// lastId를 "0"로 세팅
		resetId();
		
		saveTodo("집안일", "");
		saveTodo("빨래", "1");
		saveTodo("청소", "2");
		saveTodo("방청소", "3");
	}
	
	public void resetId() {
		dao.resetId();
	}
	
	public TodoVo saveTodo(String name, String refs) {
		TodoVo todo = new TodoVo();
		todo.setName(name);
		Arrays.asList(refs.split(",")).stream()
		  .map(ref -> ref.trim()) // 공백제거 
		  .forEach(ref -> {todo.addRef(ref);});
		return saveTodo(todo);
	}
	
	/**
	 * 할일을 저장한다.
	 * id가 없으면 새로 생성한다.
	 * @param vo
	 * @return
	 */
	public TodoVo saveTodo(TodoVo vo) {
		if (vo.getId() == null) {
			// id 값이 없으면 새로운 todo로 등록한다.
			return dao.insert(vo);
		}
		vo.setRefs(
			vo.getRefs().stream()
			            .filter(ref->!ref.equals(vo.getId())) // 자기 자신은 참조할 수 없다.
			            .filter(ref->existsTodo(ref)) // 존재하는 할일만 참조할 수 있다.
			            .collect(Collectors.toList())
		); // 참조목록 중 일부를 필터링한다.
		return dao.update(vo);
	}
	
	/**
	 * 할일 하나를 id로 조회한다.
	 * @param id
	 * @return
	 */
	public TodoVo getTodo(String id) {
		return dao.select(id);
	}
	
	/**
	 * 범위를 지정하여 할일 목록을 조회한다.
	 * @param offset 시작점 
	 * @param limit 조회건수 
	 * @return 할일 목록 
	 */
	public List<TodoVo> getTodoList(long offset, long limit) {
		return dao.select(offset, limit);
	}

	/**
	 * 할일 하나를 id로 삭제한다.
	 * @param id
	 */
	public void remove(String id) {
		dao.delete(id);
	}

	/**
	 * 할일을 종료시킨다.
	 * 이 때 딸린 할일의 종료여부를 체크한다.
	 * 딸린 할일이 종료되지 않았다면 이 할일도 종료할 수 없다.
	 * @param vo
	 * @return
	 */
	public String completeTodo(TodoVo vo) {
		if (vo.isComplete() && vo.getSubs() != null && 
			vo.getSubs().stream().filter(a->!getTodo(a).isComplete()).count() > 0) {
			return "연관된 할일이 모두 완료되지 않았으므로 이 일도 완료할 수 없습니다. \n완료되지 않은 딸린 할일: " + 
					vo.getSubs().stream().filter(a->!getTodo(a).isComplete()).collect(Collectors.joining(", "));
		}
		dao.complete(vo.getId(), vo.isComplete());
		return "OK";
	}
	
	/**
	 * ID로 할일이 존재 여부를 체크
	 * @param id
	 * @return
	 */
	public boolean existsTodo(String id) {
		return dao.exists(id);
	}

	/**
	 * 할일의 전체 갯수를 구한다.
	 * @return
	 */
	public Long count() {
		return dao.count();
	}


}
