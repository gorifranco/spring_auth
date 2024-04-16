package com.example.demo.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.config.ConfigManager;
import com.example.demo.models.DatabaseConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

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

  @PostMapping("/canviaConfiguracio")
  public RedirectView canviaConfiguracio(@ModelAttribute DatabaseConfig databaseConfig, RedirectAttributes redirectAttributes){
      ObjectMapper oMapper = new ObjectMapper();

      if (databaseConfig.getPeriodically_execution() == null){
          databaseConfig.setPeriodically_execution("no");
      }else{databaseConfig.setPeriodically_execution("yes");}

      if (databaseConfig.getSend_mail() == null){
          databaseConfig.setSend_mail("no");
      }else{databaseConfig.setSend_mail("yes");}

      Map<String, String> updates = oMapper.convertValue(databaseConfig, Map.class);
      ConfigManager.updateProperties(updates);

      Map<String, String> currentConfig = ConfigManager.getAllProperties();
      redirectAttributes.addFlashAttribute("config", currentConfig);

      RedirectView rv = new RedirectView();
      rv.setContextRelative(true);
      rv.setUrl("/config?okey=true");
      return rv;
  }
  
}