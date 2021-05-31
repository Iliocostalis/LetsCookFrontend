package com.se.letscook.models;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Recipe implements Comparable<Recipe>, Serializable
{
    @SerializedName(value = "id")
    private String id;

    @SerializedName(value = "title")
    private String title;

    @SerializedName(value = "people")
    private int amountPeople;

    @SerializedName(value = "description")
    private String description;

    @SerializedName(value = "ingredients")
    private List<Ingredient> ingredients;

    @SerializedName(value = "instructions")
    private List<Instruction> instructions;

    private transient Bitmap image;

    public Recipe()
    {
        this("", "", 0, "", new ArrayList<Ingredient>(), new ArrayList<Instruction>(), null);
    }

    public Recipe(String id, String title, int amountPeople, String description, List<Ingredient> ingredients, List<Instruction> instructions, Bitmap image)
    {
        this.id = id;
        this.title = title;
        this.amountPeople = amountPeople;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.image = image;
    }

    public Recipe(String title, int amountPeople, String description, List<Ingredient> ingredients, List<Instruction> instructions, Bitmap image)
    {
        this.title = title;
        this.amountPeople = amountPeople;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getAmountPeople()
    {
        return amountPeople;
    }

    public void setAmountPeople(int amountPeople)
    {
        this.amountPeople = amountPeople;
    }

    public List<Ingredient> getIngredients()
    {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients)
    {
        this.ingredients = ingredients;
    }

    public List<Instruction> getInstructions()
    {
        return instructions;
    }

    public void setInstructions(List<Instruction> instructions)
    {
        this.instructions = instructions;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        Recipe recipe = (Recipe) o;
        return amountPeople == recipe.amountPeople &&
                Objects.equals(id, recipe.id) &&
                Objects.equals(title, recipe.title) &&
                Objects.equals(description, recipe.description) &&
                Objects.equals(ingredients, recipe.ingredients) &&
                Objects.equals(instructions, recipe.instructions) &&
                Objects.equals(image, recipe.image);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, title, amountPeople, description, ingredients, instructions, image);
    }

    @Override
    public int compareTo(@NonNull Recipe recipe)
    {
        return this.title.compareTo(recipe.title);
    }
}
