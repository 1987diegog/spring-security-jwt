package com.demente.ideas.app.controllers;

import com.demente.ideas.app.models.entity.Client;
import com.demente.ideas.app.models.services.IClientService;
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
@SessionAttributes("client")
public class ClientController {

    protected final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private IClientService clientService;

    public ClientController(IClientService clientService) {

    }

    // @ModelAttribute lo utilizamos cuando queremos pasar datos que son comunes a dos o mas metodos
    // handlers del controlador, (por ejemplo si tuvieraos un select de paises). En este
    // caso @ModelAttribute carga title al controlador y podra ser accedido desde cualquier
    // vista (Thymeleaf, HTML, etc)
    @ModelAttribute("title")
    public String title() {
        return "Clients [DementeIdeas]";
    }

    // otra forma de @Secured ->
    // @PreAuthorize("hasRole('ROLE_USER')")
    // @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Secured("ROLE_USER")
    @GetMapping("/client")
    public String getClient(Model model) {
        Client client = clientService.getMockClient();
        model.addAttribute("title", "Profile: " + client.getName());
        model.addAttribute("client", client);
        return "client/profile";
    }

//    @Secured("ROLE_USER")
    @GetMapping(value = {"/clients", "/"})
    public String clientList(Model model, Authentication authentication, HttpServletRequest request) {

        formasDeObtenerRole(authentication, request);

        model.addAttribute("title", "Client list");
        model.addAttribute("clients", clientService.findAll());
        return "client/list";
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
    @GetMapping("/client-form")
    public String crear(Model model) {
        Client client = new Client();
        model.addAttribute("title", "Client form");
        model.addAttribute("client", client);
        return "client/form";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/client-form")
    public String guardar(@Valid Client client, BindingResult result, Model model, RedirectAttributes flash,
                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Client form");
            model.addAttribute("client", client); // puede omitirse ya que se pasa implicitamente cuando
            // da error (binging result) si el attributo tiene el mismo nombre, en este caso "client"
            model.addAttribute("error", "ha ocurrido un error al intentar guardar el usuario.");
            logger.error(result.getAllErrors().get(0));
            return "client/form";
        }
        String message = (client.getId() != null) ? "Usuario editado con exito!" : "Usuario creado con exito!";
        clientService.save(client);
        // eliminara el objeto 'client' de la session
        status.setComplete();
        flash.addFlashAttribute("success", message);
        return "redirect:clients";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/client-form/{id}")
    public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {
        try {
            Client client = null;
            client = clientService.find(id);
            model.addAttribute("title", "Client form edit");
            model.addAttribute("client", client);
            return "client/form";
        } catch (NotFoundException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/clients";
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping(value = "/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        clientService.delete(id);
        flash.addFlashAttribute("success", "Usuario eliminado con exito!");
        return "redirect:/clients";
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
