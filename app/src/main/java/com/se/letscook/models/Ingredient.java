package com.se.letscook.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Ingredient implements Comparable<Ingredient>, Serializable
{

    @SerializedName(value = "amount")
    private double amount;

    @SerializedName(value = "unit")
    private IngredientUnit unit;

    @SerializedName(value = "name")
    private String name;

    public Ingredient()
    {
        this(0, IngredientUnit.EMPTY, "");
    }

    public Ingredient(double amount, IngredientUnit unit, String name)
    {
        this.amount = amount;
        this.unit = unit;
        this.name = name;
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        this.amount = amount;
    }

    public IngredientUnit getUnit()
    {
        return unit;
    }

    public void setUnit(IngredientUnit unit)
    {
        this.unit = unit;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
        Ingredient that = (Ingredient) o;
        return Double.compare(that.amount, amount) == 0 &&
                unit == that.unit &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(amount, unit, name);
    }

    @Override
    public int compareTo(@NonNull Ingredient ingredient)
    {
        return this.name.compareTo(ingredient.name);
    }
}
