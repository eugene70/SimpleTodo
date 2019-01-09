package eugene.todo.api.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import eugene.todo.api.vo.TodoVo;

@Service
public class TodoApiService {
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, String> hashOperations;

	@Resource(name="redisTemplate")
	private ZSetOperations<String, String> zSetOperations;
	
	SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	
	@PostConstruct
	public void init() {
		
		// sorted set에 키값을 보관(todo 목록 관리)
		zSetOperations.add("test:todos", "1", 1);
		zSetOperations.add("test:todos", "2", 2);
		zSetOperations.add("test:todos", "3", 3);
		zSetOperations.add("test:todos", "4", 4);
		
		// sorted set에 키값을 보관(참조 목록 관리)
		zSetOperations.add("test:todos:refs:2", "1", 1);
		zSetOperations.add("test:todos:refs:3", "1", 1);
		zSetOperations.add("test:todos:refs:4", "1", 1);
		zSetOperations.add("test:todos:refs:4", "3", 3);
		
		// sorted set에 키값을 보관(피참조 목록 관리)
		zSetOperations.add("test:todos:subs:1", "2", 2);
		zSetOperations.add("test:todos:subs:1", "3", 3);
		zSetOperations.add("test:todos:subs:1", "4", 4);
		zSetOperations.add("test:todos:subs:3", "4", 4);
		
		hashOperations.put("test:todos:"+1, "id", "1");
		hashOperations.put("test:todos:"+1, "name", "집안일");
		hashOperations.put("test:todos:"+1, "creDt", "2018-04-01 10:00:00");
		hashOperations.put("test:todos:"+1, "modDt", "2018-04-01 13:00:00");
		hashOperations.put("test:todos:"+1, "complete", "false");
		hashOperations.put("test:todos:"+2, "id", "2");
		hashOperations.put("test:todos:"+2, "name", "빨래");
		hashOperations.put("test:todos:"+2, "creDt", "2018-04-01 11:00:00");
		hashOperations.put("test:todos:"+2, "modDt", "2018-04-01 11:00:00");
		hashOperations.put("test:todos:"+2, "complete", "false");
		hashOperations.put("test:todos:"+3, "id", "3");
		hashOperations.put("test:todos:"+3, "name", "청소");
		hashOperations.put("test:todos:"+3, "creDt", "2018-04-01 12:00:00");
		hashOperations.put("test:todos:"+3, "modDt", "2018-04-01 13:00:00");
		hashOperations.put("test:todos:"+3, "complete", "false");
		hashOperations.put("test:todos:"+4, "id", "4");
		hashOperations.put("test:todos:"+4, "name", "방청소");
		hashOperations.put("test:todos:"+4, "creDt", "2018-04-01 12:00:00");
		hashOperations.put("test:todos:"+4, "modDt", "2018-04-01 13:00:00");
		hashOperations.put("test:todos:"+4, "complete", "false");
	}
	
	public TodoVo getTodo(String id) {
		TodoVo vo = null;
		Map<String, String> todoMap = hashOperations.entries("test:todos:"+id);
		Set<String> refs, subs;
		
		if (!todoMap.isEmpty()) {
			vo = new TodoVo();
			vo.setId(todoMap.get("id"));
			vo.setName(todoMap.get("name"));
			vo.setCreDt(todoMap.get("creDt"));
			vo.setModDt(todoMap.get("modDt"));
			vo.setComplete(Boolean.valueOf(todoMap.get("complete")));
			
			refs = zSetOperations.range("test:todos:refs:"+id, 0, -1);
			for (String ref: refs) {
				vo.addRef(ref);
			}
			
			subs = zSetOperations.range("test:todos:subs:"+id, 0, -1);
			for (String sub: subs) {
				vo.addSub(sub);
			}			
		}
		return vo;
	}
	
	public List<TodoVo> getTodoList(long offset, long limit) {
		Set<String> todoIdSet = zSetOperations.range("test:todos", offset, limit==0 ? -1 : offset + limit - 1);
		
		List<TodoVo> list = new ArrayList<TodoVo>();
		
		for (String id: todoIdSet) {
			list.add(getTodo(id));
		}
		return list;
	}
}
