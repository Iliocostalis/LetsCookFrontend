package com.se.letscook.activity;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.se.letscook.LetsCookApp;
import com.se.letscook.R;
import com.se.letscook.models.Ingredient;
import com.se.letscook.models.IngredientUnit;
import com.se.letscook.models.Instruction;
import com.se.letscook.models.Recipe;

public class RecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_activity);

        // Toolbar mit dem DrawerLayout verbinden
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // verbinden abgeschlossen

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Rezept aus SerializableExtra laden und inhalte der Textfelder dementsprechend setzen
        selectedRecipe = (Recipe) getIntent().getSerializableExtra("Recipe");

        ((TextView) findViewById(R.id.title)).setText(selectedRecipe.getTitle());

        String description = "";
        for (Instruction instruction: selectedRecipe.getInstructions()) {
            description += instruction.getText() + "\n\n";
        }
        ((TextView) findViewById(R.id.description)).setText(description);

        String person = "";
        if(selectedRecipe.getAmountPeople() == 1){
            person = "Zutaten für " + selectedRecipe.getAmountPeople() + " Person:";
        }
        else{
            person = "Zutaten für " + selectedRecipe.getAmountPeople() + " Personen:";
        }
        ((TextView) findViewById(R.id.textView5)).setText(person);

        String ingredients = "";
        for (Ingredient ingredient: selectedRecipe.getIngredients()) {
            ingredients += ingredient.getName() + "\n";
        }
        ((TextView) findViewById(R.id.ingredients)).setText(ingredients);

        String amount = "";
        IngredientUnit stuck = IngredientUnit.STÜCK;
        for (Ingredient ingredient: selectedRecipe.getIngredients()) {
            if(ingredient.getAmount() != 0) {
                amount += ingredient.getAmount() + " ";
                IngredientUnit unit = ingredient.getUnit();
                if (unit == null || unit == stuck)
                    amount += "\n ";
                else
                    amount += unit.getDbName() + "\n ";
            }
            else {amount += "\n ";}
        }
        amount = amount.replace(".0", "");
        ((TextView) findViewById(R.id.amount)).setText(amount);


        // Bild aus der Datenbank laden
        LiveData<Bitmap> bitmapLiveData = ((LetsCookApp) getApplication()).getRepository().fetchSingleImage(selectedRecipe.getId());
        bitmapLiveData.observe(this, bitmap -> {
            selectedRecipe.setImage(bitmap);
            ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
            bitmapLiveData.removeObservers(this);
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change) {
            Intent intent = new Intent(this, InsertRecipeActivity.class);
            intent.putExtra("ChangeRecipe",true);
            startActivity(intent);
            return true;

        }else if (id == R.id.action_delete) {
            LiveData<Boolean> liveData = ((LetsCookApp) getApplication()).getRepository().deleteRecipe(selectedRecipe.getId());
            liveData.observe(this, value -> {
                liveData.removeObservers(this);

                if(value == null || !value)
                    Toast.makeText(this, "Löschen fehlgeschlagen", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(this, "Löschen erfolgreich", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Methode, die aufgerufen wird falls ein Butten in der "linken" Liste betätigt wurde
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_makeRecipe) {
            Intent intent = new Intent (this, InsertRecipeActivity.class);
            startActivity(intent);
        }

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }
}
