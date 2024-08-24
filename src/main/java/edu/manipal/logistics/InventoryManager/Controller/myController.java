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
import edu.manipal.logistics.InventoryManager.business.entities.CategoryItem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class myController {

    @GetMapping("/")
	public String loginPage(){
		return "login";
	}

	@PostMapping("/login")
	public String validLogin(HttpServletRequest req , @RequestParam String uname , @RequestParam String pwd) {
		// check login
		HttpSession s = req.getSession();
		s.setAttribute(uname, pwd);
		
		return "redirect:/categories";
	}

	@GetMapping("/categories")
	public String categoriesPage(@RequestParam(value = "selectedCategory", required = false) String selectedCategory, Model model){

		GoogleDatastore gd = new GoogleDatastore();
		List<Category> categories = gd.getAllCategory();
		model.addAttribute("categories", categories);
	
		if (selectedCategory != null && !selectedCategory.isEmpty()) {
			List<CategoryItem> categoryItems = gd.getAllItemsInCategory(selectedCategory);
			model.addAttribute("categoryItems", categoryItems);
			model.addAttribute("selectedCategory", selectedCategory);

			List<InventoryItem> items = gd.getAllInventoryItems();
			model.addAttribute("items", items);
		}
	
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

	@PostMapping("/selectCategory")
	public String selectCategory(@RequestParam String name) {
		return "redirect:/categories?selectedCategory=" + name;
	}

	/*@PostMapping("/deleteCategory")
	public String postMethodName(@RequestParam String name) {
		GoogleDatastore gd = new GoogleDatastore();
		gd.deleteCategory(name);		
		return "redirect:/categories";
	}*/
	

	@PostMapping("/newCategoryItem")
	public String newCategoryItem(@RequestParam String category, @RequestParam String itemKey, @RequestParam Long requested, @RequestParam Long given) {

		if (category.length() > 0 && itemKey.length() > 0) {
			CategoryItem ci = new CategoryItem();
			ci.setCategoryKey(category);
			ci.setItemKey(itemKey);
			ci.setGiven(given);
			ci.setRequested(requested);

			GoogleDatastore gd = new GoogleDatastore();

			if(!gd.existsCategoryItem(itemKey , category)){
				// get the category item and make the changes there
				InventoryItem ii = gd.getInventoryItem(itemKey);
				ii.changeRequested(requested);
				ii.changeGiven(given);
				ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());
				gd.saveInventoryItem(ii);
			}
			else{
				CategoryItem oldci = gd.getCategoryItem(itemKey, category);
				InventoryItem ii = gd.getInventoryItem(itemKey);
				ii.changeGiven(given - oldci.getGiven());
				ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());
				gd.saveInventoryItem(ii);
			}
			
			gd.saveCategoryItem(ci);
		}

		return "redirect:/categories?selectedCategory=" + category;
	}
	
	@PostMapping("/deleteCategoryItem")
	public String deleteCategoryItem(@RequestParam String category, @RequestParam String itemKey) {
		
		GoogleDatastore gd = new GoogleDatastore();

		InventoryItem ii = gd.getInventoryItem(itemKey);
		CategoryItem ci = gd.getCategoryItem(itemKey , category);
		ii.changeRequested(-ci.getRequested());
		ii.changeGiven(-ci.getGiven());
		ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());

		if(ii.getQuantity() == 0 && ii.getRequested() == 0 && ii.getGiven() == 0)
			gd.deleteInventoryItem(itemKey);
		else
			gd.saveInventoryItem(ii);
				
		gd.deleteCategoryItem(itemKey, category);
	
		return "redirect:/categories?selectedCategory=" + category;
	}
	
	
	@GetMapping("/itemList")
	public String itemListPage(Model model) {

		GoogleDatastore gd = new GoogleDatastore();
		List<InventoryItem> items = gd.getAllInventoryItems();
		model.addAttribute("items", items);

		return "itemList";
	}

	@PostMapping("/changeItem")
	public String changeRecieved(@RequestParam String itemKey , @RequestParam Long received , @RequestParam String vendor) {
		
		GoogleDatastore gd = new GoogleDatastore();
		InventoryItem ii = gd.getInventoryItem(itemKey);
		ii.changeReceived(received);
		ii.setVendor(vendor);
		gd.saveInventoryItem(ii);
		
		return "redirect:/itemList";
	}
	
	
	@GetMapping("/inventoryList")
	public String inventoryListPage(Model model) {

		GoogleDatastore gd = new GoogleDatastore();
		List<InventoryItem> items = gd.getAllInventoryItems();
		model.addAttribute("items", items);

		return "inventoryList";
	}

	@PostMapping("/newItem")
	public String newItem(@RequestParam String itemKey , @RequestParam Long quantity , @RequestParam Long requested) {

		if(itemKey.length() > 0 && quantity > 0){
			InventoryItem ii = new InventoryItem();
			ii.setItemKey(itemKey);
			ii.setQuantity(quantity);
			ii.setRequested(requested);

			GoogleDatastore gd = new GoogleDatastore();
			gd.saveInventoryItem(ii);
		}
		
		return "redirect:/inventoryList";
	}

	@PostMapping("/deleteItem")
	public String deleteItem(@RequestParam String deleteKey) {
		
		GoogleDatastore gd = new GoogleDatastore();
		InventoryItem ii = gd.getInventoryItem(deleteKey);
		if(ii.getRequested() == 0)
			gd.deleteInventoryItem(deleteKey);

		return "redirect:/inventoryList";
	}
}
