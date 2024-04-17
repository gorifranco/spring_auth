package com.example.demo.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.config.ConfigManager;
import com.example.demo.logs.CustomLogManager;
import com.example.demo.models.DatabaseConfig;
import com.example.demo.services.MainService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class HomeController {

  private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

  @GetMapping("/")
  public String index(Model model) {
    try{
          model.addAttribute("logs", CustomLogManager.returnReverseLogHTML());
          model.addAttribute("last_log", CustomLogManager.getLastLog());
    }catch(Exception e){
      logger.warn("logs", "No s'han pogut carregar els logs de l'aplicació");
      model.addAttribute("logs", "No s'han pogut carregar els logs de l'aplicació");
    }
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
  public RedirectView canviaConfiguracio(@ModelAttribute DatabaseConfig databaseConfig,
      RedirectAttributes redirectAttributes) {
    ObjectMapper oMapper = new ObjectMapper();

    if (databaseConfig.getPeriodically_execution() == null) {
      databaseConfig.setPeriodically_execution("no");
    } else {
      databaseConfig.setPeriodically_execution("yes");
    }

    if (databaseConfig.getSend_mail() == null) {
      databaseConfig.setSend_mail("no");
    } else {
      databaseConfig.setSend_mail("yes");
    }

    Map<String, String> updates = oMapper.convertValue(databaseConfig, Map.class);
    ConfigManager.updateProperties(updates);

    Map<String, String> currentConfig = ConfigManager.getAllProperties();
    redirectAttributes.addFlashAttribute("config", currentConfig);

    RedirectView rv = new RedirectView();
    rv.setContextRelative(true);
    rv.setUrl("/config?okey=true");

    logger.info("Configuració canviada");

    return rv;
  }

  @PostMapping(value="run", produces = MediaType.APPLICATION_JSON_VALUE)
  public String run(){
    MainService mainService = new MainService();
    try{
      if(ConfigManager.getString("run_periodically").equals("yes")){
        mainService.runPeriodically();

        return "{status: \"success\"," +
        " next_execution: \"" + ConfigManager.getString("next_execution") + "\"}";
    }else{
      mainService.runOnce();
      return "{status: \"success\"," +
              " next_execution: \"no\"}";
    }
    }catch(Exception e){
      logger.warn("Error executant el servei, " + e.getStackTrace());
      return "{error: \"" + e.getMessage() + "\"}";
    }
  }

  @ResponseBody
  @GetMapping(value = "getLastLogs/{data}")
  public String getLastLogs(@PathVariable(value="data") String data){
    try{
      return "{ 'log': + '" + CustomLogManager.returnLogHtmlFromTime(data) + "', 'last_log': '" + CustomLogManager.getLastLog() + "' }";
    }catch(IOException e){
        logger.warn("No s'ha pogut llegir l'arxiu de log, " + e.getStackTrace());
        return "Couldn't read log file";
    }
  }
}