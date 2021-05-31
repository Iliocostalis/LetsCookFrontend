package com.se.letscook;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.se.letscook.models.Ingredient;
import com.se.letscook.models.IngredientUnit;
import com.se.letscook.models.Instruction;
import com.se.letscook.models.Recipe;
import com.se.letscook.network.RequestQueueHandler;
import com.se.letscook.network.UTF8StringRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RequestQueueHandler.class, OnlineRepository.class})
public class OnlineRepositoryGetTest {

    @Rule
    InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private Context con;

    private OnlineRepositoryTestHelper testHelper;

    @Before
    public void setUp() throws Exception {
        con = Mockito.mock(Context.class);
        testHelper = new OnlineRepositoryTestHelper();

        // Get fake RequestQueue over fake RequestQueueHandler
        PowerMockito.mockStatic(RequestQueueHandler.class);

        RequestQueueHandler mockRequestQueueHandler = Mockito.mock(RequestQueueHandler.class);
        RequestQueue mockRequestQueue = Mockito.mock(RequestQueue.class);

        PowerMockito.when(RequestQueueHandler.getInstance(any())).thenReturn(mockRequestQueueHandler);
        Mockito.when(mockRequestQueueHandler.getRequestQueue()).thenReturn(mockRequestQueue);

        // Save Mock-UTF8StringRequest with linked Data and return Mock-UTF8StringRequest
        PowerMockito.whenNew(UTF8StringRequest.class).withAnyArguments().then(invocation -> {
            UTF8StringRequest mockRequest = Mockito.mock(UTF8StringRequest.class);
            testHelper.saveRequest(mockRequest, invocation);
            return mockRequest;
        });

        // Add UTF8StringRequest to Queue (like normal RequestQueue.add(Request)) -> Handled in TestHelper
        Mockito.when(mockRequestQueue.add(any(UTF8StringRequest.class))).thenAnswer(invocation -> {
            testHelper.addToQueue(invocation.getArgument(0));
            return null;
        });

        // Start RequestQueue (like normal RequestQueue.start() -> Handled in TestHelper
        Mockito.doAnswer(invocation -> {
            testHelper.executeQueue();
            return null;
        }).when(mockRequestQueue).start();
    }

    @Test
    public void correctApiCallForAllRecipes() {
        testHelper.setReturnJson(new String[]{"[{\"id\":\"1\",\"title\":\"Halloumi mit Linsengemüse\",\"people\":2,\"description\":\"Das ist die Beschreibung des Rezepts\",\"ingredients\":[{\"amount\":150,\"unit\":\"g\",\"name\":\"Kirschtomaten\"},{\"amount\":1,\"unit\":\"stueck\",\"name\":\"Knoblauchzehe\"},{\"amount\":1,\"unit\":\"packung\",\"name\":\"braune Linsen\"},{\"amount\":0.5,\"unit\":\"kg\",\"name\":\"Zitrone\"},{\"amount\":8,\"unit\":\"l\",\"name\":\"Honig\"},{\"amount\":17,\"unit\":\"ml\",\"name\":\"körniger Senf\"},{\"amount\":75,\"unit\":\"el\",\"name\":\"Rucola\"},{\"amount\":20,\"unit\":\"tl\",\"name\":\"getrocknete Cranberrys\"}],\"instructions\":[{\"text\":\"Heize den Backofen auf 220 °C Ober-/Unterhitze (200 °C Umluft) vor. Ungeschälte Süßkartoffeln in 1 cm breite Spalten schneiden und auf ein mit Backpapier belegtes Backblech geben, etwas Platz für die Tomaten lassen. Süßkartoffeln salzen*, pfeffern* und 25 Min. im Backofen backen, bis sie weich sind.\"},{\"text\":\"Knoblauch und Schalotte abziehen, Schalotte in feine Streifen schneiden. Braune Linsen in ein Sieb geben und mit kaltem Wasser spülen, bis dieses klar hindurchläuft. Zitrone in 6 Spalten schneiden. 2 Zitronenspalten in eine große Schüssel pressen und mit 2 EL Olivenöl*, Senf, Honig, Salz* und Pfeffer* zu einem Dressing glatt rühren.\"}]},{\"id\":\"2\",\"title\":\"Köttbullar! Schwedische Hackbällchen\",\"people\":2,\"description\":\"Wow, eine zweite Beschreibung!\",\"ingredients\":[{\"amount\":300,\"unit\":\"g\",\"name\":\"gemischtes Hackfleisch\"}],\"instructions\":[{\"text\":\"Kartoffeln schälen und vierteln. In einen großen Topf reichlich kaltes Wasser geben, salzen* und Kartoffelviertel zugeben. Wasser einmal aufkochen lassen und Kartoffelviertel für 8 – 10 Min. in leicht sprudelndem Wasser köcheln lassen. In der Zwischenzeit mit der Zubereitung fortfahren.\"}]}]",
                "{\"id\":\"1\",\"image\":\"abcd\"}", "{\"id\":\"1\",\"image\":\"abcd\"}"});
        OnlineRepository.getRepository(con).getAllRecipes();

        // List of expected executed Requests
        ArrayList<OnlineRepositoryTestHelper.RequestData> executedRequests = new ArrayList<>();
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.GET, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/"));
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.GET, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/1/image"));
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.GET, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/2/image"));

        testHelper.assertIfExecutedRequestsAreEqual(executedRequests);
    }

