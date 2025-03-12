package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import org.springframework.ui.Model;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.service.OrderService;




@Controller
public class OrderController {
  private final OrderService orderService;

  

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/admin/order")
  public String getDashboard(Model model) {
    List<Order> orders = this.orderService.getAllOrders();
    if(orders != null){
      model.addAttribute("orders", orders);
    }
    
    return "admin/order/show";
  }

  @GetMapping("/admin/order/{id}")
  public String getMethodName(Model model, @PathVariable long id) {

    return "";
  }
  
}
