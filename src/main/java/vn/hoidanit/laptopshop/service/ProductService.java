package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.repository.ProductRepository;


@Service
public class ProductService {

  private final ProductRepository productRepository;

  

  public ProductService(ProductRepository userRepository) {
    this.productRepository = userRepository;
  }



  public Product handleSaveProduct(Product product){
    return this.productRepository.save(product);
  }

  public List<Product> getAllProducts(){
    return this.productRepository.findAll();
  }

  public List<Product> getProductByName(String name){
    return this.productRepository.findByName(name);
  }

  public Product getProductById(long id){
    return this.productRepository.findById(id);
  }

  public void deleteProductById(long id){
    this.productRepository.deleteById(id);
  }

}