package edu.manipal.logistics.InventoryManager.Controller;

import org.apache.poi.ss.usermodel.*;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import edu.manipal.logistics.InventoryManager.business.database.GoogleDatastore;
import edu.manipal.logistics.InventoryManager.business.entities.Category;
import edu.manipal.logistics.InventoryManager.business.entities.InventoryItem;
import edu.manipal.logistics.InventoryManager.business.entities.UserInfo;
import edu.manipal.logistics.InventoryManager.business.service.LoginService;
import edu.manipal.logistics.InventoryManager.business.entities.CategoryItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@SessionAttributes()
public class myController {

    public static String cleanString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        // Remove special characters but allow spaces, letters, and numbers
        String cleaned = input.replaceAll("[^a-zA-Z0-9 ]", "");
        // Replace multiple spaces with a single space
        cleaned = cleaned.replaceAll("\\s+", " ");
        // Trim leading and trailing spaces
        cleaned = cleaned.trim();
        // Convert to lowercase
        cleaned = cleaned.toLowerCase();

        return cleaned;
    }

    private boolean isUserLoggedIn(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("name") != null;
    }

    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String validLogin(HttpServletRequest req, Model model, @RequestParam String name,
            @RequestParam String password) {
        HttpSession session = req.getSession();
        session.setAttribute("name", name);

        LoginService service = new LoginService();
        boolean isValidUser = service.validateUser(name, password);
        if (!isValidUser) {
            model.addAttribute("errorMessage", "Access Denied , Invalid Credentials");
            return "redirect:/";
        }

        model.addAttribute("name", name);
        model.addAttribute("password", password);

        return "redirect:/categories";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    @PostMapping("/createUser")
    public String createUser(HttpServletRequest req, Model model, @RequestParam String name,
            @RequestParam String password) {
        GoogleDatastore gd = new GoogleDatastore();
        if (gd.existsUser(name)) {
            model.addAttribute("errorMessage", "Username Taken");
            return "redirect:/";
        }

        UserInfo ui = new UserInfo();
        ui.setName(name);
        ui.setPassword(password);
        gd.saveUserInfo(ui);

        return "redirect:/";
    }

    @GetMapping("/categories")
    public String categoriesPage(HttpServletRequest req,
            @RequestParam(value = "selectedCategory", required = false) String selectedCategory, Model model) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        List<Category> categories = gd.getAllCategory();
        model.addAttribute("categories", categories);

        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            List<CategoryItem> categoryItems = gd.getAllItemsInCategory(selectedCategory);

            Map<Long, InventoryItem> inventoryItemMap = new HashMap<>();
            for (CategoryItem categoryItem : categoryItems) {
                InventoryItem item = gd.getInventoryItem(categoryItem.getItemId());
                if (item != null) {
                    inventoryItemMap.put(item.getItemId(), item);
                }
            }

            model.addAttribute("categoryItems", categoryItems);
            model.addAttribute("selectedCategory", selectedCategory);
            model.addAttribute("inventoryItemMap", inventoryItemMap);

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
        name = cleanString(name);

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
    public String newCategoryItem(HttpServletRequest req, @RequestParam String category, @RequestParam String itemKey,
            @RequestParam Long requested, @RequestParam Long given) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        category = cleanString(category);
        itemKey = cleanString(itemKey);

        // implement fuzzy matching here

        if (category.length() > 0 && itemKey.length() > 0) {
            GoogleDatastore gd = new GoogleDatastore();

            CategoryItem ci = new CategoryItem();
            InventoryItem ii;
            if (gd.existsInventoryItem(itemKey)) {
                ii = gd.getInventoryItem(itemKey);
            } else {
                ii = new InventoryItem();
                ii.setItemKey(itemKey);
                gd.saveInventoryItem(ii);
            }

            ci.setCategoryKey(category);
            ci.setItemId(ii.getItemId());
            ci.setGiven(given);
            ci.setRequested(requested);

            if (!gd.existsCategoryItem(itemKey, category)) {
                ii.changeRequested(requested);
                ii.changeGiven(given);
                ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());
                gd.saveInventoryItem(ii);
            } else {
                CategoryItem oldci = gd.getCategoryItem(itemKey, category);
                ii.changeGiven(given - oldci.getGiven());
                ii.changeRequested(requested - oldci.getRequested());
                ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());
                gd.saveInventoryItem(ii);
            }

            gd.saveCategoryItem(ci);
        }

        return "redirect:/categories?selectedCategory=" + category;
    }

    @PostMapping("/editItemName")
    public String editItemName(HttpServletRequest req, @RequestParam String category, @RequestParam Long itemId,
            @RequestParam String newName) {
        try {
            GoogleDatastore gd = new GoogleDatastore();
            CategoryItem ci = gd.getCategoryItem(itemId, category);
            InventoryItem oldii = gd.getInventoryItem(itemId);
            oldii.changeRequested(-ci.getRequested());
            oldii.changeGiven(-ci.getGiven());

            oldii.setOrder(oldii.getRequested() - oldii.getQuantity() - oldii.getGiven() + oldii.getReceived());

            gd.deleteCategoryItem(category, oldii.getItemId());

            InventoryItem ii;
            if (gd.existsInventoryItem(newName)) {
                ii = gd.getInventoryItem(newName);
            } else {
                ii = new InventoryItem();
                ii.setItemKey(newName);
                gd.saveInventoryItem(ii);
            }

            ci.setItemId(ii.getItemId());
            ii.changeGiven(ci.getGiven());
            ii.changeRequested(ci.getRequested());

            ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());

            gd.saveInventoryItem(oldii);
            gd.saveInventoryItem(ii);
            gd.saveCategoryItem(ci);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/categories?selectedCategory=" + category;
    }

    @PostMapping("/deleteCategoryItem")
    public String deleteCategoryItem(HttpServletRequest req, @RequestParam String category,
            @RequestParam String itemKey) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();

        InventoryItem ii = gd.getInventoryItem(itemKey);
        CategoryItem ci = gd.getCategoryItem(itemKey, category);
        ii.changeRequested(-ci.getRequested());
        ii.changeGiven(-ci.getGiven());
        ii.setOrder(ii.getRequested() - ii.getQuantity() - ii.getGiven() + ii.getReceived());

        gd.deleteCategoryItem(itemKey, category);

        if (ii.getQuantity() == 0 && ii.getRequested() == 0 && ii.getGiven() == 0)
            gd.deleteInventoryItem(itemKey);
        else
            gd.saveInventoryItem(ii);

        return "redirect:/categories?selectedCategory=" + category;
    }

    @GetMapping("/itemList")
    public String itemListPage(HttpServletRequest req, Model model) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        List<InventoryItem> items = gd.getItemsToOrder();
        model.addAttribute("items", items);

        return "itemList";
    }

    @PostMapping("/changeItem")
    public String changeRecieved(HttpServletRequest req, @RequestParam String itemKey, @RequestParam Long received,
            @RequestParam String vendor) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        vendor = cleanString(vendor);
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
    public String newItem(HttpServletRequest req, @RequestParam String itemKey, @RequestParam Long quantity,
            @RequestParam Long requested) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        itemKey = cleanString(itemKey);

        if (itemKey.length() > 0 && quantity > 0) {
            InventoryItem ii;
            GoogleDatastore gd = new GoogleDatastore();
            Long change = 0L;

            if (gd.existsInventoryItem(itemKey)) {
                ii = gd.getInventoryItem(itemKey);
                change = quantity - ii.getQuantity();
            } else {
                ii = new InventoryItem();
            }
            ii.setItemKey(itemKey);
            ii.setQuantity(quantity);
            ii.setRequested(requested);

            ii.setOrder(ii.getOrder() - change);

            gd.saveInventoryItem(ii);
        }

        return "redirect:/inventoryList";
    }

    @PostMapping("/uploadInventoryItemExcel")
    public String uploadInventoryItemExcel(HttpServletRequest req, @RequestParam MultipartFile file) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            GoogleDatastore gd = new GoogleDatastore();
            List<InventoryItem> li = new ArrayList<InventoryItem>();

            for (int i = 1; i <= sheet.getLastRowNum() && i < 500; i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                Cell itemKeyCell = row.getCell(0);
                Cell quantityCell = row.getCell(1);

                if (itemKeyCell == null || quantityCell == null)
                    break;

                String itemKey = itemKeyCell.getStringCellValue();
                itemKey = cleanString(itemKey);
                Long quantity = (long) quantityCell.getNumericCellValue();

                if (itemKey.length() == 0 || quantity == 0L)
                    break;

                InventoryItem ii = new InventoryItem();
                ii.setItemKey(itemKey);
                ii.setQuantity(quantity);
                li.add(ii);
            }
            System.err.println();
            gd.saveInventoryItemList(li);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/inventoryList";
    }

    @PostMapping("/editInventoryItemName")
    public String editInvantoryItemName(HttpServletRequest req, @RequestParam Long itemId,
            @RequestParam String newName) {

        GoogleDatastore gd = new GoogleDatastore();
        InventoryItem ii = gd.getInventoryItem(itemId);
        ii.setItemKey(newName);
        gd.saveInventoryItem(ii);

        return "redirect:/inventoryList";
    }

    @PostMapping("/deleteItem")
    public String deleteItem(HttpServletRequest req, @RequestParam String deleteKey) {
        if (!isUserLoggedIn(req)) {
            return "redirect:/";
        }

        GoogleDatastore gd = new GoogleDatastore();
        InventoryItem ii = gd.getInventoryItem(deleteKey);
        if (ii.getRequested() <= 0)
            gd.deleteInventoryItem(deleteKey);

        return "redirect:/inventoryList";
    }
}
