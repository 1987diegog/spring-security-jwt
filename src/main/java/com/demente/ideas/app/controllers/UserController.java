package com.demente.ideas.app.controllers;

import com.demente.ideas.app.models.entity.User;
import com.demente.ideas.app.models.services.IUserService;
import javassist.NotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;

@Controller
@SessionAttributes("user")
public class UserController {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private IUserService userService;

    public UserController(IUserService userService) {

    }

    // @ModelAttribute lo utilizamos cuando queremos pasar datos que son comunes a dos o mas metodos
    // handlers del controlador, (por ejemplo si tuvieraos un select de paises). En este
    // caso @ModelAttribute carga title al controlador y podra ser accedido desde cualquier
    // vista (Thymeleaf, HTML, etc)
    @ModelAttribute("title")
    public String title() {
        return "Users [DementeIdeas]";
    }

    // otra forma de @Secured ->
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Secured("ROLE_USER")
    @GetMapping("/user")
    public String getUser(Model model) {
        User user = userService.getMockUser();
        model.addAttribute("title", "Profile: " + user.getName());
        model.addAttribute("user", user);
        return "user/profile";
    }

    @Secured("ROLE_USER")
    @GetMapping(value = {"/users", "/"})
    public String userList(Model model, Authentication authentication, HttpServletRequest request) {

        formasDeObtenerRole(authentication, request);

        model.addAttribute("title", "User list");
        model.addAttribute("users", userService.findAll());
        return "user/list";
    }

    /**
     *
     * @param authentication
     * @param request
     */
    private void formasDeObtenerRole(Authentication authentication, HttpServletRequest request) {

        // Primera forma, por inyeccion de dependencias, authentication en el metodo
        if (authentication != null) {
            logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
        }

        // Utilizando forma estatica SecurityContextHolder.getContext().getAuthentication():
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("Usuario autenticado: ".concat(auth.getName()));
        }

        if (hasRole("ROLE_ADMIN")) {
            logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
        } else {
            logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
        }

        // Forma usando SecurityContextHolderAwareRequestWrapper
        SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");
        if (securityContext.isUserInRole("ROLE_ADMIN")) {
            logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
        } else {
            logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
        }

        // Forma usando HttpServletRequest
        if (request.isUserInRole("ROLE_ADMIN")) {
            logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
        } else {
            logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/user-form")
    public String crear(Model model) {
        User user = new User();
        model.addAttribute("title", "User form");
        model.addAttribute("user", user);
        return "user/form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/user-form")
    public String guardar(@Valid User user, BindingResult result, Model model, RedirectAttributes flash,
                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "User form");
            model.addAttribute("user", user); // puede omitirse ya que se pasa implicitamente cuando
            // da error (binging result) si el attributo tiene el mismo nombre, en este caso "user"
            model.addAttribute("error", "ha ocurrido un error al intentar guardar el usuario.");
            logger.error(result.getAllErrors().get(0));
            return "user/form";
        }
        String message = (user.getId() != null) ? "Usuario editado con exito!" : "Usuario creado con exito!";
        userService.save(user);
        // eliminara el objeto 'user' de la session
        status.setComplete();
        flash.addFlashAttribute("success", message);
        return "redirect:users";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/user-form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        try {
            User user = null;
            user = userService.find(id);
            model.addAttribute("title", "User form edit");
            model.addAttribute("user", user);
            return "user/form";
        } catch (NotFoundException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/users";
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        userService.delete(id);
        flash.addFlashAttribute("success", "Usuario eliminado con exito!");
        return "redirect:/users";
    }


    /**
     * @param role
     * @return
     */
    private boolean hasRole(String role) {

        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return false;
        }

        Authentication auth = context.getAuthentication();
        if (auth == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.contains(new SimpleGrantedAuthority(role));

		/*
		 * for(GrantedAuthority authority: authorities) {
			if(role.equals(authority.getAuthority())) {
				logger.info("Hola usuario ".concat(auth.getName()).concat(" tu role es: ".concat(authority.getAuthority())));
				return true;
			}
		}

		return false;
		*/
    }
}
