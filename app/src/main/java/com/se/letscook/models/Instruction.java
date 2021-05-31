package com.se.letscook.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Objects;

public class Instruction implements Comparable<Instruction>, Serializable
{
    @SerializedName(value = "text")
    private String text;

    public Instruction()
    {
        this("");
    }

    public Instruction(String text)
    {
        this.text = text;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
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
        Instruction that = (Instruction) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(text);
    }

    @Override
    public int compareTo(@NonNull Instruction instruction)
    {
        return this.text.compareTo(instruction.text);
    }
}

