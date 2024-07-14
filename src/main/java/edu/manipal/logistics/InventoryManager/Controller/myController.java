package edu.manipal.logistics.InventoryManager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class myController {
    @GetMapping("/")
	public String loginPage(){
		return "login";
	}

	@GetMapping("/categories")
	public String catrgoriesPage(@RequestParam(required = false) String uname , @RequestParam(required = false) String pwd) {
		return "categories";
	}
	
	@GetMapping("/itemList")
	public String itemListPage() {
		return "itemList";
	}
	
	@GetMapping("/inventoryList")
	public String inventoryListPage() {
		return "inventoryList";
	}
}
