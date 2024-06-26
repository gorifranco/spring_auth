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
import com.example.demo.models.GeneralConfig;
import com.example.demo.models.PoolConfig;
import com.example.demo.services.CryptService;
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
      logger.warn("No s'han pogut carregar els logs de l'aplicacio");
      model.addAttribute("logs", "No s'han pogut carregar els logs de l'aplicacio");
    }
    return "index";
  }

  @GetMapping("/configuraPool/{id}")
  public ModelAndView config(@PathVariable(value = "id") int id) {

    ModelAndView modelAndView = new ModelAndView("config");
    modelAndView.addObject("id", id);

    if (id >= 0 && id < MainService.poolSize()) {
      PoolConfig pc = MainService.getPool(id).getDatabaseConfig();
      pc.setDdbb_in_password(CryptService.decrypt(pc.getDdbb_in_password()));
      pc.setDdbb_out_password(CryptService.decrypt(pc.getDdbb_out_password()));
      modelAndView.addObject("config", pc);
    }

    return modelAndView;
  }

  @SuppressWarnings("unchecked")
  @PostMapping("/configuracioGeneral")
  public RedirectView canviaConfiguracio(@ModelAttribute GeneralConfig generalConfig,
      RedirectAttributes redirectAttributes) {

    ObjectMapper oMapper = new ObjectMapper();

    generalConfig.setMail_password(CryptService.encrypt(generalConfig.getMail_password()));

    Map<String, String> updates = oMapper.convertValue(generalConfig,
        Map.class);

    ConfigManager.updateProperties(updates);

    Map<String, String> currentConfig = ConfigManager.getAllProperties();
    redirectAttributes.addFlashAttribute("config", currentConfig);

    RedirectView rv = new RedirectView();
    rv.setContextRelative(true);
    rv.setUrl("/configuracioGeneral?okey=true");

    logger.info("Configuracio general canviada");

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

    databaseConfig.setDdbb_in_password(CryptService.encrypt(databaseConfig.getDdbb_in_password()));
    databaseConfig.setDdbb_out_password(CryptService.encrypt(databaseConfig.getDdbb_out_password()));
    MainService.updatePoolConfig(id, databaseConfig);

    redirectAttributes.addFlashAttribute("config", databaseConfig);

    RedirectView rv = new RedirectView();
    rv.setContextRelative(true);
    rv.setUrl("/?okey=true");

    logger.info("Configuracio canviada");

    return rv;
  }

  @ResponseBody
  @GetMapping(value = "run/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public String run(@PathVariable(value = "id") int id) {

    CustomPool pool = MainService.getPool(id);
    if (pool == null) {
      return "La connexio no existeix.";
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

    if (dbconf.getDdbb_in_password() != null)
      dbconf.setDdbb_in_password(CryptService.encrypt(dbconf.getDdbb_in_password()));

    if (dbconf.getDdbb_out_password() != null)
      dbconf.setDdbb_out_password(CryptService.encrypt(dbconf.getDdbb_out_password()));

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