    @Test
    public void correctSerializationForAllRecipes() {
        // JSON returned, when Requests are executed
        testHelper.setReturnJson(new String[]{"[{\"id\":\"1\",\"title\":\"Halloumi mit Linsengemüse\",\"people\":2,\"description\":\"Das ist die Beschreibung des Rezepts\",\"ingredients\":[{\"amount\":150,\"unit\":\"g\",\"name\":\"Kirschtomaten\"},{\"amount\":1,\"unit\":\"stueck\",\"name\":\"Knoblauchzehe\"},{\"amount\":1,\"unit\":\"packung\",\"name\":\"braune Linsen\"},{\"amount\":0.5,\"unit\":\"kg\",\"name\":\"Zitrone\"},{\"amount\":8,\"unit\":\"l\",\"name\":\"Honig\"},{\"amount\":17,\"unit\":\"ml\",\"name\":\"körniger Senf\"},{\"amount\":75,\"unit\":\"el\",\"name\":\"Rucola\"},{\"amount\":20,\"unit\":\"tl\",\"name\":\"getrocknete Cranberrys\"}],\"instructions\":[{\"text\":\"Heize den Backofen auf 220 °C Ober-/Unterhitze (200 °C Umluft) vor. Ungeschälte Süßkartoffeln in 1 cm breite Spalten schneiden und auf ein mit Backpapier belegtes Backblech geben, etwas Platz für die Tomaten lassen. Süßkartoffeln salzen*, pfeffern* und 25 Min. im Backofen backen, bis sie weich sind.\"},{\"text\":\"Knoblauch und Schalotte abziehen, Schalotte in feine Streifen schneiden. Braune Linsen in ein Sieb geben und mit kaltem Wasser spülen, bis dieses klar hindurchläuft. Zitrone in 6 Spalten schneiden. 2 Zitronenspalten in eine große Schüssel pressen und mit 2 EL Olivenöl*, Senf, Honig, Salz* und Pfeffer* zu einem Dressing glatt rühren.\"}]},{\"id\":\"2\",\"title\":\"Köttbullar! Schwedische Hackbällchen\",\"people\":2,\"description\":\"Wow, eine zweite Beschreibung!\",\"ingredients\":[{\"amount\":300,\"unit\":\"g\",\"name\":\"gemischtes Hackfleisch\"}],\"instructions\":[{\"text\":\"Kartoffeln schälen und vierteln. In einen großen Topf reichlich kaltes Wasser geben, salzen* und Kartoffelviertel zugeben. Wasser einmal aufkochen lassen und Kartoffelviertel für 8 – 10 Min. in leicht sprudelndem Wasser köcheln lassen. In der Zwischenzeit mit der Zubereitung fortfahren.\"}]}]",
                "{\"id\":\"1\",\"image\":\"abcd\"}", "{\"id\":\"2\",\"image\":\"abcd\"}"});

        // Expected data
        List<Ingredient> firstIngredientList = new ArrayList<>();
        firstIngredientList.add(new Ingredient(150, IngredientUnit.GRAMM, "Kirschtomaten"));
        firstIngredientList.add(new Ingredient(1, IngredientUnit.STÜCK, "Knoblauchzehe"));
        firstIngredientList.add(new Ingredient(1, IngredientUnit.PACKUNG, "braune Linsen"));
        firstIngredientList.add(new Ingredient(0.5, IngredientUnit.KILOGRAMM, "Zitrone"));
        firstIngredientList.add(new Ingredient(8, IngredientUnit.LITER, "Honig"));
        firstIngredientList.add(new Ingredient(17, IngredientUnit.MILLILITER, "körniger Senf"));
        firstIngredientList.add(new Ingredient(75, IngredientUnit.ESSLÖFFEL, "Rucola"));
        firstIngredientList.add(new Ingredient(20, IngredientUnit.TEELÖFFEL, "getrocknete Cranberrys"));
        List<Instruction> firstInstructionList = new ArrayList<>();
        firstInstructionList.add(new Instruction("Heize den Backofen auf 220 °C Ober-/Unterhitze (200 °C Umluft) vor. Ungeschälte Süßkartoffeln in 1 cm breite Spalten schneiden und auf ein mit Backpapier belegtes Backblech geben, etwas Platz für die Tomaten lassen. Süßkartoffeln salzen*, pfeffern* und 25 Min. im Backofen backen, bis sie weich sind."));
        firstInstructionList.add(new Instruction("Knoblauch und Schalotte abziehen, Schalotte in feine Streifen schneiden. Braune Linsen in ein Sieb geben und mit kaltem Wasser spülen, bis dieses klar hindurchläuft. Zitrone in 6 Spalten schneiden. 2 Zitronenspalten in eine große Schüssel pressen und mit 2 EL Olivenöl*, Senf, Honig, Salz* und Pfeffer* zu einem Dressing glatt rühren."));

        List<Ingredient> secondIngredientList = new ArrayList<>();
        secondIngredientList.add(new Ingredient(300, IngredientUnit.GRAMM, "gemischtes Hackfleisch"));
        List<Instruction> secondInstrucionList = new ArrayList<>();
        secondInstrucionList.add(new Instruction("Kartoffeln schälen und vierteln. In einen großen Topf reichlich kaltes Wasser geben, salzen* und Kartoffelviertel zugeben. Wasser einmal aufkochen lassen und Kartoffelviertel für 8 – 10 Min. in leicht sprudelndem Wasser köcheln lassen. In der Zwischenzeit mit der Zubereitung fortfahren."));

        ArrayList<Recipe> expectedList = new ArrayList<>();
        expectedList.add(new Recipe("1", "Halloumi mit Linsengemüse", 2, "Das ist die Beschreibung des Rezepts", firstIngredientList, firstInstructionList, null));
        expectedList.add(new Recipe("2", "Köttbullar! Schwedische Hackbällchen", 2, "Wow, eine zweite Beschreibung!" , secondIngredientList, secondInstrucionList, null));
        Collections.sort(expectedList);

        Observer mockObserver = Mockito.mock(Observer.class);
        OnlineRepository.getRepository(con).getAllRecipes().observeForever(mockObserver);

        // get response for Listener
        ArgumentCaptor<List<Recipe>> actualList = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockObserver).onChanged(actualList.capture());

