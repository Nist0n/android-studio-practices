package ru.mirea.pavlovve.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;

public final class ProfilePreferences {

    private static final String PREFS_NAME = "user_profile";

    static final String KEY_NAME = "name";
    static final String KEY_AGE = "age";
    static final String KEY_FOOD = "favorite_food";
    static final String KEY_MONEY = "money_rub";

    private final SharedPreferences prefs;

    public ProfilePreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void save(String name, int age, String food, long moneyRub) {
        prefs.edit()
                .putString(KEY_NAME, name)
                .putInt(KEY_AGE, age)
                .putString(KEY_FOOD, food)
                .putLong(KEY_MONEY, moneyRub)
                .apply();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public int getAge() {
        return prefs.getInt(KEY_AGE, 0);
    }

    public String getFavoriteFood() {
        return prefs.getString(KEY_FOOD, "");
    }

    public long getMoneyRub() {
        return prefs.getLong(KEY_MONEY, 0L);
    }

    public boolean hasProfile() {
        return !getName().isEmpty();
    }
}
