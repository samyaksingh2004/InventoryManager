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
import edu.manipal.logistics.InventoryManager.business.entities.OrderItem;


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
	public String categoriesPage(@RequestParam(value = "selectedCategory", required = false) String selectedCategory, Model model){

		GoogleDatastore gd = new GoogleDatastore();
		List<Category> categories = gd.getAllCategory();
		model.addAttribute("categories", categories);
	
		if (selectedCategory != null && !selectedCategory.isEmpty()) {
			List<OrderItem> orderItems = gd.getAllItemsInCategory(selectedCategory);
			model.addAttribute("orderItems", orderItems);
			model.addAttribute("selectedCategory", selectedCategory);
		}
	
		return "categories";
	}

	/*@PostMapping("/deleteCategory")
	public String deleteCategory(@RequestParam String name) {
		GoogleDatastore gd = new GoogleDatastore();
		gd.deleteCategory(name);
		
		return "redirect:/categories";
	}*/
	

	@PostMapping("/newcategory")
	public String newCategory(@RequestParam String name) {

		Category c = new Category();
		c.setName(name);
		GoogleDatastore gd = new GoogleDatastore();
		gd.saveCategory(c);

		return "redirect:/categories";
	}

	@PostMapping("/selectCategory")
	public String selectCategory(@RequestParam String name) {
		return "redirect:/categories?selectedCategory=" + name;
	}

	@PostMapping("/newOrderItem")
	public String newOrderItem(@RequestParam String category, @RequestParam String itemKey, @RequestParam Long requested, @RequestParam Long given) {

		if (category.length() > 0 && itemKey.length() > 0) {
			OrderItem oi = new OrderItem();
			oi.setCategoryKey(category);
			oi.setItemKey(itemKey);
			oi.setGiven(given);
			oi.setRequested(requested);

			GoogleDatastore gd = new GoogleDatastore();
			InventoryItem ii = gd.getInventoryItem(itemKey);
			ii.changeRequested(requested);

			gd.saveInventoryItem(ii);
			gd.saveOrderItem(oi);
		}

		return "redirect:/categories?selectedCategory=" + category;
	}
	
	@PostMapping("/deleteOrderItem")
	public String deleteOrderItem(@RequestParam String category, @RequestParam String itemKey) {
		
		GoogleDatastore gd = new GoogleDatastore();

		InventoryItem ii = gd.getInventoryItem(itemKey);
		OrderItem oi = gd.getOrderItem(itemKey, category);

		if (ii.getQuantity() == 0)
			gd.deleteInventoryItem(itemKey);
		else{
			ii.changeRequested(-oi.getRequested());
			gd.saveInventoryItem(ii);
		}
		
		gd.deleteOrderItem(itemKey, category);
	
		return "redirect:/categories?selectedCategory=" + category;
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
}
