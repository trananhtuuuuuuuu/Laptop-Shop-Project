package vn.hoidanit.laptopshop.repository;




import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import vn.hoidanit.laptopshop.domain.Product;


public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
  Product save(Product product);

  List<Product> findAll();

  Product findById(long id);
  
  Page<Product> findAll(Pageable page);

  Page<Product> findAll(Specification<Product> spec, Pageable page);

  void deleteById(long id);
}
