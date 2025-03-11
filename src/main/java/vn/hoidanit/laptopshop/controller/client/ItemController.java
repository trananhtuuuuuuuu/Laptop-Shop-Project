package vn.hoidanit.laptopshop.controller.client;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;





@Controller
public class ItemController {

  private final ProductService productService;
  
  public ItemController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping("product/{id}")
  public String getProductPage(Model model, @PathVariable long id) {
    Product product = this.productService.getProductById(id);
    model.addAttribute("product", product);
    model.addAttribute("id", id);
    return "client/product/detail";
  }

  @PostMapping("/add-product-to-cart/{id}")
  public String addProductToCart(
    @PathVariable long id,
    HttpServletRequest request) {
      HttpSession session = request.getSession(false);
      Long productId = id;
      String email = (String)session.getAttribute("email");
      this.productService.handleAddProductToCart(email, productId, session);
      
      return "redirect:/";
  }
  

  @GetMapping("/cart")
  public String getCartPage(Model model,
  HttpServletRequest request) {
    User currentUser = new User(); // Tạo mới một user để lưu user hiện tại đang login
    HttpSession session = request.getSession(false); // request lên server
    long id = (long) session.getAttribute("id"); // lấy id của user đang login
    currentUser.setId(id); // set lại id

    Cart cart = this.productService.fetchByUser(currentUser); // Viết một method trong
    // productService để lấy giỏ hàng của một user 

    // Tiếp theo sẽ cần lấy danh sách của từng product trong giỏ hàng bằng hàm sau
    List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();
    // if(cartDetails.isEmpty()){
    //   return "client/cart/emptyShow";
    // }
    double totalPrice = 0;

    for(CartDetail cd : cartDetails){
      totalPrice += cd.getPrice() * cd.getQuantity();
    }

    model.addAttribute("cartDetails", cartDetails);
    model.addAttribute("totalPrice", totalPrice);


    
    return "client/cart/show";
  }
  
  
}
