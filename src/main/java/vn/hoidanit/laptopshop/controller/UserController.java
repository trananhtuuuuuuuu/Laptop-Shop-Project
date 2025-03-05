package vn.hoidanit.laptopshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;
import vn.hoidanit.laptopshop.service.UserService;



@Controller
public class UserController{ 

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping("/")
  public String getHomePage(Model model) {
    model.addAttribute("eric", "test");// eric là tên access bên view, test là biến chứa value của nó
    return "hello";
  }

  @RequestMapping("/admin/user")
  public String getAdminUserPage(Model model) {
    model.addAttribute("newUser", new User());
    return "admin/user/create";
  }


  @RequestMapping(value = "/admin/user/create", method = RequestMethod.POST)
  public String createAdminUserPage(Model model, @ModelAttribute("newUser") User userGot) {
    User user = this.userService.handleSaveUser(userGot);
    System.out.println("run here" + user);
    return "hello";
  }
  

}



