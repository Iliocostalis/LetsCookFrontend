package com.se.letscook.activity;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.se.letscook.ArrayAdapterIngredients;
import com.se.letscook.LetsCookApp;
import com.se.letscook.R;
import com.se.letscook.models.Ingredient;
import com.se.letscook.models.IngredientUnit;
import com.se.letscook.models.Instruction;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.RecipeResponse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InsertRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextInputLayout textInputTitle;
    private TextInputLayout textInputDescription;
    private TextInputLayout textInputAmountPeople;
    private TextInputLayout textInputInstruction;

    private ArrayAdapter<IngredientUnit> adapterIngredient;
    private ArrayAdapterIngredients ingredientListAdapter;
    private ListView listView;
    private Recipe recipe;

    private static int RESULT_LOAD_IMAGE = 22;
    private boolean createNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_recipe_activity);

        textInputTitle = findViewById(R.id.title);
        textInputDescription = findViewById(R.id.description);
        textInputAmountPeople = findViewById(R.id.amountPeople);
        textInputInstruction = findViewById(R.id.instruction);

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
        navigationView.getMenu().findItem(R.id.nav_makeRecipe).setChecked(true);



        // Spinner für die Auswahl der Einheiten erstellen
        List<IngredientUnit> unit = new ArrayList<>(Arrays.asList(IngredientUnit.values()));
        adapterIngredient = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unit);
        adapterIngredient.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        listView = findViewById(R.id.amount);
        ingredientListAdapter = new ArrayAdapterIngredients(this, R.layout.list_layout_ingredients, listView);
        listView.setAdapter(ingredientListAdapter);



        if(getIntent().getBooleanExtra("ChangeRecipe",false)){
            createNew = false;
            loadRecipe();
        }else
            recipe = new Recipe();
    }

    private void loadRecipe(){
        recipe = RecipeActivity.selectedRecipe;
        textInputTitle.getEditText().setText(recipe.getTitle());
        textInputDescription.getEditText().setText(recipe.getDescription());
        textInputAmountPeople.getEditText().setText(String.valueOf(recipe.getAmountPeople()));
        String instructions = "";
        for(Instruction instruction : recipe.getInstructions()){
            if(!instructions.equals(""))
                instructions += "\n\n";
            instructions += instruction.getText();
        }
        textInputInstruction.getEditText().setText(instructions);
        ((ImageView)findViewById(R.id.imageViewInsert)).setImageBitmap(recipe.getImage());

        for(Ingredient ingredient : recipe.getIngredients())
            ingredientListAdapter.add(ingredient);
    }

    private boolean validateTitle(){
        String titleInput = textInputTitle.getEditText().getText().toString().trim();

        if(titleInput.isEmpty()){
            textInputTitle.setError("Feld kann nicht leer sein");
            return false;
        }else{
            textInputTitle.setError(null);
            return true;
        }
    }

    private boolean validateAmountPeople(){
        String amountPeopleInput = textInputAmountPeople.getEditText().getText().toString().trim();

        if(amountPeopleInput.isEmpty()){
            textInputAmountPeople.setError("Feld kann nicht leer sein");
            return false;
        }else{
            textInputAmountPeople.setError(null);
            return true;
        }
    }

    private boolean validateDescription(){
        String descriptionInput = textInputDescription.getEditText().getText().toString();

        if(descriptionInput.isEmpty()){
            textInputDescription.setError("Feld kann nicht leer sein");
            return false;
        }else if (descriptionInput.length() > 200){
            textInputDescription.setError("Kurzbeschreibung ist zu lang");
            return false;
        }else{
            textInputDescription.setError(null);
            return true;
        }
    }

    private boolean validateInstruction(){
        String instructionInput = textInputInstruction.getEditText().getText().toString().trim();

        if(instructionInput.isEmpty()){
            textInputInstruction.setError("Feld kann nicht leer sein");
            return false;
        }else{
            textInputInstruction.setError(null);
            return true;
        }
    }

    public void selectIngredient(View view){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);

        if(inflater == null)
            return;

        View popupView = inflater.inflate(R.layout.select_ingredient, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

        popupWindow.setAnimationStyle(R.style.AnimationIngredient);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        Spinner spinner = popupView.findViewById(R.id.ingridientunit);
        spinner.setAdapter(adapterIngredient);

        // Falls auserhalb des vorgesehenen Bereichs geklickt wird, wird das Popup Fenster geschlossen
        ((FrameLayout)popupView.findViewById(R.id.backgroundView1)).setOnClickListener(v-> popupWindow.dismiss());
        ((FrameLayout)popupView.findViewById(R.id.backgroundView2)).setOnClickListener(v-> popupWindow.dismiss());
        ((FrameLayout)popupView.findViewById(R.id.backgroundView3)).setOnClickListener(v-> popupWindow.dismiss());
        ((FrameLayout)popupView.findViewById(R.id.backgroundView4)).setOnClickListener(v-> popupWindow.dismiss());

        ((Button)popupView.findViewById(R.id.cancelButton)).setOnClickListener((View.OnClickListener) v -> popupWindow.dismiss());

        ((Button)popupView.findViewById(R.id.addButton)).setOnClickListener((View.OnClickListener) v -> {

            if(((EditText)popupView.findViewById(R.id.ingredient)).getText().toString().equals("") ||
                    ((EditText)popupView.findViewById(R.id.amount)).getText().toString().equals(""))
            {
                Toast.makeText(this, "Überprüfe die Eingaben", Toast.LENGTH_SHORT).show();
                return;
            }


            Ingredient ingredient = new Ingredient();

            ingredient.setName( ((EditText)popupView.findViewById(R.id.ingredient)).getText().toString() );
            ingredient.setAmount(Double.parseDouble(((EditText)popupView.findViewById(R.id.amount)).getText().toString()));
            ingredient.setUnit((IngredientUnit)spinner.getSelectedItem());

            ingredientListAdapter.add(ingredient);

            ingredientListAdapter.setListViewHeight(listView);

            popupWindow.dismiss();
        });
    }

    public void save(View v){

        if(!validateDescription() | !validateTitle() | !validateAmountPeople() | !validateInstruction())
        {
            Toast.makeText(this, "Überprüfe die Eingaben", Toast.LENGTH_SHORT).show();
            return;
        }else if(ingredientListAdapter.itemList.size() == 0){
            Toast.makeText(this, "Zutaten können nicht leer sein", Toast.LENGTH_SHORT).show();
            return;
        }

        recipe.setIngredients(ingredientListAdapter.itemList);
        recipe.setTitle(((TextInputLayout)findViewById(R.id.title)).getEditText().getText().toString());
        recipe.setDescription(((TextInputLayout)findViewById(R.id.description)).getEditText().getText().toString());

        ArrayList<Instruction> instructions = new ArrayList<>();
        Instruction instruction = new Instruction(((TextInputLayout)findViewById(R.id.instruction)).getEditText().getText().toString());
        instructions.add(instruction);

        recipe.setInstructions(instructions);

        recipe.setAmountPeople(Integer.parseInt(((TextInputLayout)findViewById(R.id.amountPeople)).getEditText().getText().toString()));

        Button saveButton = findViewById(R.id.save);
        saveButton.setClickable(false);


        if(createNew){
            LiveData<RecipeResponse> response = ((LetsCookApp) getApplication()).getRepository().postRecipe(recipe);
            response.observe(this, code -> {
                response.removeObservers(this);

                if(code == RecipeResponse.SUCCESS){
                    Toast.makeText(this, "Upload erfolgreich", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "Upload fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    saveButton.setClickable(true);
                }
            });
        }else{
            LiveData<RecipeResponse> response = ((LetsCookApp) getApplication()).getRepository().putRecipe(recipe);
            response.observe(this, code -> {
                response.removeObservers(this);

                if(code == RecipeResponse.SUCCESS){
                    Toast.makeText(this, "Upload erfolgreich", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "Upload fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    saveButton.setClickable(true);
                }
            });
        }
    }

    public void close(View v){
        onBackPressed();
    }

    public void addPicture(View v){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
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
        getMenuInflater().inflate(R.menu.empty, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // photoPickerIntent data
        // Bild wird geladen, falls eins ausgewählt wurde
        if (resultCode == RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            try {
                final Uri imageUri = data.getData();

                if(imageUri == null)
                    throw new NullPointerException();

                final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                recipe.setImage(BitmapFactory.decodeStream(imageStream));
                ((ImageView)findViewById(R.id.imageViewInsert)).setImageBitmap(recipe.getImage());

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "You haven't picked a Image",Toast.LENGTH_SHORT).show();
        }
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
