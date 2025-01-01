package com.android.groceryandmealplanner.Data;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Map;

public class Recipe implements Parcelable{

    private String url;
    private String name;
    private String timeToMake;
    private List<String> steps;
    private List<String> tips;
    private List<Map<String, String>> ingredients; // List of maps for ingredients

    // Default constructor (required for Firestore)
    public Recipe() {}

    public Recipe(String url, String name, String timeToMake, List<String> steps, List<String> tips, List<Map<String, String>> ingredients) {
        this.url = url;
        this.name = name;
        this.timeToMake = timeToMake;
        this.steps = steps;
        this.tips = tips;
        this.ingredients = ingredients;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTimeToMake() { return timeToMake; }
    public void setTimeToMake(String timeToMake) { this.timeToMake = timeToMake; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public List<String> getTips() { return tips; }
    public void setTips(List<String> tips) { this.tips = tips; }

    public List<Map<String, String>> getIngredients() { return ingredients; }
    public void setIngredients(List<Map<String, String>> ingredients) { this.ingredients = ingredients; }

    // Parcelable Implementation
    protected Recipe(Parcel in) {
        url = in.readString();
        name = in.readString();
        timeToMake = in.readString();
        steps = in.createStringArrayList();
        tips = in.createStringArrayList();
        ingredients = in.readArrayList(Map.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(name);
        dest.writeString(timeToMake);
        dest.writeStringList(steps);
        dest.writeStringList(tips);
        dest.writeList(ingredients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
