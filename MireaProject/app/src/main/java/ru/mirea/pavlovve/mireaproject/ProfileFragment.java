package ru.mirea.pavlovve.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private ProfilePreferences profilePreferences;
    private TextInputEditText nameInput;
    private TextInputEditText ageInput;
    private TextInputEditText foodInput;
    private TextInputEditText moneyInput;
    private TextView savedSummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePreferences = new ProfilePreferences(requireContext());

        nameInput = view.findViewById(R.id.inputName);
        ageInput = view.findViewById(R.id.inputAge);
        foodInput = view.findViewById(R.id.inputFood);
        moneyInput = view.findViewById(R.id.inputMoney);
        savedSummary = view.findViewById(R.id.textProfileSummary);
        Button saveButton = view.findViewById(R.id.buttonSaveProfile);

        saveButton.setOnClickListener(v -> saveProfile());
        loadProfile();
    }

    private void loadProfile() {
        if (!profilePreferences.hasProfile()) {
            savedSummary.setText(R.string.profile_empty);
            return;
        }
        nameInput.setText(profilePreferences.getName());
        ageInput.setText(String.valueOf(profilePreferences.getAge()));
        foodInput.setText(profilePreferences.getFavoriteFood());
        moneyInput.setText(String.valueOf(profilePreferences.getMoneyRub()));
        updateSummary();
    }

    private void saveProfile() {
        String name = textOf(nameInput);
        String ageText = textOf(ageInput);
        String food = textOf(foodInput);
        String moneyText = textOf(moneyInput);

        if (name.isEmpty() || ageText.isEmpty() || food.isEmpty() || moneyText.isEmpty()) {
            Toast.makeText(requireContext(), R.string.profile_fill_all, Toast.LENGTH_SHORT).show();
            return;
        }

        int age;
        long money;
        try {
            age = Integer.parseInt(ageText);
            money = Long.parseLong(moneyText);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), R.string.profile_invalid_numbers, Toast.LENGTH_SHORT).show();
            return;
        }

        profilePreferences.save(name, age, food, money);
        Toast.makeText(requireContext(), R.string.profile_saved, Toast.LENGTH_SHORT).show();
        updateSummary();
    }

    private void updateSummary() {
        savedSummary.setText(getString(
                R.string.profile_summary,
                profilePreferences.getName(),
                profilePreferences.getAge(),
                profilePreferences.getFavoriteFood(),
                profilePreferences.getMoneyRub()));
    }

    private static String textOf(TextInputEditText editText) {
        if (editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
