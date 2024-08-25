package edu.manipal.logistics.InventoryManager.Controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.ui.Model;

import edu.manipal.logistics.InventoryManager.business.database.GoogleDatastore;
import edu.manipal.logistics.InventoryManager.business.entities.Category;
import edu.manipal.logistics.InventoryManager.business.entities.InventoryItem;
import edu.manipal.logistics.InventoryManager.business.entities.UserInfo;
import edu.manipal.logistics.InventoryManager.business.service.LoginService;
import edu.manipal.logistics.InventoryManager.business.entities.CategoryItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
@SessionAttributes()
public class myController {

    private boolean isUserLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("name") != null;
    }

    @GetMapping("/")
    public String loginPage(){
        return "login";
    }

    @PostMapping("/login")
    public String validLogin(HttpServletRequest req , Model model , @RequestParam String name , @RequestParam String password) {
        HttpSession session = req.getSession();
        session.setAttribute("name", name);

        LoginService service = new LoginService();
        boolean isValidUser = service.validateUser(name , password);
        if(!isValidUser){
            model.addAttribute("errorMessage" , "Access Denied , Invalid Credentials");
            return "redirect:/";
        }

        model.addAttribute("name" , name);
        model.addAttribute("password" , password);

        return "redirect:/categories";
    }

    @PostMapping("/createUser")
    public String createUser(HttpServletRequest req , Model model , @RequestParam String name , @RequestParam String password) {
        GoogleDatastore gd = new GoogleDatastore();
        if(gd.existsUser(name)){
            model.addAttribute("errorMessage" , "Username Taken");
            return "redirect:/";
        }
        
        UserInfo ui = new UserInfo();
        ui.setName(name);
        ui.setPassword(password);
        gd.saveUserInfo(ui);

        return "redirect:/";
    }

    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest req, @RequestParam(value = "selectedCategory", required = false) String selectedCategory, Model model){
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

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
    public String newCategory(HttpServletRequest req, @RequestParam String name) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        Category c = new Category();
        c.setName(name);
        GoogleDatastore gd = new GoogleDatastore();
        gd.saveCategory(c);

        return "redirect:/categories";
    }

    @PostMapping("/selectCategory")
    public String selectCategory(HttpServletRequest req, @RequestParam String name) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        return "redirect:/categories?selectedCategory=" + name;
    }

    @PostMapping("/newCategoryItem")
    public String newCategoryItem(HttpServletRequest req, @RequestParam String category, @RequestParam String itemKey, @RequestParam Long requested, @RequestParam Long given) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        if (category.length() > 0 && itemKey.length() > 0) {
            CategoryItem ci = new CategoryItem();
            ci.setCategoryKey(category);
            ci.setItemKey(itemKey);
            ci.setGiven(given);
            ci.setRequested(requested);

            GoogleDatastore gd = new GoogleDatastore();

            if(!gd.existsCategoryItem(itemKey , category)){
                InventoryItem ii = gd.getInventoryItem(itemKey);
                ii.changeRequested(requested);
                ii.changeGiven(given);
                ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());
                gd.saveInventoryItem(ii);
            } else {
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
    public String deleteCategoryItem(HttpServletRequest req, @RequestParam String category, @RequestParam String itemKey) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

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
    public String itemListPage(HttpServletRequest req, Model model) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        List<InventoryItem> items = gd.getAllInventoryItems();
        model.addAttribute("items", items);

        return "itemList";
    }

    @PostMapping("/changeItem")
    public String changeRecieved(HttpServletRequest req, @RequestParam String itemKey , @RequestParam Long received , @RequestParam String vendor) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        InventoryItem ii = gd.getInventoryItem(itemKey);
        ii.changeReceived(received);
        ii.setVendor(vendor);
        gd.saveInventoryItem(ii);
        
        return "redirect:/itemList";
    }

    @GetMapping("/inventoryList")
    public String inventoryListPage(HttpServletRequest req, Model model) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        List<InventoryItem> items = gd.getAllInventoryItems();
        model.addAttribute("items", items);

        return "inventoryList";
    }

    @PostMapping("/newItem")
    public String newItem(HttpServletRequest req, @RequestParam String itemKey , @RequestParam Long quantity , @RequestParam Long requested) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

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
    public String deleteItem(HttpServletRequest req, @RequestParam String deleteKey) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        InventoryItem ii = gd.getInventoryItem(deleteKey);
        if(ii.getRequested() == 0)
            gd.deleteInventoryItem(deleteKey);

        return "redirect:/inventoryList";
    }
}
