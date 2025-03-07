package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Role;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;
import vn.hoidanit.laptopshop.repository.RoleRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public UserService(UserRepository userRepository,
  RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  public List<User> getAllUsers(){
    List<User> users = this.userRepository.findAll();
    return users;
  }


  public List<User> getAllUsersByEmail(String email){
    return this.userRepository.findByEmail(email);
  }


  public User handleSaveUser(User user){
    return this.userRepository.save(user);
  }

  public User getUserById(long id){
    return this.userRepository.findById(id);
  }


  public void deleteUserById(long id){
    this.userRepository.deleteById(id);
  }
  
  public Role getRoleByName(String name){
    return this.roleRepository.findByName(name);
  }
  
}
