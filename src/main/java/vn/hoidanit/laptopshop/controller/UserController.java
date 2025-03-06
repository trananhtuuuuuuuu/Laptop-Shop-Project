package vn.hoidanit.laptopshop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
public class UserController{ 

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping("/")
  public String getHomePage(Model model) {
    model.addAttribute("eric", "test");// eric là tên access bên view, test là biến chứa value của nó
    List<User> users = this.userService.getAllUsers();
    System.out.println(users);
    System.out.println("\n\n");
    List<User> usersByEmail = this.userService.getAllUsersByEmail("1@gmail.com");
    System.out.println(usersByEmail);
    return "hello";
  }

  @RequestMapping("/admin/user")
  public String getAdminUserPage(Model model) {
    model.addAttribute("newUser", new User());
    return "admin/user/table-user";
  }


  // @RequestMapping(value = "/admin/user/create")
  // public String createAdminUserPage(Model model, @ModelAttribute("newUser") User userGot) {
  //   User user = this.userService.handleSaveUser(userGot);
  //   System.out.println("run here" + user);
  //   return "admin/user/create";
  // }



  @RequestMapping(value = "/admin/user/create", method = RequestMethod.GET)
  public String createAdminUserPage(Model model, @ModelAttribute("newUser") User userGot) {
    User user = this.userService.handleSaveUser(userGot);
    System.out.println("run here" + user);
    return "admin/user/create";
  }

  



  

}



