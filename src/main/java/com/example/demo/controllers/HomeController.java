package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class HomeController {

  @GetMapping("/c1")
  public ModelAndView index() {
    ModelAndView modelAndView = new ModelAndView("index");
    return modelAndView;
  }

  // @GetMapping("/config")
  // public ModelAndView config() {
  //     Map<String, String> config = ConfigReader.getAllProperties();
  //     ModelAndView modelAndView = new ModelAndView("config");
  //     modelAndView.addObject("config", config);
  //     return modelAndView;
  // }
}