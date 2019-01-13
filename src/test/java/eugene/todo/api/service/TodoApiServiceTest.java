/**
 * 
 */
package eugene.todo.api.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
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
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("setUpBeforeClass");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("tearDownAfterClass");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown");
	}

	@Test
	public void saveTodo() {
		
		// 새로운 할일을 등록 
		TodoVo newTodo = new TodoVo();
		newTodo.setName("UnitTest_saveTodo1");
		TodoVo firstTodo = service.saveTodo(newTodo);
		newTodo = new TodoVo();
		newTodo.setName("UnitTest_saveTodo2");
		TodoVo secondTodo = service.saveTodo(newTodo);
		assertNotNull(firstTodo);
		assertNotNull(firstTodo.getId());
		assertNotNull(secondTodo);
		assertNotNull(secondTodo.getId());
		assertNotEquals(firstTodo.getId(), secondTodo.getId());
		assertNotEquals(firstTodo.getName(), secondTodo.getName());
		
		// 등록한 할일을 조회 
		TodoVo getFirstTodo = service.getTodo(firstTodo.getId());
		assertEquals(firstTodo.getName(), getFirstTodo.getName());
		assertNotNull(getFirstTodo.getCreDt());
		assertNotNull(getFirstTodo.getModDt());
		assertFalse(getFirstTodo.isComplete());
		
		// update 테스트
		getFirstTodo.setName("UnitTest_editTodo");
		firstTodo = service.saveTodo(getFirstTodo);
		assertEquals(firstTodo.getName(), getFirstTodo.getName());
		//assertNotEquals(firstTodo.getCreDt(), firstTodo.getModDt());
		
		service.remove(firstTodo.getId());
		service.remove(secondTodo.getId());

	}
	
	@Test	
	public void getTodoList() {
		System.out.println(service.count());
	}
	@Test
	public void count() {
		System.out.println(service.count());
	}

}
