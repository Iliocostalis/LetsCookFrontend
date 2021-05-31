package com.se.letscook.models;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName(value = "id")
    public String id;

    @SerializedName(value = "image")
    public String imageAsString;
}
