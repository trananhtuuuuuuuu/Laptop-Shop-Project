package vn.hoidanit.laptopshop.domain;

import jakarta.persistence.Entity;

@Entity
public class User {
  private long id;
  private String email;
  private String password;
  private String fullName;
  private String address;
  private String phone;

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
    return "[id = " + this.id + " ,email = " + this.email  + " ,password = " + this.password +
    " ,fullname = " + this.fullName + ", address = " + this.address + " ,phone = " + this.phone + "\n";
  }
  

  
}
