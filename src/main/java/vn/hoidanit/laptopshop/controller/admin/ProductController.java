package vn.hoidanit.laptopshop.controller.admin;



import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;


@Controller
public class ProductController {

  private final ProductService productService;

  private final UploadService uploadService;


  public ProductController(ProductService productService,
  UploadService uploadService) {
    this.productService = productService;
    this.uploadService = uploadService;
  }



  // GET để render dữ liệu với url /admin/product/create
  @GetMapping("/admin/product/create")
  public String getCreateProductPage(Model model) {
      model.addAttribute("newProduct", new Product());
      return "admin/product/create";
  }
  

  // POST để người dùng submit form và gửi dữ liệu lên với url là /admin/product/create,
  // thì sẽ nhảy vào hàm này
  @PostMapping("/admin/product/create")
  public String getProductPage(Model model,
  @ModelAttribute("newProduct") @Valid Product productGot,
  BindingResult newProductBindingResult,
  @RequestParam("imageFile") MultipartFile file) {

    if(newProductBindingResult.hasErrors()){
      return "admin/product/create";
    }


    String fileName = this.uploadService.handleSaveUploadFile(file, "product");
    //String hashPassword = this.passwordEncoder.encode(productGot.getPassword());
    
    productGot.setImage(fileName);

    Product product = this.productService.handleSaveProduct(productGot);

    System.out.println(product);

    System.out.println("File name image = " + fileName);

      return "redirect:/admin/product";
  }




  @GetMapping("/admin/product")
  public String getProduct(Model model,
  @RequestParam("page") Optional<String> pageOptional) {
    //thực tế thì DB quan tâm 2 tham số laf offset và limit
    //client chỉ quan tâm page, truyền bằng page hoặc limit
    // Ví dụ page = 1 . limit = 10 thì
    // dưới DB có 100 rows. Ví dụ count = 100 => chia limit = 10 pages

    int page = 1;
    try{ 
      if(pageOptional.isPresent()){ 
        //convert from String to int
        page = Integer.parseInt(pageOptional.get());
      }
      else{
        //page = 1
      }
    } catch(Exception e){
      //page = 1
    }

    Pageable pageable = PageRequest.of(page - 1, 5); // tham số đầu tiên là pageNumber: La phía gửi lên từ client
    // và tham số thứ 2 là pageSize: Số lượng phần tử muốn lấy

      Page<Product> products = this.productService.getAllProducts(pageable);
      List<Product> listProducts = products.getContent();

      model.addAttribute("products", listProducts);

      model.addAttribute("currentPage", page);

      model.addAttribute("totalPages", products.getTotalPages());
      return "admin/product/show";
  }
  



  @GetMapping("/admin/product/{id}")
  public String detailProduct(Model model, @PathVariable long id) {

    Product product = this.productService.getProductById(id);
    System.out.println(product);
    model.addAttribute("product", product);
    model.addAttribute("id", id);
    return "admin/product/detail";
  }





  @GetMapping("/admin/product/update/{id}")
  public String getUpdateProduct(Model model, @PathVariable long id) {
    Product product = this.productService.getProductById(id);
    model.addAttribute("product", product);
    model.addAttribute("id", id);
    return "admin/product/update";
  }




  @PostMapping("/admin/product/update")
  public String postUpdateProduct(Model model, 
  @ModelAttribute("product") @Valid Product productGot,
  BindingResult newProductBindingResult,
  @RequestParam("imageFile") MultipartFile file) {

    if (newProductBindingResult.hasErrors()) {
      return "admin/product/update";
    }

    System.out.println(productGot);
    
    Product getProduct = this.productService.getProductById(productGot.getId());

    if(getProduct != null){

      if(!file.isEmpty()){
        String img = this.uploadService.handleSaveUploadFile(file, "product");
        getProduct.setImage(img);
      }

      getProduct.setName(productGot.getName());
      getProduct.setPrice(productGot.getPrice());
      getProduct.setQuantity(productGot.getQuantity());
      getProduct.setDetailDesc(productGot.getDetailDesc());
      getProduct.setShortDesc(productGot.getShortDesc());
      getProduct.setFactory(productGot.getFactory());
      
      getProduct.setTarget(productGot.getTarget());
      Product updateProduct= this.productService.handleSaveProduct(getProduct);
      System.out.println("Updated Product " + updateProduct + " successfully");
    }
      
    return "redirect:/admin/product";
  }
  

  @GetMapping("/admin/product/delete/{id}")
  public String getDeleteProduct(Model model, @PathVariable long id) {
    Product product = this.productService.getProductById(id);
    model.addAttribute("product", product);
    return "admin/product/delete";
  }

  @PostMapping("/admin/product/delete")
  public String postMethodName(Model model, 
  @ModelAttribute("product") Product productGot) {
    
    this.productService.deleteProduct(productGot.getId());
      
    return "redirect:/admin/product";
  }


  
  
  
  
  
  


  
}
