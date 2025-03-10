package vn.hoidanit.laptopshop.domain;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotNull
  @Size(min=3, message = "Name min is three character")
  private String name;

  @NotNull
  @Min(value=1, message = "Not empty for price")
  private double price;


  private String image;


  @NotNull(message = "detailDesc not Empty")
  @Column(columnDefinition = "MEDIUMTEXT")
  private String detailDesc;



  @NotNull
  @Size(min=1, message = "Short description for product")
  private String shortDesc;


  @NotNull
  @Min(value = 1, message = "Must be to enter for quantity")
  private long quantity;

  private long sold;
  private String factory;
  private String target;


  // 1 product hasMany OrderDetail
  


  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public double getPrice() {
    return price;
  }
  public void setPrice(double price) {
    this.price = price;
  }
  public String getImage() {
    return image;
  }
  public void setImage(String image) {
    this.image = image;
  }
  public String getDetailDesc() {
    return detailDesc;
  }
  public void setDetailDesc(String detailDesc) {
    this.detailDesc = detailDesc;
  }
  public String getShortDesc() {
    return shortDesc;
  }
  public void setShortDesc(String shortDesc) {
    this.shortDesc = shortDesc;
  }
  public long getQuantity() {
    return quantity;
  }
  public void setQuantity(long quantity) {
    this.quantity = quantity;
  }
  public long getSold() {
    return sold;
  }
  public void setSold(long sold) {
    this.sold = sold;
  }
  public String getFactory() {
    return factory;
  }
  public void setFactory(String factory) {
    this.factory = factory;
  }
  public String getTarget() {
    return target;
  }
  public void setTarget(String target) {
    this.target = target;
  }

  @Override
  public String toString(){
    return "Product [id=" + this.id + 
    ", name=" + this.name +
    ", price=" + this.price +
    ", image=" + this.image +
    ", detailDesc=" + this.detailDesc +
    ", shortDesc=" + this.shortDesc +
    ", quantity=" + this.quantity +
    ", sold=" + this.sold +
    ", factory=" + this.factory +
    ", target=" + this.target + "]\n";
  }

  

}
