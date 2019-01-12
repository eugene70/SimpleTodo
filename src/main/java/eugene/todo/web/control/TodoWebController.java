package eugene.todo.web.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import eugene.todo.vo.TodoVo;
import eugene.todo.web.service.TodoWebService;

@Controller
public class TodoWebController {

	@Autowired
	TodoWebService service;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model,
			           @RequestParam(defaultValue="1") int page,
			           @RequestParam(defaultValue="5") int limit) {
		logger.info("home page={}, limit={}", page, limit);
		int offset = page < 2 ? 0 : (page - 1) * limit;
		TodoVo[] todos = service.getTodos(offset, limit);
		
		long count = service.getTodoCount();
		List<String> pages = new ArrayList<String>();;
		for (int i=0; i<(count + limit - 1) / limit; i++) {
			pages.add(String.valueOf(i+1));
		}
		
		model.addAttribute("currentPage", page);
		model.addAttribute("limit", limit);
		model.addAttribute("todos", todos);
		model.addAttribute("todoCount", count);
		model.addAttribute("pages", pages);
		return "home";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String post(Model model,
			           @RequestParam(defaultValue="1") int page,
			           @RequestParam(defaultValue="5") int limit,
			           @RequestParam(required=false) String id,
			           @RequestParam String name,
			           @RequestParam(required=false) String refs) {
		logger.info("post id={}, name={}, refs={}, page={}, limit={}", id, name, refs, page, limit);
		
		service.post(id, name, refs);
		
		int offset = page < 2 ? 0 : (page - 1) * limit;
		TodoVo[] todos = service.getTodos(offset, limit);
		
		long count = service.getTodoCount();
		List<String> pages = new ArrayList<String>();;
		for (int i=0; i<(count + limit - 1) / limit; i++) {
			pages.add(String.valueOf(i+1));
		}
		
		model.addAttribute("currentPage", page);
		model.addAttribute("todos", todos);
		model.addAttribute("todoCount", count);
		model.addAttribute("pages", pages);
		return "home";
	}


}
