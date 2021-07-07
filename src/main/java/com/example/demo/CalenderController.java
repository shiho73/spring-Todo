package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalenderController {

	//カレンダー
	@RequestMapping("/list/calender")
	public ModelAndView calender(ModelAndView mv) {

		mv.setViewName("calender");
		return mv;
	}

}
