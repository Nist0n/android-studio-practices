package ru.mirea.pavlovve.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FilesFragment extends Fragment {

    private File notesDir;
    private ListView filesList;
    private TextView filePreview;
    private TextView fileHint;
    private ArrayAdapter<String> adapter;
    private final List<String> fileNames = new ArrayList<>();
    private String selectedFileName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notesDir = new File(requireContext().getFilesDir(), "notes");
        if (!notesDir.exists()) {
            notesDir.mkdirs();
        }

        filesList = view.findViewById(R.id.listFiles);
        filePreview = view.findViewById(R.id.textFilePreview);
        fileHint = view.findViewById(R.id.textFilesHint);
        FloatingActionButton fab = view.findViewById(R.id.fabAddNote);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fileNames);
        filesList.setAdapter(adapter);
        filesList.setOnItemClickListener(this::onFileSelected);

        view.findViewById(R.id.buttonUppercase).setOnClickListener(v -> convertSelectedToUppercase());

        fab.setOnClickListener(v -> showCreateNoteDialog());
        refreshFileList();
    }

    private void showCreateNoteDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_note, null, false);
        TextInputEditText titleInput = dialogView.findViewById(R.id.inputNoteTitle);
        TextInputEditText bodyInput = dialogView.findViewById(R.id.inputNoteBody);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.files_dialog_title)
                .setView(dialogView)
                .setPositiveButton(R.string.files_dialog_save, (dialog, which) -> {
                    String title = titleInput.getText() != null
                            ? titleInput.getText().toString().trim() : "";
                    String body = bodyInput.getText() != null
                            ? bodyInput.getText().toString().trim() : "";
                    createNoteFile(title, body);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void createNoteFile(String title, String body) {
        if (title.isEmpty() || body.isEmpty()) {
            Toast.makeText(requireContext(), R.string.files_fill_all, Toast.LENGTH_SHORT).show();
            return;
        }

        String safeName = title.replaceAll("[^a-zA-Zа-яА-Я0-9_\\-]", "_");
        if (safeName.isEmpty()) {
            safeName = "note";
        }
        File file = new File(notesDir, safeName + ".txt");

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(body.getBytes(StandardCharsets.UTF_8));
            Toast.makeText(requireContext(), R.string.files_created, Toast.LENGTH_SHORT).show();
            refreshFileList();
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.files_write_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void onFileSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || position >= fileNames.size()) {
            return;
        }
        selectedFileName = fileNames.get(position);
        try {
            filePreview.setText(readFile(new File(notesDir, selectedFileName)));
            fileHint.setText(getString(R.string.files_selected, selectedFileName));
        } catch (IOException e) {
            filePreview.setText("");
            Toast.makeText(requireContext(), R.string.files_read_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void convertSelectedToUppercase() {
        if (selectedFileName == null) {
            Toast.makeText(requireContext(), R.string.files_select_first, Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(notesDir, selectedFileName);
        try {
            String content = readFile(file);
            String converted = content.toUpperCase(Locale.getDefault());
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(converted.getBytes(StandardCharsets.UTF_8));
            }
            filePreview.setText(converted);
            Toast.makeText(requireContext(), R.string.files_converted, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), R.string.files_write_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshFileList() {
        fileNames.clear();
        File[] files = notesDir.listFiles();
        if (files != null && files.length > 0) {
            Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (fileNames.isEmpty()) {
            fileHint.setText(R.string.files_empty);
            filePreview.setText("");
        }
    }

    private static String readFile(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append('\n');
                }
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
