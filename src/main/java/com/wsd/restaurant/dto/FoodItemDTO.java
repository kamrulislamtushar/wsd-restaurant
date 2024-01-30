package com.wsd.restaurant.dto;

import com.wsd.restaurant.enumeration.FoodType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.wsd.domain.FoodItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FoodItemDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private FoodType foodType;

    @NotNull
    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public FoodType getFoodType() {
        return foodType;
    }

    public void setFoodType(FoodType foodType) {
        this.foodType = foodType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FoodItemDTO)) {
            return false;
        }

        FoodItemDTO foodItemDTO = (FoodItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, foodItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FoodItemDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
