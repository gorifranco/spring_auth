package com.example.demo.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.example.demo.models.GeneralConfig;
import com.example.demo.models.PoolConfig;
import com.example.demo.services.CustomPool;
import com.example.demo.services.MainService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class HomeController {

  private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

  @GetMapping("/")
  public String index(Model model) {

    if (MainService.poolSize() > 0)
      model.addAttribute("connexions", MainService.getPools());

    try {
      model.addAttribute("logs", CustomLogManager.returnReverseLogHTML());
      model.addAttribute("last_log", CustomLogManager.getLastLog());
    } catch (Exception e) {
      logger.warn("logs", "No s'han pogut carregar els logs de l'aplicació");
      model.addAttribute("logs", "No s'han pogut carregar els logs de l'aplicació");
    }
    return "index";
  }

  @GetMapping("/configuraPool/{id}")
  public ModelAndView config(@PathVariable(value = "id") int id) {

    ModelAndView modelAndView = new ModelAndView("config");
    modelAndView.addObject("id", id);

    if (id >= 0 && id < MainService.poolSize()) {
      modelAndView.addObject("config", MainService.getPool(id).getDatabaseConfig());
    }

    return modelAndView;
  }

  @SuppressWarnings("unchecked")
  @PostMapping("/configuracioGeneral")
  public RedirectView canviaConfiguracio(@ModelAttribute GeneralConfig generalConfig,
      RedirectAttributes redirectAttributes) {

    ObjectMapper oMapper = new ObjectMapper();

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(generalConfig.getMail_password().getBytes(StandardCharsets.UTF_8));

      String encoded = Base64.getEncoder().encodeToString(hash);
      generalConfig.setMail_password(encoded);
    } catch (Exception e) {
      logger.error("No s'ha pogut encriptar la contrassenya" + e.getMessage());
    }
    
    Map<String, String> updates = oMapper.convertValue(generalConfig,
        Map.class);

    ConfigManager.updateProperties(updates);

    Map<String, String> currentConfig = ConfigManager.getAllProperties();
    redirectAttributes.addFlashAttribute("config", currentConfig);

    RedirectView rv = new RedirectView();
    rv.setContextRelative(true);
    rv.setUrl("/configuracioGeneral?okey=true");

    logger.info("Configuració general canviada");

    return rv;
  }

  @GetMapping("/configuracioGeneral")
  public ModelAndView configuracioGeneral() {
    ModelAndView modelAndView = new ModelAndView("generalConfig");
    modelAndView.addObject("config", ConfigManager.getAllProperties());
    return modelAndView;
  }

  @PostMapping("/configuraPool/{id}")
  public RedirectView configuraPool(@ModelAttribute PoolConfig databaseConfig,
      RedirectAttributes redirectAttributes, @PathVariable(value = "id") int id) {

    System.out.println(databaseConfig.toString());

    MainService.updatePoolConfig(id, databaseConfig);

    redirectAttributes.addFlashAttribute("config", databaseConfig);

    RedirectView rv = new RedirectView();
    rv.setContextRelative(true);
    rv.setUrl("/?okey=true");

    logger.info("Configuració canviada");

    return rv;
  }

  @ResponseBody
  @GetMapping(value = "run/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String run(@PathVariable(value = "id") int id) {

    CustomPool pool = MainService.getPool(id);
    if (pool == null) {
      return "La connexió no existeix.";
    }
    return pool.run();
  }

  @ResponseBody
  @GetMapping(value = "stop/{id}")
  public void stop(@PathVariable(value = "id") int id) {

    CustomPool pool = MainService.getPool(id);
    if (pool != null)
      pool.stopService();
  }

  @ResponseBody
  @GetMapping(value = "getLastLogs/{data}")
  public String getLastLogs(@PathVariable(value = "data") String data) {
    try {
      return "{ 'log': '" + CustomLogManager.returnLogHtmlFromTime(data) + "', 'last_log': '"
          + CustomLogManager.getLastLog() + "' }";
    } catch (IOException e) {
      logger.warn("No s'ha pogut llegir l'arxiu de log, " + e.getStackTrace());
      return "Couldn't read log file";
    }
  }

  @PostMapping(value = "new")
  public RedirectView newPool(@ModelAttribute PoolConfig dbconf) {
    MainService.createPool(dbconf);
    RedirectView rv = new RedirectView();
    rv.setUrl("/");
    return rv;

  }

  @GetMapping("eliminarPool/{id}")
  public RedirectView eliminarPool(@PathVariable(value = "id") int id) {
    boolean resultat = MainService.deletePool(id);
    RedirectView rv = new RedirectView();
    rv.setUrl("/?borrat=" + resultat);
    return rv;
  }
}