package vn.hoidanit.laptopshop.service.specification;

import org.springframework.data.jpa.domain.Specification;

import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.Product_;

// Đây là class mà các tiêu chi muốn filter phải viết trong class này
public class ProductSpecs {

  public static Specification<Product> nameLike(String name){
    return (root, query, criteriaBuilder) 
        -> criteriaBuilder.like(root.get(Product_.NAME), "%"+name+"%");
  }

  
}
