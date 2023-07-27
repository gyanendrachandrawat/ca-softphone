package com.consultadd.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class IndexController {

    @RequestMapping(value = "/index")
    public ModelAndView indexPage() {

        return new ModelAndView("index");
    }
}
