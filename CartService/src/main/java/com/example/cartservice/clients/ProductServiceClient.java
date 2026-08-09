package com.example.cartservice.clients;

import com.example.cartservice.dtos.ProductDto;
import com.example.cartservice.exceptions.ProductNotFoundException;
import com.example.cartservice.exceptions.ProductServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

// Fetches canonical product name/price from ProductCatalogService so add-to-cart
// can't be tampered with by a client-supplied price.
@Component
public class ProductServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public ProductServiceClient(RestTemplate restTemplate,
                                 @Value("${product-catalog-service.base-url}") String baseUrl)
                                 // @Value annotation injects the value of the 
                                 // property "product-catalog-service.base-url" from the application properties 
                                 // into the baseUrl parameter.
                                 {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public ProductDto getProductById(Long productId) {
        try {
            return restTemplate.getForObject(baseUrl + "/products/{id}", ProductDto.class, productId);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductNotFoundException("Product not found for id: " + productId);
        } catch (RestClientException ex) {
            throw new ProductServiceUnavailableException("ProductCatalogService is unavailable", ex);
        }
    }
}
