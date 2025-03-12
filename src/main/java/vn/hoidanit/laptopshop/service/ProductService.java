package vn.hoidanit.laptopshop.service;

import java.util.List;


import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;


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

  public List<Product> getAllProducts(){
    return this.productRepository.findAll();
  }


  public Cart fetchByUser(User user){
    return this.cartRepository.findByUser(user);
  }

  public void handleAddProductToCart(String email, 
  long productId,
  HttpSession session){
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
          cartDetail.setQuantity(1);
          this.cartDetailRepository.save(cartDetail);

          //update cart(sum)
          int s = cart.getSum() + 1;
          cart.setSum(s);
          cart = this.cartRepository.save(cart);
          session.setAttribute("sum", s);
        }
        else{
          oldDetail.setQuantity(oldDetail.getQuantity() + 1);
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


}