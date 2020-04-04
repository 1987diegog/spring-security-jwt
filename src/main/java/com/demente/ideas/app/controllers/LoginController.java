package com.demente.ideas.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout, Model model,
                        Principal principal, RedirectAttributes flash) {

        if(principal != null) {
            flash.addFlashAttribute("info", "Ya inicio sesión");
            // se evita que se inicie la secion varias veces, ya que
            // si principal es != null ya existe un login realizado,
            // por lo tanto, se redirige a la pantalla de inicio.
            return "redirect:/users";
        }

        if(logout != null) {
            model.addAttribute("success", "Ha cerrado sesión correctamente");
        }

        if(error != null) {
            model.addAttribute("error", "Error al iniciar sesion, el usuario o contraseña " +
                    "no es correcto, por favor vuelva a intentarlo");
        }

        return "login";
    }
}
