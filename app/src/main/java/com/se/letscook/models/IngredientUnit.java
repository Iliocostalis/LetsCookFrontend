package com.se.letscook.models;

import com.google.gson.annotations.SerializedName;

public enum IngredientUnit
{
    @SerializedName(value = "g")
    GRAMM("g"),

    @SerializedName(value = "kg")
    KILOGRAMM("kg"),

    @SerializedName(value = "l")
    LITER("l"),

    @SerializedName(value = "ml")
    MILLILITER("ml"),

    @SerializedName(value = "el")
    ESSLÖFFEL("el"),

    @SerializedName(value = "tl")
    TEELÖFFEL("tl"),

    @SerializedName(value = "stueck")
    STÜCK("stueck"),

    @SerializedName(value = "packung")
    PACKUNG("packung"),

    @SerializedName(value = "")
    EMPTY("");

    private String dbName;

    IngredientUnit(String dbName)
    {
        this.dbName = dbName;
    }

    public String getDbName()
    {
        return this.dbName;
    }

    public static IngredientUnit fromDbName(String name)
    {
        name = name.toLowerCase();
        for(IngredientUnit iu : values())
        {
            if(iu.dbName.equals(name))
            {
                return iu;
            }
        }
        return null;
    }

}
