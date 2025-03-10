package vn.hoidanit.laptopshop.service;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;



@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserService userService; 

  public CustomUserDetailsService(UserService userService){
    this.userService = userService;
  }
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //logic
    vn.hoidanit.laptopshop.domain.User user = this.userService.getUserByEmail(username);
    if(user == null){
      throw new UsernameNotFoundException("User Not Found");
    }
     return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

  }
  
}
