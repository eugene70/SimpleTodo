package eugene.todo.web.control;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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
			           @RequestParam(defaultValue="4") int limit) {
		logger.info("home get page={}, limit={}", page, limit);
		
		// page와 limit으로 조회해 올 offset을 계산
		int offset = page < 2 ? 0 : (page - 1) * limit;
		
		TodoVo[] todos = service.getTodos(offset, limit);
		
		long count = service.getTodoCount();

		model.addAttribute("currentPage", page);
		model.addAttribute("limit", limit);
		model.addAttribute("todos", todos);
		model.addAttribute("todoCount", count);
		model.addAttribute("pages", getPages(count, limit));
		return "home";
	}

	/**
	 * 할일 저장(새 할일 or 수정)
	 * @param model
	 * @param todo 할일 
	 * @param page 현재 페이지 
	 * @param limit 페이징하는 단위
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String post(Model model,
					   @Valid @ModelAttribute TodoVo todo,
	                   @RequestParam(defaultValue="1") int page,
			           @RequestParam(defaultValue="4") int limit
			           ) {
		logger.info("home post todo={}, page={}, limit={}", todo, page, limit);
		
		service.post(todo);
		
		int offset = page < 2 ? 0 : (page - 1) * limit;
		TodoVo[] todos = service.getTodos(offset, limit);

		long count = service.getTodoCount();

		model.addAttribute("currentPage", page);
		model.addAttribute("limit", limit);
		model.addAttribute("todos", todos);
		model.addAttribute("todoCount", count);
		model.addAttribute("pages", getPages(count, limit));
		return "home";
	}
	
	/**
	 * 할일목록 화면의 페이징에 사용할 페이지 리스트를 작성한다.
	 * TODO 페이지가 아주 많을 경우 일부만 노출되게 개선 필요 
	 * @param count
	 * @param limit
	 * @return
	 */
	private List<Integer> getPages(long count, int limit) {
		return	Stream.iterate(1, n -> n+1)
				      .limit((count + limit - 1) / limit)
				      .collect(Collectors.toList());		
	}

}
