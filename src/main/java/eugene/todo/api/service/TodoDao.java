package eugene.todo.api.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import eugene.todo.vo.TodoVo;

@Component
public class TodoDao {

	@Resource(name="redisTemplate") 
	private ValueOperations<String, String> valueOperations;

	@Resource(name="redisTemplate")
	private HashOperations<String, String, String> hashOperations;

	@Resource(name="redisTemplate")
	private ZSetOperations<String, String> zSetOperations;
	
	private final SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String key = "eugene:todos";
	private String keyLastId = key + ":lastId";
	private String keyPrefix = key + ":";
	private String keyPrefixRefs = keyPrefix + "refs:";
	private String keyPrefixSubs = keyPrefix + "subs:";
	
	public void setKey(String newKey) {
		key = newKey;
		keyLastId = key + ":lastId";
		keyPrefix = key + ":";
		keyPrefixRefs = keyPrefix + "refs:";
		keyPrefixSubs = keyPrefix + "subs:";
	}
	
	/**
	 * 새로운 할일 ID를 생성한다.
	 * 동시성 문제 발생 가능성으로 동기화하였다.
	 * @return 생성된 할일 ID
	 */
	public synchronized String generateId() {
		String lastId = valueOperations.get(keyLastId);
		if (lastId == null) {
			valueOperations.set(keyLastId, "1");
			return "1";
		}
		//valueOperations.increment(keyLastId);
		//Serialization 때문에 값 앞에 특수 문자열이 붙어있어
		//Redis에서 숫자로 인식하지 않아 Redis의 INCR 명령을 사용할 수 없음
		
		lastId = String.valueOf(Long.valueOf(lastId) + 1);
		valueOperations.set(keyLastId, lastId);
		return lastId;
	}
	
	public void resetId() {
		valueOperations.set(keyLastId, "0");
	}

	public void removeIdKey() {
		valueOperations.getOperations().delete(keyLastId);
	}
	
	/**
	 * 새로운 todo를 등록한다.
	 * @param vo 등록할 할일 레코드
	 * @return 등록된 할일 레코드 
	 */
	public TodoVo insert(TodoVo vo) {
		if (vo.getId() == null) {
			vo.setId(generateId());
		}
		
		// sorted set에 키값을 보관(todo 목록 관리)
		zSetOperations.add(key, vo.getId(), Long.valueOf(vo.getId()));
		
		// 참조 할일 연결을 설정한다. 
		makeReference(vo.getId(), vo.getRefs());
		
		Date now = new Date();
		vo.setCreDt(dtFormat.format(now));
		vo.setModDt(dtFormat.format(now));
		
 		hashOperations.put(keyPrefix+vo.getId(), "id", vo.getId());
		hashOperations.put(keyPrefix+vo.getId(), "name", vo.getName());
		hashOperations.put(keyPrefix+vo.getId(), "creDt", vo.getCreDt());
		hashOperations.put(keyPrefix+vo.getId(), "modDt", vo.getModDt());
		//hashOperations.put(keyPrefix+vo.getId(), "complete", String.valueOf(vo.isComplete()));
		return vo;
	}
	
	/**
	 * id로 할일 하나를 조회한다.
	 * @param id 할일 ID
	 * @return 할일 레코드 
	 */
	public TodoVo select(String id) {
		TodoVo vo = null;
		Map<String, String> todoMap = hashOperations.entries(keyPrefix+id);
		Set<String> refs, subs;
		
		if (!todoMap.isEmpty()) {
			vo = new TodoVo();
			vo.setId(todoMap.get("id"));
			vo.setName(todoMap.get("name"));
			vo.setCreDt(todoMap.get("creDt"));
			vo.setModDt(todoMap.get("modDt"));
			vo.setComplete(Boolean.valueOf(todoMap.get("complete")));
			
			refs = zSetOperations.range(keyPrefixRefs+id, 0, -1);
			for (String ref: refs) {
				vo.addRef(ref);
			}
			
			subs = zSetOperations.range(keyPrefixSubs+id, 0, -1);
			for (String sub: subs) {
				vo.addSub(sub);
			}			
		}
		return vo;
	}

	/**
	 * 범위를 지정하여 할일 목록을 조회한다.
	 * @param offset 시작점 
	 * @param limit 조회건수 
	 * @return 할일 목록(리스트)
	 */
	public List<TodoVo> select(long offset, long limit) {
		Set<String> todoIdSet = zSetOperations.range(key, offset, limit==0 ? -1 : offset + limit - 1);
		
		List<TodoVo> list = new ArrayList<TodoVo>();
		
		for (String id: todoIdSet) {
			list.add(select(id));
		}
		return list;
	}
	
