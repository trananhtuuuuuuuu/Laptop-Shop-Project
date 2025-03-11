package vn.hoidanit.laptopshop.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hoidanit.laptopshop.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
  Cart findByUser(vn.hoidanit.laptopshop.domain.User user);
}
