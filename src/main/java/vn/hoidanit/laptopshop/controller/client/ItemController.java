package vn.hoidanit.laptopshop.controller.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.service.ProductService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
      this.productService.handleAddProductToCart(email, productId, session, 1);
      
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

    model.addAttribute("cart", cart);


    
    return "client/cart/show";
  }

  @PostMapping("/delete-cart-product/{id}")
  public String deleteProductInCart(HttpServletRequest request,
  @PathVariable long id) {
      
    HttpSession session = request.getSession(false);

    long cartDetailId = id;

    this.productService.deleteCartDetail(cartDetailId, session);
    
    return "redirect:/cart";
  }


  @GetMapping("/checkout")
  public String getCheckOutPage(Model model, HttpServletRequest request) {
    User currentUser = new User();// null
    HttpSession session = request.getSession(false);
    long id = (long) session.getAttribute("id");
    currentUser.setId(id);
 
    Cart cart = this.productService.fetchByUser(currentUser);
 
    List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();
 
    double totalPrice = 0;
    for(CartDetail cd : cartDetails) {
      totalPrice += cd.getPrice() * cd.getQuantity();
    }
 
    model.addAttribute("cartDetails", cartDetails);
    model.addAttribute("totalPrice", totalPrice);
 
    return "client/cart/checkout";
    }
 
  @PostMapping("/confirm-checkout")
  public String getCheckOutPage(@ModelAttribute("cart") Cart cart) {
      List<CartDetail> cartDetails = cart == null ? new ArrayList<CartDetail>() : cart.getCartDetails();
      this.productService.handleUpdateCartBeforeCheckout(cartDetails);
      return "redirect:/checkout";
  }
 
  @PostMapping("/place-order")
  public String handlePlaceOrder(
    HttpServletRequest request,
    @RequestParam("receiverName") String receiverName,
    @RequestParam("receiverAddress") String receiverAddress,
    @RequestParam("receiverPhone") String receiverPhone) {
      //logic

    User currentUser = new User();// null
    HttpSession session = request.getSession(false);
    long id = (long) session.getAttribute("id");
    currentUser.setId(id);

    this.productService.handlePlaceOrder(currentUser, session, receiverName, receiverAddress, receiverPhone);
 
    return "redirect:/thanks";
  }

  @GetMapping("/thanks")
  public String getThanks(Model model) {
      return "client/cart/thanks";
  }
     

     
  @PostMapping("/add-product-from-view-detail")
  public String handleAddProductFromViewDetail(
          @RequestParam("id") long id,
          @RequestParam("quantity") long quantity,
          HttpServletRequest request) {
      HttpSession session = request.getSession(false);

      String email = (String) session.getAttribute("email");
      this.productService.handleAddProductToCart(email, id, session, quantity);
      return "redirect:/product/" + id;
  }



  @GetMapping("/products")
  public String getProducts(Model model,
  @RequestParam("page") Optional<String> pageOptional,
  @RequestParam("name") Optional<String> nameOptional,
  @RequestParam("min-price") Optional<String> minOptional,
  @RequestParam("max-price") Optional<String> maxOptional,
  @RequestParam("factory") Optional<String> factoryOptional,
  @RequestParam("price") Optional<String> priceOptional
  ) {

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


    


    Pageable pageable = PageRequest.of(page - 1, 60);

    //case 0: filter by name
    // String name = nameOptional.isPresent() ? nameOptional.get() : "";
    // Page<Product> pageProducts = this.productService.getAllProducts(pageable, name);


    //case 1: filter by min-price
    double min = minOptional.isPresent() ? Double.parseDouble(minOptional.get()) : 0;
    Page<Product> pageProducts = this.productService.getLessThanProductWithPrice(pageable, min);
    






    List<Product> products = pageProducts.getContent(); 
    model.addAttribute("products", products);
    model.addAttribute("currentPage", page);

    model.addAttribute("totalPages", pageProducts.getTotalPages());


      return "client/product/show";
  }
  
  
  
}
