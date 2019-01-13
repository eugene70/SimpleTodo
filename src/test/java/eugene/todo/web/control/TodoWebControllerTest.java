package eugene.todo.web.control;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import eugene.todo.api.service.TodoApiService;
import eugene.todo.web.service.TodoWebService;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoWebController.class)
public class TodoWebControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	TodoWebService todoWebService;

	@Test
	public void getTodo() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}

}
