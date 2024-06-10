package hska.iwi.eShopMaster.model.businessLogic.manager.impl;


import hska.iwi.eShopMaster.model.businessLogic.manager.CategoryManager;
import hska.iwi.eShopMaster.model.database.dataobjects.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import static hska.iwi.eShopMaster.model.businessLogic.manager.impl.Executer.delete;
import static hska.iwi.eShopMaster.model.businessLogic.manager.impl.Executer.post;
import static hska.iwi.eShopMaster.model.businessLogic.manager.impl.Executer.get;

public class CategoryManagerImpl implements CategoryManager {
    private final String categoryEndpoint = "http://category-service:8080/categories/";
    private final Gson gson = new Gson();

    public CategoryManagerImpl() {
    }

    public List<Category> getCategories() {
        try {
            String response = get(categoryEndpoint);
            Category[] categories = gson.fromJson(response, Category[].class);
            return Arrays.asList(categories);
        } catch (Exception e) {
            return new ArrayList<Category>();
        }
    }


    public Category getCategory(int id) {
        try {
            String response = get(categoryEndpoint + id);
            return gson.fromJson(response, Category.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Category getCategoryByName(String name) {
        return null;
    }

    public void addCategory(String name) {
        Category cat = new Category(name);

        String body = gson.toJson(cat);
        try {
            post(categoryEndpoint, body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delCategory(Category cat) {
        this.delCategoryById(cat.getId());

    }

    public void delCategoryById(int id) {
        try {
            delete(categoryEndpoint + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}