	/**
	 * 할일을 수정한다.
	 * @param vo 수정할 할일 레코드
	 * @return 수정된 할일 레코드
	 */
	public TodoVo update(TodoVo vo) {
		// 상위 할일에서 하위 할일(현재 할일)로의 연결을 지운다.
		for (String oldRef: zSetOperations.range(keyPrefixRefs + vo.getId(), 0, -1)) {
			zSetOperations.remove(keyPrefixSubs + oldRef, vo.getId());
		}
		// 현 할일에서 상위 할일로의 연결을 모두 지운다.
		zSetOperations.removeRange(keyPrefixRefs + vo.getId(), 0, -1);
		
		// 참조 할일 연결을 새로 설정한다. 
		makeReference(vo.getId(), vo.getRefs());
		
		Date now = new Date();
		vo.setModDt(dtFormat.format(now));
		
 		hashOperations.put(keyPrefix+vo.getId(), "id", vo.getId());
		hashOperations.put(keyPrefix+vo.getId(), "name", vo.getName());
		hashOperations.put(keyPrefix+vo.getId(), "modDt", vo.getModDt());
		//hashOperations.put(keyPrefix+vo.getId(), "complete", String.valueOf(vo.isComplete()));
		return vo;
	}
	
	/**
	 * id로 할일을 삭제한다.
	 * @param id 할일 ID 
	 */
	public void delete(String id) {
		// sorted set에 키값을 삭제(todo목록에서 삭제)
		zSetOperations.remove(key, id);
		
		// 상위 할일에서 하위 할일로의 연결을 지운다.
		for (String oldRef: zSetOperations.range(keyPrefixRefs + id, 0, -1)) {
			zSetOperations.remove(keyPrefixSubs + oldRef, id);
		}

		// 현행 상위 할일로의 연결을 모두 지운다.
		//zSetOperations.removeRange(keyPrefixRefs + id, 0, -1);
		
 		//hashOperations.delete(keyPrefix+id);
 		
 		valueOperations.getOperations().delete(keyPrefix+id);
 		valueOperations.getOperations().delete(keyPrefixRefs+id);
 		valueOperations.getOperations().delete(keyPrefixSubs+id);
	}

	/**
	 * 전체 할일 갯수를 구한다.
	 * @return 할일 갯수 
	 */
	public long count() {
		return zSetOperations.count(key, Double.MIN_VALUE, Double.MAX_VALUE);
	}

	/**
	 * 할일 하나를 완료 또는 완료해제 시킨다.
	 * 완료해제의 경우에는 참조관계의 상위 할일도 해제 시킨다.
	 * @param id 할일 ID 
	 * @param complete 완료 여부, true:완료, false:미완료
	 */
	public void complete(String id, boolean complete) {
		if (!complete) {
			// 완료 해제의 경우 참조 할일도 해제를 하여야 한다.
			select(id).getRefs().forEach((a)->{complete(a, false);});
		}
		hashOperations.put(keyPrefix+id, "complete", String.valueOf(complete));
	}

	/**
	 * 할일의 참조목록을 갱신한다.
	 * 참조 당하는 할일에 대해서도 딸린 할일목록을 관리한다.
	 * @param id 할일 ID
	 * @param refs 참조목록
	 */
	private void makeReference(String id, List<String> refs) {
		refs.stream()
	    	.filter(ref->!id.equals(ref)) // 자기 자신은 참조 할일에서 제거
		    .filter(ref->isSafe(id, ref)) // 참조 적합성 체크
		    .forEach(ref->{
				// sorted set에 키값을 보관(참조 목록 관리)
				zSetOperations.add(keyPrefixRefs + id, ref, Long.valueOf(ref));
				// 상호연결관계를 만들기 위해 상위 할일의 피참조(딸림) 목록도 관리한다. 
				zSetOperations.add(keyPrefixSubs + ref, id, Long.valueOf(id));		
		    });
	}

	/**
	 * 참조 적합성 체크
	 * 존재여부 및 상호 참조 방지
	 * @param ref
	 * @return
	 */
	public boolean isSafe(String id, String ref) {
		return exists(ref) &&
				// 상호참조 방지를 위한 체크 
				!zSetOperations.range(keyPrefixRefs+ref, 0, -1).contains(id);
	}
	
	/**
	 * id에 해당하는 할일이 존재하는지 검사한다.
	 * @param id 할일 ID
	 * @return id가 존재하면 참, 아니면 거짓
	 */
	public boolean exists(String id) {
		return hashOperations.hasKey(keyPrefix+id, "id");
	}
	

}
