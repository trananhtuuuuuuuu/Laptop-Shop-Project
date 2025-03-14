package vn.hoidanit.laptopshop.controller.admin;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.UserService;
import vn.hoidanit.laptopshop.service.UploadService;



@Controller
public class UserController{ 

  private final UserService userService;

  private final UploadService uploadService;
  private final PasswordEncoder passwordEncoder;



  public UserController(UserService userService,
  UploadService uploadService,
  PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.uploadService = uploadService;
    this.passwordEncoder = passwordEncoder;
  }




  // @GetMapping("/")
  // public String getHomePage(Model model) {
  //   model.addAttribute("eric", "test");// eric là tên access bên view, test là biến chứa value của nó
  //   List<User> users = this.userService.getAllUsers();
  //   System.out.println("List users = " + users);
  //   System.out.println("\n\n");
  //   List<User> usersByEmail = this.userService.getAllUsersByEmail("1@gmail.com");
  //   System.out.println("List users with email = 1@gmail.com" + usersByEmail);
  //   return "hello";
  // }




  @GetMapping("admin/user") // RequestMapping mean get data, so default method is GET
  public String getUserPage(Model model,
   @RequestParam("page") Optional<String> pageOptional){


    int page = 1;
    try{ 
      if(pageOptional.isPresent()){ 
        //convert from String to int
        page = Integer.parseInt(pageOptional.get());
      }
      else{
        //page = 1
      }
    } catch(Exception e){
      //page = 1
    }


    Pageable pageable = PageRequest.of(page - 1, 2);
    Page<User> users = this.userService.getAllUsers(pageable);

    List<User> listUsers = users.getContent();
    //System.out.println(users);
    model.addAttribute("users", listUsers);

    model.addAttribute("currentPage", page);

    model.addAttribute("totalPages", users.getTotalPages());
    return "admin/user/show";
   
  }





  @GetMapping("admin/user/{id}") // RequestMapping mean get data, so default method is GET
  public String getUserDetailPage(Model model,
  @PathVariable long id){
    System.out.println("check path id = " + id);
    //model.addAttribute("newUser", new User()); // Hàm tạo User() mặc định của class User trong domain(model)
    User user = this.userService.getUserById(id);
    model.addAttribute("user", user);
    model.addAttribute("id", id);
    
    return "admin/user/detail";
   
  }




  // đây là hàm sau khi nhấn button create user ở
  // góc bên phải của trang admin/user
  @GetMapping("/admin/user/create") // RequestMapping mean get data, so default method is GET
  public String getCreateUserPage(Model model){
    model.addAttribute("newUser", new User()); // Hàm tạo User() mặc định của class User trong domain(model)
    return "admin/user/create";
  }





  // đây là hàm sau khi nhấn nút create để tạo mới một user thì dữ liệu chúng ta nhập vào ở các ô input sẽ được gửi qua hàm này
  // gửi cho ai?, thì nó gửi lên server và để lấy được dữ liệu ta dùng anotation có tên ModelAttribute() tham số truyền vào là tên 
  // modelAttribute 
  // mà ta đã đặt tên ở bên dạng form ở file html (create.jsp) hãy tưởng tượng cái modelAttribute có newUser đó là một object và dữ liệu
  // sau khi nhập trong form đó là các thuộc tín, và để lưu object với các thuộc tính đó thì ta đã tạo class có tên là User với các thuộc
  // tính tương ứng để chứa các dữ liệu đó, còn tên object thể hiện cho class mà chứa dữ liệu sau khi được nhập vào từ người dùng là hoidanit
  // Dữ liệu này sau khi nhập vào thì chúng ta cần phải dùng hệ quản trị cơ sở dữ liệu để lưu lại, chứ không mỗi lần f5 lại là chúng ta sẽ 
  // mất hết dữ liệu
  @PostMapping(value="/admin/user/create") // RequestMapping mean get data, so default method is GET
  public String createUserPage(Model model, 
  @ModelAttribute("newUser") @Valid User userGot,
  BindingResult newUserBindingResult,
   @RequestParam("imageFile") MultipartFile file
   ){
    //  private final ServletContext servletContext;
    //

    // xử lý validate, trong trường hợp nhập vào ô input tạo mói người dùng sai với dữ liệu quy định
    // trong anotation đã thêm vào các biến trong domain.user
    // xử lý tại đây, và cũng sử dụng anotation của java spring
    // Để làm được điều đó, chúng ta cần phải nói với spring rằng là:
    // Tao cần validate đối tượng nào, hàm, method với anotation là #valid
    List<FieldError> errors = newUserBindingResult.getFieldErrors();
    for (FieldError error : errors ) {
        System.out.println (error.getField() + " - " + error.getDefaultMessage());
    }

    if(newUserBindingResult.hasErrors()){
      return "admin/user/create";
    }


    String fileName = this.uploadService.handleSaveUploadFile(file, "avatar");
    String hashPassword = this.passwordEncoder.encode(userGot.getPassword());
    
    userGot.setAvatar(fileName);
    userGot.setPassword(hashPassword);
    

    // save role

    userGot.setRole(this.userService.getRoleByName(userGot.getRole().getName()));

    this.userService.handleSaveUser(userGot);

    System.out.println("File name image = " + fileName);
    return "redirect:/admin/user";
   
  }




  @GetMapping("/admin/user/update/{id}") // RequestMapping mean get data, so default method is GET
  public String getUpdateUser(Model model,
  @PathVariable long id){
    
    User currentUser = this.userService.getUserById(id);

    model.addAttribute("newUser", currentUser);
    return "admin/user/update";
   
  }







  @PostMapping("/admin/user/update") // RequestMapping mean get data, so default method is GET
  public String updateUser(Model model,
  @ModelAttribute("newUser") User userGot){
    //System.out.println(userGot);
    User currentUser = this.userService.getUserById(userGot.getId());
    if(currentUser != null){
      currentUser.setAddress(userGot.getAddress());
      currentUser.setFullName(userGot.getFullName());
      currentUser.setPhone(userGot.getPhone());
      this.userService.handleSaveUser(currentUser);
    }
    
    //model.addAttribute("newUser", currentUser);

    return "redirect:/admin/user";
   
  }







  @GetMapping("admin/user/delete/{id}") // RequestMapping mean get data, so default method is GET
  public String deleteUserDetail(Model model,
  @PathVariable long id){
    
    //model.addAttribute("newUser", new User()); // Hàm tạo User() mặc định của class User trong domain(model)
    
    model.addAttribute("id", id);
    User user = new User();
    user.setId(id);
    model.addAttribute("newUser", user);
    
    return "admin/user/delete";
   
  }







  @PostMapping("admin/user/delete") // RequestMapping mean get data, so default method is GET
  public String postDeleteUserDetail(Model model,
  @ModelAttribute("newUser") User eric){
    
    this.userService.deleteUserById(eric.getId());
    
    return "redirect:/admin/user";
   
  }


  



  

}



