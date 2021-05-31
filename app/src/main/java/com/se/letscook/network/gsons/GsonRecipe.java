package com.se.letscook.network.gsons;

import com.google.gson.annotations.SerializedName;
import com.se.letscook.models.Ingredient;
import com.se.letscook.models.Instruction;

import java.util.ArrayList;
import java.util.List;

public class GsonRecipe
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

    @SerializedName(value = "image")
    private String hexString;

    public GsonRecipe()
    {
        this("", "", 0, "", new ArrayList<Ingredient>(), new ArrayList<Instruction>(), null);
    }

    public GsonRecipe(String id, String title, int amountPeople, String description, List<Ingredient> ingredients, List<Instruction> instructions, String hexString)
    {
        this.id = id;
        this.title = title;
        this.amountPeople = amountPeople;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.hexString = hexString;
    }

}
