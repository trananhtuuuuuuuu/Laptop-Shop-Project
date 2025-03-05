package vn.hoidanit.laptopshop;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
  @GetMapping("/")
  public String index(){
    return "Hello World with Anh Tu";
  }


  @GetMapping("/user")
  public String userPage(){
    return "This is page user access";
  }

  @GetMapping("/admin")
  public String adminPage(){
    return "This is page admin access";
  }
}






