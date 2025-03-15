package vn.hoidanit.laptopshop.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.ProductCriterialDTO;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.service.specification.ProductSpecs;


@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CartRepository cartRepository;
  private final CartDetailRepository cartDetailRepository;
  private final UserService userService;
  private final OrderRepository orderRepository;
  private final OrderDetailRepository orderDetailRepository;

  public ProductService(ProductRepository userRepository,
  CartRepository cartRepository,
  CartDetailRepository cartDetailRepository,
  UserService userService,
  OrderRepository orderRepository,
  OrderDetailRepository orderDetailRepository) {
    this.productRepository = userRepository;
    this.cartDetailRepository = cartDetailRepository;
    this.cartRepository = cartRepository;
    this.userService = userService;
    this.orderDetailRepository = orderDetailRepository;
    this.orderRepository = orderRepository;
  }

  public Product getProductById(long id) {
    return this.productRepository.findById(id);
  }






  public void deleteProduct(long id) {
      this.productRepository.deleteById(id);
  }







  public Product handleSaveProduct(Product product){
    return this.productRepository.save(product);
  }






  public Page<Product> getAllProducts(Pageable pageable){
    return this.productRepository.findAll(pageable);
  }





  
  public Cart fetchByUser(User user){
    return this.cartRepository.findByUser(user);
  }





  public void handleAddProductToCart(String email, long productId, HttpSession session, long quantity){
    //check user đã có Cart chưa? Nếu chưa -> Tạo mới
    User user = this.userService.getUserByEmail(email);
    if(user != null){
      Cart cart = this.cartRepository.findByUser(user);


      if(cart == null){
        //create new cart
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setSum(0);
        cart = this.cartRepository.save(newCart);
      }

      //save cart_detail
      //find product by id
      Product product = this.productRepository.findById(productId);

      if(product != null){
        Product realProduct = new Product();

        realProduct.setDetailDesc(product.getDetailDesc());
        realProduct.setShortDesc(product.getShortDesc());
        realProduct.setFactory(product.getFactory());
        realProduct.setId(product.getId());
        realProduct.setImage(product.getImage());
        realProduct.setPrice(product.getPrice());
        realProduct.setQuantity(product.getQuantity());
        realProduct.setSold(product.getSold());
        realProduct.setName(product.getName());
        realProduct.setTarget(product.getTarget());

        //check xem sản phẩm đã từng được thêm vào giỏ hàng
        // trước đây chưa?

        //boolean isExistProductInCart = this.cartDetailRepository.existsByCartAndProduct(cart, realProduct);

        CartDetail oldDetail = 
        this.cartDetailRepository.findByCartAndProduct(
          cart, realProduct);
        
        if(oldDetail == null){
          CartDetail cartDetail = new CartDetail();
          cartDetail.setCart(cart);
          cartDetail.setProduct(realProduct);
          cartDetail.setPrice(realProduct.getPrice());
          cartDetail.setQuantity(quantity);
          this.cartDetailRepository.save(cartDetail);

          //update cart(sum)
          int s = cart.getSum() + 1;
          cart.setSum(s);
          cart = this.cartRepository.save(cart);
          session.setAttribute("sum", s);
        }
        else{
          oldDetail.setQuantity(oldDetail.getQuantity() + quantity);
          this.cartDetailRepository.save(oldDetail);
        }

        
      }
      
      
    }



    // Lưu cart detail



  }





  public CartDetail getCartDetailById(long id){
    return this.cartDetailRepository.findById(id);
  }





  public void deleteCartDetail(long cartDetailId, HttpSession session){
    CartDetail cartDetail = this.getCartDetailById(cartDetailId);
    if(cartDetail != null){
      Cart currentCart = cartDetail.getCart();

      this.cartDetailRepository.deleteById(cartDetailId);

      //update cart
      if (currentCart.getSum() > 1) {
        // update current cart
        int s = currentCart.getSum() - 1;
        currentCart.setSum(s);
        session.setAttribute("sum", s);
        this.cartRepository.save(currentCart);
      } else {
        // delete cart (sum = 1)
        this.cartRepository.deleteById(currentCart.getId());
        session.setAttribute("sum", 0);
    }


      //
    }
  }





  public void handleUpdateCartBeforeCheckout(List<CartDetail> cartDetails) {
    for (CartDetail cartDetail : cartDetails) {
        CartDetail cdOptional = this.cartDetailRepository.findById(cartDetail.getId());
        if (cdOptional != null) {
            cdOptional.setQuantity(cartDetail.getQuantity());
            this.cartDetailRepository.save(cdOptional);
        }
    }
  }






  public void handlePlaceOrder(
    User user, HttpSession session,
    String receiverName, String receiverAddress, String receiverPhone){

    //step 1:
    // get cart by user
    Cart cart = this.cartRepository.findByUser(user);
    if(cart != null){
      List<CartDetail> cartDetails = cart.getCartDetails();

      if(cartDetails != null){
         //create order 
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(receiverName);
        order.setReceiverAddress(receiverAddress);
        order.setReceiverPhone(receiverPhone);
        order.setStatus("PENDING");
      

        double sum = 0;
        for(CartDetail cd : cartDetails){
          sum += cd.getPrice();
          
        }
        order.setTotalPrice(sum);
        order = this.orderRepository.save(order);

        //step 2:
        // delete cartDetail and cart
        for(CartDetail cd : cartDetails){
          OrderDetail orderDetail = new OrderDetail();
          orderDetail.setOrder(order);
          orderDetail.setProduct(cd.getProduct());
          orderDetail.setQuantity(cd.getQuantity());
          orderDetail.setPrice(cd.getPrice());
          this.orderDetailRepository.save(orderDetail);
        }

        for(CartDetail cd : cartDetails){
          this.cartDetailRepository.deleteById(cd.getId());
        }

        this.cartRepository.deleteById(cart.getId());
        //update session
        session.setAttribute("sum", 0);
      }
    }

  }






  //
  public Page<Product> getAllProducts(Pageable pageable, ProductCriterialDTO productCriterialDTO){

    if(productCriterialDTO.getTarget() == null && 
    productCriterialDTO.getFactory() == null &&
    productCriterialDTO.getPrice() == null){
      return this.productRepository.findAll(pageable);
    }
    Specification<Product> combinedSpec = Specification.where(null);
    if(productCriterialDTO.getTarget() != null && productCriterialDTO.getTarget().isPresent()){
      Specification<Product> currSpecification = ProductSpecs.matchListTarget(productCriterialDTO.getTarget().get());
      combinedSpec = combinedSpec.and(currSpecification);
    }

    if(productCriterialDTO.getFactory() != null && productCriterialDTO.getFactory().isPresent()){
      Specification<Product> currSpecification = ProductSpecs.matchListFactory(productCriterialDTO.getFactory().get());
      combinedSpec = combinedSpec.and(currSpecification);
    }
    if (productCriterialDTO.getPrice() != null && productCriterialDTO.getPrice().isPresent()) {
      Specification<Product> currentSpecs = this.buildPriceSpecification(productCriterialDTO.getPrice().get());
            combinedSpec = combinedSpec.and(currentSpecs);
          }
      
      return this.productRepository.findAll(combinedSpec, pageable);
     }
      
      
        
    
      
    
  public Specification<Product> buildPriceSpecification(List<String> price) {
    Specification<Product> combinedSpec = Specification.where(null);
    //  int count = 0;
      for (String p : price) {
        double min = 0;
        double max = 0;
  
              // Set the appropriate min and max based on the price range string
        switch (p) {
          case "duoi-10-trieu":
            min = 1;
            max = 10000000;
            break;
          case "10-15-trieu":
            min = 10000000;
            max = 15000000;
             //count++;
            break;
          case "15-20-trieu":
            min = 15000000;
            max = 20000000;
             //count++;
            break;
          case "tren-20-trieu":
            min = 20000000;
            max = Double.MAX_VALUE;
             //count++;
            break;
          case "20-30-trieu":
            min = 20000000;
            max = 30000000;
             //count++;
            break;
                  // Add more cases as needed
        }
  
          if (min != 0 && max != 0) {
              Specification<Product> rangeSpec = ProductSpecs.matchListPrice(min, max);
              combinedSpec = combinedSpec.or(rangeSpec);
          }
      }
  
          // Check if any price ranges were added (combinedSpec is empty)
         //  if (count == 0) {
         //      return this.productRepository.findAll(page);
         //  }
  
          return combinedSpec;
      }
  


}