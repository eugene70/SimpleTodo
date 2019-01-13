/**
 * 
 */
package eugene.todo.api.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import eugene.todo.vo.TodoVo;

/**
 * @author eugene
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoApiServiceTest {

	@Autowired
	TodoApiService service;
	
	private final String firstTodoName = "UnitTest_saveTodo1";
	private final String secondTodoName = "UnitTest_saveTodo2";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service.setKey("test:todos");
		
		// Fixture 테스트를 위해 기본 두건의 할일을 등록

		TodoVo newTodo = new TodoVo();
		newTodo.setName(firstTodoName);
		service.saveTodo(newTodo);
		
		newTodo = new TodoVo();
		newTodo.setName(secondTodoName);
		newTodo.addRef("1");
		service.saveTodo(newTodo);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		service.destroyAll();
	}

	@Test
	public void testSaveNew() {
		// getTodo() 등록한 할일을 조회 
		TodoVo firstTodo = service.getTodo("1");
		assertEquals(firstTodo.getName(), firstTodoName);
		assertEquals(firstTodo.getSubs().get(0), "2");

		TodoVo secondTodo = service.getTodo("2");
		assertEquals(secondTodo.getName(), secondTodoName);
		assertEquals(secondTodo.getRefs().get(0), "1");
	}
	
	@Test
	public void testSave() {
		// getTodo() 등록한 할일을 조회 
		TodoVo firstTodo = service.getTodo("1");
		TodoVo secondTodo = service.getTodo("2");
		secondTodo.setName(firstTodo.getName());
		secondTodo.removeRef("1");
		service.saveTodo(secondTodo);
		firstTodo = service.getTodo("1");
		secondTodo = service.getTodo("2");
		
		assertEquals(firstTodo.getName(), secondTodo.getName());
		assertEquals(0, firstTodo.getSubs().size());
		assertEquals(0, firstTodo.getRefs().size());
	}
	
	@Test
	public void testCrossReference() {
		TodoVo firstTodo = service.getTodo("1");
		firstTodo.addRef("2");
		service.saveTodo(firstTodo);
		firstTodo = service.getTodo("1");
		assertEquals(0, firstTodo.getRefs().size());
	}
	
	@Test
	public void testGetTodo() {
		TodoVo firstTodo = service.getTodo("1");
		assertEquals(firstTodo.getName(), "UnitTest_saveTodo1");
		assertEquals(firstTodo.getSubs().get(0), "2");
	}
	
	@Test
	public void testGetTodoList() {
		List<TodoVo> todos = service.getTodoList(0, 0);
		assertEquals("getTodoList 테스트", todos.size(), 2);
		assertEquals("getTodoList 테스트", todos.get(0).getName(), firstTodoName);
		assertEquals("getTodoList 테스트", todos.get(0).getSubs().get(0), "2");
		assertEquals("getTodoList 테스트", todos.get(1).getName(), secondTodoName);
		assertEquals("getTodoList 테스트", todos.get(1).getRefs().get(0), "1");
	}
	
	@Test
	public void testCount() {
		assertEquals("두건의 할일을 등록한 후 레코드 수 변화 테스트", 2, service.count());
	}
	
	@Test
	public void testRemove() {
		// 등록했던 할일 삭제
		service.remove("1");
		service.remove("2");
		assertEquals("두건의 할일을 삭제한 후 레코드 수 변화 테스트", 0, service.count());
	}
	
	@Test
	public void testComplete() {
		TodoVo firstTodo = service.getTodo("1");
		firstTodo.setComplete(true);
		String msg = service.completeTodo(firstTodo);
		assertNotEquals("할일 완료 처리 테스트", "OK", msg);

		TodoVo secondTodo = service.getTodo("2");
		secondTodo.setComplete(true);
		msg = service.completeTodo(secondTodo);
		assertEquals("할일 완료 처리 테스트", "OK", msg);
		
		msg = service.completeTodo(firstTodo);
		assertEquals("할일 완료 처리 테스트", "OK", msg);
		
		secondTodo.setComplete(false);
		msg = service.completeTodo(secondTodo);
		assertEquals("할일 완료 처리 테스트", "OK", msg);
		
		firstTodo = service.getTodo("1");
		assertFalse("딸린 할일이 완료가 취소되면 참조되는 할일도 같이 취소된다", firstTodo.isComplete());
	}
}
