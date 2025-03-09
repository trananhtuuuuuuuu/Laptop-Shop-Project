package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import vn.hoidanit.laptopshop.domain.Product;

import vn.hoidanit.laptopshop.service.ProductService;
import vn.hoidanit.laptopshop.service.UploadService;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;




@Controller
public class ProductController {

  private final ProductService productService;

  private final UploadService uploadService;




  public ProductController(ProductService productService,
  UploadService uploadService) {
    this.productService = productService;
    this.uploadService = uploadService;
  }

  @GetMapping("/admin/product")
  public String getProduct() {
      return "admin/product/show";
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

    List<FieldError> errors = newProductBindingResult.getFieldErrors();
    for (FieldError error : errors ) {
        System.out.println (error.getField() + " - " + error.getDefaultMessage());
    }
    double temp = productGot.getPrice();
    System.out.println("temp = " + temp);

    if(newProductBindingResult.hasErrors()){
      return "/admin/product/create";
    }



    String fileName = this.uploadService.handleSaveUploadFile(file, "product");
    //String hashPassword = this.passwordEncoder.encode(productGot.getPassword());
    
    productGot.setImage(fileName);

    Product product = this.productService.handleSaveProduct(productGot);

    System.out.println(product);

    System.out.println("File name image = " + fileName);

      return "admin/product/show";
  }
  
}
