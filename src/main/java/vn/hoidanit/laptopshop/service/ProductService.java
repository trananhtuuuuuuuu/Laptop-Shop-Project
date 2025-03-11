package vn.hoidanit.laptopshop.service;

import java.util.List;


import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;


@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CartRepository cartRepository;
  private final CartDetailRepository cartDetailRepository;
  private final UserService userService;

  public ProductService(ProductRepository userRepository,
  CartRepository cartRepository,
  CartDetailRepository cartDetailRepository,
  UserService userService) {
    this.productRepository = userRepository;
    this.cartDetailRepository = cartDetailRepository;
    this.cartRepository = cartRepository;
    this.userService = userService;
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

  public void handleAddProductToCart(String email, long productId){
    //check user đã có Cart chưa? Nếu chưa -> Tạo mới
    User user = this.userService.getUserByEmail(email);
    if(user != null){
      Cart cart = this.cartRepository.findByUser(user);


      if(cart == null){
        //create new cart
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setSum(1);
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

        CartDetail cartDetail = new CartDetail();
        cartDetail.setCart(cart);
        cartDetail.setProduct(realProduct);
        cartDetail.setPrice(realProduct.getPrice());
        cartDetail.setQuantity(1);
        this.cartDetailRepository.save(cartDetail);
      }
      
      
    }



    // Lưu cart detail



  }

}