package helloworld;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
/**
 * SpringBoot Hello World
 * @author 15272
 *
 */
@Controller
public class HelloWorld {
	@RequestMapping("/test")
	@ResponseBody
	public Map<String, Object> showHelloworld(){
		Map<String, Object> map = new HashMap<>();
		map.put("msg", "HelloWorld");
		return map;
	}
}
