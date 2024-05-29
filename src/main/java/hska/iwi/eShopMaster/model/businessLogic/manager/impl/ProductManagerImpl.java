package hska.iwi.eShopMaster.model.businessLogic.manager.impl;

import com.google.gson.Gson;
import hska.iwi.eShopMaster.model.businessLogic.manager.CategoryManager;
import hska.iwi.eShopMaster.model.businessLogic.manager.ProductManager;
import hska.iwi.eShopMaster.model.database.dataobjects.Category;
import hska.iwi.eShopMaster.model.database.dataobjects.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hska.iwi.eShopMaster.model.businessLogic.manager.impl.RESTHelper.delete;
import static hska.iwi.eShopMaster.model.businessLogic.manager.impl.RESTHelper.post;
import static hska.iwi.eShopMaster.model.businessLogic.manager.impl.RESTHelper.get;

public class ProductManagerImpl implements ProductManager {
    private final String productEndpoint = "http://product-service:8080/products/";
    private final Gson gson = new Gson();

    public ProductManagerImpl() {
    }

    public List<Product> getProducts() {
        try {
            String response = get(productEndpoint);
            Product[] products = gson.fromJson(response, Product[].class);
            getCategories(products);
            return Arrays.asList(products);
        } catch (Exception e) {
            return new ArrayList<Product>();
        }

    }

    public List<Product> getProductsForSearchValues(String searchDescription,
                                                    Double searchMinPrice, Double searchMaxPrice) {
        StringBuilder builder = new StringBuilder().append(productEndpoint).append("?search=").append(searchDescription);
        if (searchMinPrice != null)
            builder.append("&minPrice=").append(searchMinPrice);
        if (searchMaxPrice != null)
            builder.append("&maxPrice=").append(searchMaxPrice);
        try {
            String response = get(builder.toString());
            Product[] products = gson.fromJson(response, Product[].class);
            getCategories(products);
            return Arrays.asList(products);
        } catch (Exception e) {
            return new ArrayList<Product>();
        }
    }

    public Product getProductById(int id) {
        try {
            String response = get(productEndpoint + id);
            Product product = gson.fromJson(response, Product.class);
            product.setCategory(new CategoryManagerImpl().getCategory(product.categoryId));
            return product;
        } catch (Exception e) {
            return null;
        }
    }

    public Product getProductByName(String name) {
        return null;
    }

    public int addProduct(String name, double price, int categoryId, String details) {
        CategoryManager categoryManager = new CategoryManagerImpl();
        Category category = categoryManager.getCategory(categoryId);

        if (category == null) return -1;

        Product product;
        if (details == null) {
            product = new Product(name, price, category);
        } else {
            product = new Product(name, price, category, details);
        }

        product.categoryId = categoryId;
        try {
            String response = post(productEndpoint, gson.toJson(product));
            return gson.fromJson(response, Product.class).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public void deleteProductById(int id) {
        try {
            delete(productEndpoint + id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteProductsByCategoryId(int categoryId) {
        // TODO Auto-generated method stub
        return false;
    }

    private void getCategories(Product[] products) {
        CategoryManager categoryManager = new CategoryManagerImpl();
        for(Product product: products){
            product.setCategory(categoryManager.getCategory(product.categoryId));
        }
    }

}