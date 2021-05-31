package com.se.letscook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.se.letscook.ArrayAdapterAllRecipes;
import com.se.letscook.LetsCookApp;
import com.se.letscook.R;
import com.se.letscook.models.Recipe;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean appStart = true;
    private ArrayAdapterAllRecipes arrayAdapterAllRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

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
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);

        // Logo beim Appstart zeigen
        if(appStart) {
            new Handler().postDelayed((Runnable) () -> ((DrawerLayout)findViewById(R.id.drawer_layout)).setFocusable(false),1500);

            ((View) findViewById(R.id.logoView)).setVisibility(View.VISIBLE);
            ((View) findViewById(R.id.logoView)).animate().alpha(0).setDuration(400).setStartDelay(1500).withEndAction((Runnable) () -> {
                ((View) findViewById(R.id.logoView)).setVisibility(View.GONE);
                ((View) findViewById(R.id.logoView)).setFocusable(false);
            });
            appStart = false;
        }

        ListView listView = findViewById(R.id.list);
        arrayAdapterAllRecipes = new ArrayAdapterAllRecipes(getApplicationContext(), R.layout.list_layout_all_recipes);
        listView.setAdapter(arrayAdapterAllRecipes);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            arrayAdapterAllRecipes.getItem(position);

            Intent intent = new Intent(view.getContext(), RecipeActivity.class);
            intent.putExtra("Recipe", arrayAdapterAllRecipes.getItem(position));
            startActivity(intent);
        });

        ((LetsCookApp) getApplication()).getRepository().getAllRecipes();
    }

    // Observer werden aufgerufen, falls die Rezeptliste sich verändert
    private void addObserver(){
        ((LetsCookApp) getApplication()).getRepository().getAllRecipesWithoutUpdating().observe(this, list -> {

            arrayAdapterAllRecipes.clear();
            if(list != null)
                for (Recipe recipe: list ) {
                    arrayAdapterAllRecipes.add(recipe);
                }
        });

        List<Recipe> recipeList = ((LetsCookApp) getApplication()).getRepository().getAllRecipesWithoutUpdating().getValue();
        if(recipeList != null){
            arrayAdapterAllRecipes.clear();
            for (Recipe recipe: recipeList ) {
                arrayAdapterAllRecipes.add(recipe);
            }
        }
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
    protected void onPause(){
        super.onPause();
        ((LetsCookApp) getApplication()).getRepository().getAllRecipesWithoutUpdating().removeObservers(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        addObserver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            ((LetsCookApp) getApplication()).getRepository().getAllRecipes();
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
