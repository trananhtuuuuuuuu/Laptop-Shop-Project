package vn.hoidanit.laptopshop.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {


  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;
  

  private String email;
  private String password;
  private String fullName;
  private String address;
  private String phone;

  private String avatar;
  // roleID
  //User many -> to one > role
  @ManyToOne
  private Role role;

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }


  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }


  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  
  public String getFullName() {
    return fullName;
  }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }


  public String getAddress() {
    return address;
  }
  public void setAddress(String address) {
    this.address = address;
  }


  public String getPhone() {
    return phone;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }

  

  @Override
  public String toString(){
    return "user [id = " + this.id + " ,email = " + this.email  + " ,password = " + this.password +
    " ,fullname = " + this.fullName + ", address = " + this.address + " ,phone = " + this.phone + 
    " ,avatar = " + this.avatar + "]\n";
  }
  public String getAvatar() {
    return avatar;
  }
  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }
  

  
}
