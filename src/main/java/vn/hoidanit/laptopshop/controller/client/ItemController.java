package vn.hoidanit.laptopshop.controller.client;


import java.util.ArrayList;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.ProductCriterialDTO;
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
  ProductCriterialDTO productCriterialDTO,
  HttpServletRequest request
  ){
    //System.out.println(productCriterialDTO);
    int page = 1;
    try{ 
      if(productCriterialDTO.getPage().isPresent()){ 
        //convert from String to int
        page = Integer.parseInt(productCriterialDTO.getPage().get());
      }
      else{
        //page = 1
      }
    } catch(Exception e){
      //page = 1
    }
    Pageable pageable = PageRequest.of(page - 1, 3);

    if(productCriterialDTO.getSort() != null && productCriterialDTO.getSort().isPresent()){
      String sort = productCriterialDTO.getSort().get();
      if(sort.equals("gia-tang-dan")){
        pageable = PageRequest.of(page - 1, 10, Sort.by(Product_.PRICE).ascending());

      }
      else if(sort.equals("gia-giam-dan")){
        pageable = PageRequest.of(page - 1, 10, Sort.by(Product_.PRICE).descending());

      }
      else{
        pageable = PageRequest.of(page - 1, 10);
      }
    }
   

    //case 0: filter by name
    // String name = nameOptional.isPresent() ? nameOptional.get() : "";
    Page<Product> pageProducts = this.productService.getAllProducts(pageable, productCriterialDTO);

    


    //case 1: filter by min-price
    // double min = minOptional.isPresent() ? Double.parseDouble(minOptional.get()) : 0;
    // Page<Product> pageProducts = this.productService.getGreaterThanProductWithPrice(pageable, min);

    //case 2: filter by max-price
    // double max = maxOptional.isPresent() ? Double.parseDouble(maxOptional.get()) : 0;
    // Page<Product> pageProducts = this.productService.getLessThanProductWithPrice(pageable, max);
    
    //case 3: filter by factory
    // String factory = factoryOptional.isPresent() ? factoryOptional.get() : "";
    // Page<Product> pageProducts = this.productService.getOneFactoryProduct(pageable, factory);

    //case 4:
    // List<String> factory = Arrays.asList(factoryOptional.get().split(","));
    // Page<Product> pageProducts = this.productService.getFactoryProduct(pageable, factory);

    //case 5:
    // String price = priceOptional.isPresent() ? priceOptional.get() : "";
    // Page<Product> pageProducts = this.productService.getPriceProduct(pageable, price);

    //case 6:
    // List<String> price = Arrays.asList(priceOptional.get().split(","));
    // Page<Product> pageProducts = this.productService.getListPriceProduct(pageable, price);



    List<Product> products = pageProducts.getContent().size() > 0 ? pageProducts.getContent() : new ArrayList<Product>();


    String qs = request.getQueryString();
    if (qs != null && !qs.isBlank()) {
        // remove page
        qs = qs.replace("page=" + page, "");
    }



    model.addAttribute("products", products);
    model.addAttribute("currentPage", page);

    model.addAttribute("totalPages", pageProducts.getTotalPages());
    model.addAttribute("queryString", qs);


      return "client/product/show";
  }
  
  
  
}
