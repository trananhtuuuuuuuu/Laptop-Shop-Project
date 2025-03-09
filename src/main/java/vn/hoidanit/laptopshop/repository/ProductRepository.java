package vn.hoidanit.laptopshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.hoidanit.laptopshop.domain.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
  Product save(Product product);
  List<Product> findAll();
  List<Product> findByName(String name);
  Product findById(long id);
  void deleteById(long id);
}
