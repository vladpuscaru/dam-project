package com.example.damproject.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.damproject.network.model.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class JsonParser {

    private class HttpGetImage {

    }


    public static HttpResponse parseJson(String json) {
        if (json == null) {
            return null;
        }

        List<Recipe> list = null;

        try {
            JSONObject object = new JSONObject(json);
            list = getFoodQueryListFromJson(object.getJSONArray("hits"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

//        for (Recipe r : list) {
//            Log.d("WTF", r.toString());
//        }

        return new HttpResponse(list);
    }

    private static List<Recipe> getFoodQueryListFromJson(JSONArray array) throws JSONException, IOException, ExecutionException, InterruptedException {
        if (array == null) {
            return null;
        }

        List<Recipe> list = new ArrayList<>();


        for (int i = 0; i < array.length(); i++) {
            Recipe recipe = getRecipeFromJson(array.getJSONObject(i).getJSONObject("recipe"));
            list.add(recipe);
        }

        return list;
    }

    private static Recipe getRecipeFromJson(JSONObject jsonObject) throws JSONException, IOException, ExecutionException, InterruptedException {
        if (jsonObject == null) {
            return null;
        }


        String name = jsonObject.getString("label");
        List<String> healthLabels = getHealthLabelsListFromJson(jsonObject.getJSONArray("healthLabels"));
        List<String> ingredients = getIngredientsListFromJson(jsonObject.getJSONArray("ingredientLines"));
        double calories = jsonObject.getDouble("calories");
        List<Double> nutrients = getNutrientsListFromJson(jsonObject.getJSONObject("totalNutrients"));
        double carbohydrates = nutrients.get(0);
        double fats = nutrients.get(1);
        double proteins = nutrients.get(2);

        Bitmap image = (new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(strings[0]).getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return bitmap;
            }
        }.execute(jsonObject.getString("image")).get());

        return new Recipe(name, calories, carbohydrates, fats, proteins, healthLabels, ingredients, image);
    }

    private static List<String> getIngredientsListFromJson(JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }


        List<String> ingredients = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            ingredients.add(array.getString(i));
        }

        return ingredients;
    }

    private static List<String> getHealthLabelsListFromJson(JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }


        List<String> healthLabels = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            healthLabels.add(array.getString(i));
        }

        return healthLabels;
    }

    private static List<Double> getNutrientsListFromJson(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null) {
            return null;
        }


        List<Double> nutrients = new ArrayList<>();

        nutrients.add(jsonObject.getJSONObject("CHOCDF").getDouble("quantity"));
        nutrients.add(jsonObject.getJSONObject("FAT").getDouble("quantity"));
        nutrients.add(jsonObject.getJSONObject("PROCNT").getDouble("quantity"));

        return nutrients;
    }
}
