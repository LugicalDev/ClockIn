package classes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.clockitcurrent.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class Schedule {
    private String name;
    private Context context;
    private Activity viewToUse;
    public ArrayList<Plan> plans = new ArrayList<Plan>();
    private int date;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
   /* public static ArrayList<Button> buttonList;
    public static ArrayList<View> layoutList;
    public static ArrayList<TextView> textList;
    */
    
    public static final String SCHEDULE_FILLER = "$#@";
    public static final String PLAN_FILLER = "!!=_=!!";

    // Creating a new schedule based off template.
    public Schedule(String name, String template, int date, Context context, Activity view) {
        this.name = name;
        this.date = date;
        this.viewToUse = view;
        this.context = context;
        preferences = context.getSharedPreferences(template, Context.MODE_PRIVATE);
        editor = preferences.edit();
        // Verify it's an actual schedule template + deserialize for use
        if (preferences.getBoolean("Valid", false)) {
            String serialized = preferences.getString("Data", "");
            deserialize(serialized);
        }
    }

    // Default, new blank schedule w/no template
    public Schedule(String name, Context context, Activity view) {
        this.name = name;
        this.context = context;
        this.viewToUse = view;
        this.date = 0;
    }

    public void saveAsTemplate() {
        preferences = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putBoolean("Valid", true);
        editor.putString("Data", serialize());
    }

    public String serialize() {
        String serializedString = "";
        serializedString += ("Name:" + this.name);
        serializedString += (SCHEDULE_FILLER + "Date:" + this.date + SCHEDULE_FILLER);
        serializedString += ("Plans:");
        for (Plan plan: this.plans) {
            serializedString += (PLAN_FILLER + plan.serialize());
        }
        return serializedString;
    }

    public void deserialize(String serializedString) {

        String[] product = serializedString.split(SCHEDULE_FILLER);
        for (String string : product) {
            /* if (string.contains("Name:")) {
                String newString = string.replace("Name:", "");
                this.name = newString;
            } else */
            if (string.contains("Plans:")) {
                String newString = string.replace("Plans:", "");
                String[] planStrings = newString.split(PLAN_FILLER);
                for (String val : planStrings) {
                    Plan newPlan = new Plan(this);
                    newPlan.deserialize(val);
                    this.plans.add(newPlan);
                }
            } else if (string.contains("Date:")) {
                String newData = string.replace("Date:", "");
                this.date = Integer.parseInt(newData);
            }
        }
    }

    public void addPlan(Plan plan) {
        this.plans.add(plan);
    }
    public void updateFragment(Button[] buttonList, TextView[] textList, View[] layoutList) {

        for (View layout : layoutList) {
            //System.out.println(layout);
           layout.setVisibility(View.GONE);
        }

        for (int i = 0; i < this.plans.size(); i++) {
            Plan plan = this.plans.get(i);
            Button buttonToEdit = (Button) buttonList[i];
            TextView textToEdit = (TextView) textList[i];
            buttonToEdit.setText((CharSequence) plan.getName());
            textToEdit.setText(plan.convertToTimestamp());
            layoutList[i].setVisibility(View.VISIBLE);
        }
    }

    // Returns the earliest time slot available, and the longest duration it can be before the next event
    private double  starter = 0;
    private double ender = 1;
    public double[] findEarliestTimeSlot() {
        starter ++;
        ender ++;
        double[] result = {starter, ender};
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<double[]> findOpenSpots() {
        return new ArrayList<double[]>();
    }
}