        //Sorting to compare Lists with default .equals Method.
        List<Recipe> others = actualList.getValue();
        Collections.sort(others);

        Assert.assertEquals(expectedList, others);
    }

    @Test
    public void correctApiCallForSingleRecipe() {
        testHelper.setReturnJson(new String[]{"{\"id\":\"abcdefg123456789\",\"title\":\"Halloumi mit Linsengemüse\",\"people\":2,\"description\":\"Das ist ein super tolles Rezept!\",\"ingredients\":[{\"amount\":150,\"unit\":\"g\",\"name\":\"Kirschtomaten\"},{\"amount\":1,\"unit\":\"stueck\",\"name\":\"Knoblauchzehe\"},{\"amount\":1,\"unit\":\"packung\",\"name\":\"braune Linsen\"},{\"amount\":0.5,\"unit\":\"kg\",\"name\":\"Zitrone\"},{\"amount\":8,\"unit\":\"l\",\"name\":\"Honig\"},{\"amount\":17,\"unit\":\"ml\",\"name\":\"körniger Senf\"},{\"amount\":75,\"unit\":\"el\",\"name\":\"Rucola\"},{\"amount\":20,\"unit\":\"tl\",\"name\":\"getrocknete Cranberrys\"}],\"instructions\":[{\"text\":\"Heize den Backofen auf 220 °C Ober-/Unterhitze (200 °C Umluft) vor. Ungeschälte Süßkartoffeln in 1 cm breite Spalten schneiden und auf ein mit Backpapier belegtes Backblech geben, etwas Platz für die Tomaten lassen. Süßkartoffeln salzen*, pfeffern* und 25 Min. im Backofen backen, bis sie weich sind.\"},{\"text\":\"Knoblauch und Schalotte abziehen, Schalotte in feine Streifen schneiden. Braune Linsen in ein Sieb geben und mit kaltem Wasser spülen, bis dieses klar hindurchläuft. Zitrone in 6 Spalten schneiden. 2 Zitronenspalten in eine große Schüssel pressen und mit 2 EL Olivenöl*, Senf, Honig, Salz* und Pfeffer* zu einem Dressing glatt rühren.\"}]}",
                "{\"id\":\"abcdefg123456789\",\"title\":\"abcd\"}"});
        OnlineRepository.getRepository(con).getRecipe("abcdefg123456789");

        // List of expected executed Requests
        ArrayList<OnlineRepositoryTestHelper.RequestData> executedRequests = new ArrayList<>();
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.GET, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/abcdefg123456789"));
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.GET, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/abcdefg123456789/image"));

        testHelper.assertIfExecutedRequestsAreEqual(executedRequests);
    }

    @Test
    public void correctSerializationForSingleRecipe() {
        // JSON returned, when Requests are executed
        testHelper.setReturnJson(new String[]{"{\"id\":\"abcdefg123456789\",\"title\":\"Halloumi mit Linsengemüse\",\"people\":2,\"description\":\"Das ist ein super tolles Rezept!\",\"ingredients\":[{\"amount\":150,\"unit\":\"g\",\"name\":\"Kirschtomaten\"},{\"amount\":1,\"unit\":\"stueck\",\"name\":\"Knoblauchzehe\"},{\"amount\":1,\"unit\":\"packung\",\"name\":\"braune Linsen\"},{\"amount\":0.5,\"unit\":\"kg\",\"name\":\"Zitrone\"},{\"amount\":8,\"unit\":\"l\",\"name\":\"Honig\"},{\"amount\":17,\"unit\":\"ml\",\"name\":\"körniger Senf\"},{\"amount\":75,\"unit\":\"el\",\"name\":\"Rucola\"},{\"amount\":20,\"unit\":\"tl\",\"name\":\"getrocknete Cranberrys\"}],\"instructions\":[{\"text\":\"Heize den Backofen auf 220 °C Ober-/Unterhitze (200 °C Umluft) vor. Ungeschälte Süßkartoffeln in 1 cm breite Spalten schneiden und auf ein mit Backpapier belegtes Backblech geben, etwas Platz für die Tomaten lassen. Süßkartoffeln salzen*, pfeffern* und 25 Min. im Backofen backen, bis sie weich sind.\"},{\"text\":\"Knoblauch und Schalotte abziehen, Schalotte in feine Streifen schneiden. Braune Linsen in ein Sieb geben und mit kaltem Wasser spülen, bis dieses klar hindurchläuft. Zitrone in 6 Spalten schneiden. 2 Zitronenspalten in eine große Schüssel pressen und mit 2 EL Olivenöl*, Senf, Honig, Salz* und Pfeffer* zu einem Dressing glatt rühren.\"}]}",
                "{\"id\":\"abcdefg123456789\",\"title\":\"abcd\"}"});

        // Expected data
        List<Ingredient> firstIngredientList = new ArrayList<>();
        firstIngredientList.add(new Ingredient(150, IngredientUnit.GRAMM, "Kirschtomaten"));
        firstIngredientList.add(new Ingredient(1, IngredientUnit.STÜCK, "Knoblauchzehe"));
        firstIngredientList.add(new Ingredient(1, IngredientUnit.PACKUNG, "braune Linsen"));
        firstIngredientList.add(new Ingredient(0.5, IngredientUnit.KILOGRAMM, "Zitrone"));
        firstIngredientList.add(new Ingredient(8, IngredientUnit.LITER, "Honig"));
        firstIngredientList.add(new Ingredient(17, IngredientUnit.MILLILITER, "körniger Senf"));
        firstIngredientList.add(new Ingredient(75, IngredientUnit.ESSLÖFFEL, "Rucola"));
        firstIngredientList.add(new Ingredient(20, IngredientUnit.TEELÖFFEL, "getrocknete Cranberrys"));
        List<Instruction> firstInstructionList = new ArrayList<>();
        firstInstructionList.add(new Instruction("Heize den Backofen auf 220 °C Ober-/Unterhitze (200 °C Umluft) vor. Ungeschälte Süßkartoffeln in 1 cm breite Spalten schneiden und auf ein mit Backpapier belegtes Backblech geben, etwas Platz für die Tomaten lassen. Süßkartoffeln salzen*, pfeffern* und 25 Min. im Backofen backen, bis sie weich sind."));
        firstInstructionList.add(new Instruction("Knoblauch und Schalotte abziehen, Schalotte in feine Streifen schneiden. Braune Linsen in ein Sieb geben und mit kaltem Wasser spülen, bis dieses klar hindurchläuft. Zitrone in 6 Spalten schneiden. 2 Zitronenspalten in eine große Schüssel pressen und mit 2 EL Olivenöl*, Senf, Honig, Salz* und Pfeffer* zu einem Dressing glatt rühren."));

        Recipe expectedRecipe = new Recipe("abcdefg123456789", "Halloumi mit Linsengemüse", 2, "Das ist ein super tolles Rezept!" , firstIngredientList, firstInstructionList, null);

        Observer mockObserver = Mockito.mock(Observer.class);
        OnlineRepository.getRepository(con).getRecipe("abcdefg123456789").observeForever(mockObserver);

        // get response for Listener
        ArgumentCaptor<Recipe> actualListCaptor = ArgumentCaptor.forClass(Recipe.class);
        Mockito.verify(mockObserver).onChanged(actualListCaptor.capture());

        Recipe actualRecipe = actualListCaptor.getValue();

        Assert.assertEquals(expectedRecipe, actualRecipe);
    }

    @Test
    public void correctApiCallForFetchSingleImage() {
        testHelper.setReturnJson(new String[]{"{\"id\":\"abcdefg123456789\",\"image\":\"ABCD12345678\"}"});

        OnlineRepository.getRepository(con).fetchSingleImage("abcdefg123456789");

        // List of expected executed Requests
        ArrayList<OnlineRepositoryTestHelper.RequestData> executedRequests = new ArrayList<>();
        executedRequests.add(new OnlineRepositoryTestHelper.RequestData(Request.Method.GET, "https://lets-cook-backend-zany-oryx-tj.cfapps.io/api/recipes/abcdefg123456789/image"));

        testHelper.assertIfExecutedRequestsAreEqual(executedRequests);
    }
}
