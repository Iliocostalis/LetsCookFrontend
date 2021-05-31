package com.se.letscook;

import com.se.letscook.models.Ingredient;
import com.se.letscook.models.Instruction;
import com.se.letscook.models.Recipe;

import junit.framework.Assert;

import org.junit.Test;

public class RecipeTests
{

    @Test
    public void testRecipeEqualsEmptyShouldBeTrue()
    {
        Recipe one = new Recipe();
        Recipe two = new Recipe();

        Assert.assertEquals(one, two);
    }

    @Test
    public void testRecipeEqualsFullShouldBeTrue()
    {
        Recipe one = new Recipe();
        one.setId("1");
        one.setTitle("Testname");
        one.setAmountPeople(2);
        one.setDescription("Description");
        one.getIngredients().add(new Ingredient());
        one.getIngredients().add(new Ingredient());
        one.getInstructions().add(new Instruction());

        Recipe two = new Recipe();
        two.setId("1");
        two.setTitle("Testname");
        two.setAmountPeople(2);
        two.setDescription("Description");
        two.getIngredients().add(new Ingredient());
        two.getIngredients().add(new Ingredient());
        two.getInstructions().add(new Instruction());

        Assert.assertEquals(one, two);
    }

}
