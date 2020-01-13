package com.example.demo.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Data
@Slf4j
public class IndexController {


    @GetMapping({"/index","/"})
    public String index(Model model){
        String username= SecurityUtils.getSubject().getPrincipal().toString();
        model.addAttribute("username",username);
        return "index";
    }

    @GetMapping("/s/hello")
    @ResponseBody
    public String  hello(){
        return "hello";
    }


   /* @GetMapping(value = "/jwt/sso/login")
    public String ssoUrl(@RequestParam String id_token,String target_link_uri, Model model){
        DingdangUserRetriever retriever = new DingdangUserRetriever(id_token, getPublicKey());
        DingdangUserRetriever.User username;
        try {
            username = retriever.retrieve();
        } catch (Exception e) {
            log.error("Retrieve Username error", e);
            model.addAttribute("error", "Retrieve Username error: " + e.getMessage());
            return "error";
        }
        if (null == username) {
            model.addAttribute("error", "wrong request,not found Username from id_token");
            return "error";
        }
        if (!StringUtils.isEmpty(target_link_uri)){
            return "redirect:"+target_link_uri;
        }
        model.addAttribute("username",username);
        return "redirect:/index";
    }*/
}
