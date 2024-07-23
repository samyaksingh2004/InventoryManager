package edu.manipal.logistics.InventoryManager.Controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import edu.manipal.logistics.InventoryManager.business.database.GoogleDatastore;
import edu.manipal.logistics.InventoryManager.business.entities.Category;
import edu.manipal.logistics.InventoryManager.business.entities.InventoryItem;

import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class myController {

    @GetMapping("/")
	public String loginPage(){
		return "login";
	}

	@PostMapping("/login")
	public String validLogin(@RequestParam String uname , @RequestParam String pwd) {
		// check login
		return "redirect:/categories";
	}

	@GetMapping("/categories")
	public String catrgoriesPage(Model model){

		GoogleDatastore gd = new GoogleDatastore();
		List<Category> categories = gd.getAllCategory();
		model.addAttribute("categories", categories);

		return "categories";
	}

	@PostMapping("/newcategory")
	public String newCategory(@RequestParam String name) {

		Category c = new Category();
		c.setName(name);
		GoogleDatastore gd = new GoogleDatastore();
		gd.saveCategory(c);

		return "redirect:/categories";
	}
	
	@GetMapping("/itemList")
	public String itemListPage() {
		return "itemList";
	}
	
	@GetMapping("/inventoryList")
	public String inventoryListPage(Model model) {

		GoogleDatastore gd = new GoogleDatastore();
		List<InventoryItem> items = gd.getAllInventoryItems();
		model.addAttribute("items", items);

		return "inventoryList";
	}

	@PostMapping("/newItem")
	public String newItem(@RequestParam String itemKey , @RequestParam Long quantity) {

		if(itemKey.length() > 0 && quantity > 0){
			InventoryItem ii = new InventoryItem();
			ii.setItemKey(itemKey);
			ii.setQuantity(quantity);
			GoogleDatastore gd = new GoogleDatastore();
			gd.saveInventoryItem(ii);
		}
		
		return "redirect:/inventoryList";
	}

	@PostMapping("/deleteItem")
	public String deleteItem(@RequestParam String deleteKey) {
		
		GoogleDatastore gd = new GoogleDatastore();
		gd.deleteInventoryItem(deleteKey);

		return "redirect:/inventoryList";
	}

	@PostMapping("/editQuantity")
	public String editQuantity(@RequestBody String entity) {
		//TODO: process POST request
		
		return entity;
	}
	
	
}
