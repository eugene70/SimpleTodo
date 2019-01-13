package eugene.todo.api.control;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import eugene.todo.api.service.TodoApiService;
import eugene.todo.vo.TodoVo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class TodoApiControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	TodoApiService todoApiService;
	
	private final String firstTodoName = "UnitTest_saveTodo1";
	private final String secondTodoName = "UnitTest_saveTodo2";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println("setUp");
		todoApiService.setKey("test:todos");
		
		// Fixture 테스트를 위해 기본 두건의 할일을 등록

		TodoVo newTodo = new TodoVo();
		newTodo.setName(firstTodoName);

		todoApiService.saveTodo(newTodo).toString();
		
		newTodo = new TodoVo();
		newTodo.setName(secondTodoName);
		newTodo.addRef("1");
		todoApiService.saveTodo(newTodo);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()");
		todoApiService.destroyAll();
	}	
	

	@Test
	public void testGetTodo() throws Exception {
		mockMvc.perform(get("/api/1.0/todos/{id}", "1")
			.accept(MediaType.APPLICATION_JSON)
	        .contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void testGetTodos() throws Exception {
		mockMvc.perform(get("/api/1.0/todos/?offset=0&limit=4"))
				.andExpect(status().isOk());
	}

	@Test
	public void testPostTodo() throws Exception {
		mockMvc.perform(post("/api/1.0/todos/")
				).andExpect(status().isBadRequest());
	}

	@Test
	public void testCount() throws Exception {
		mockMvc.perform(get("/api/1.0/todos/count"))
			.andExpect(status().isOk())
			.andExpect(content().string("2"));
	}

	@Test
	public void testEdit() throws Exception {
		mockMvc.perform(put("/api/1.0/todos/1")
				).andExpect(status().isBadRequest());
	}

	@Test
	public void testPatchTodo() throws Exception {
		mockMvc.perform(patch("/api/1.0/todos/1")
				).andExpect(status().isOk());
	}
}
