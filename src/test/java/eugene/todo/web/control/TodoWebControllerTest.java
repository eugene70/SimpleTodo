package eugene.todo.web.control;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class TodoWebControllerTest {

	@Autowired
	MockMvc mockMvc;
	
	@Test
	public void getTest() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk());
	}

	@Test
	public void postTest() throws Exception {
		mockMvc.perform(post("/")).andExpect(status().isBadRequest());
	}

}
