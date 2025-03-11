package vn.hoidanit.laptopshop.service;

import java.util.List;
import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;


@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final CartRepository cartRepository;
  private final CartDetailRepository cartDetailRepository;
  

  public ProductService(ProductRepository userRepository,
  CartRepository cartRepository,
  CartDetailRepository cartDetailRepository) {
    this.productRepository = userRepository;
    this.cartDetailRepository = cartDetailRepository;
    this.cartRepository = cartRepository;
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

  public void handleAddProductToCart(){
    //check user đã có Cart chưa? Nếu chưa -> Tạo mới


    // Lưu cart detail



  }

}