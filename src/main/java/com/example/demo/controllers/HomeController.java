package com.example.demo.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.config.ConfigManager;

@Controller
@RequestMapping("/")
public class HomeController {

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping("/config")
  public ModelAndView config() {
      Map<String, String> config = ConfigManager.getAllProperties();
      ModelAndView modelAndView = new ModelAndView("config");
      modelAndView.addObject("config", config);
      return modelAndView;
  }
}