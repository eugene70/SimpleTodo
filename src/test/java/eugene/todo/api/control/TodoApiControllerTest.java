package eugene.todo.api.control;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import eugene.todo.api.service.TodoApiService;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoApiController.class)
public class TodoApiControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	TodoApiService todoApiService;

	@Test
	public void getTodo() throws Exception {
		mockMvc.perform(get("/api/1.0/todos/?offset=0&limit=4")
				).andExpect(status().isOk());
	}

}
