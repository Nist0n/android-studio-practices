package ru.mirea.pavlovve.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mirea.pavlovve.mireaproject.network.Post;
import ru.mirea.pavlovve.mireaproject.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView resultText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressNetwork);
        resultText = view.findViewById(R.id.textNetworkResult);
        Button loadButton = view.findViewById(R.id.buttonLoadPost);

        loadButton.setOnClickListener(v -> loadPostFromNetwork());
    }

    private void loadPostFromNetwork() {
        progressBar.setVisibility(View.VISIBLE);
        resultText.setText("");

        RetrofitClient.getApi().getPost(1).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    resultText.setText(R.string.network_error);
                    return;
                }
                Post post = response.body();
                resultText.setText(getString(
                        R.string.network_post_fmt,
                        post.getId(),
                        post.getTitle(),
                        post.getBody()));
            }

            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                resultText.setText(getString(R.string.network_error_detail, t.getMessage()));
                Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
