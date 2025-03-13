package vn.hoidanit.laptopshop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import vn.hoidanit.laptopshop.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User save(User user); 

  Page<User> findAll(Pageable pageable);

  List<User> findByEmail(String email);

  User findById(long id);

  void deleteById(long id);

  boolean existsByEmail(String email);

  User findOneByEmail(String email);
}
