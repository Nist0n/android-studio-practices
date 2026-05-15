package ru.mirea.pavlovve.mireaproject.places;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.pavlovve.mireaproject.R;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.PlaceViewHolder> {

    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
    }

    private final List<Place> places = new ArrayList<>();
    private final OnPlaceClickListener listener;
    private String selectedId;

    public PlacesAdapter(OnPlaceClickListener listener) {
        this.listener = listener;
    }

    public void setPlaces(List<Place> items) {
        places.clear();
        places.addAll(items);
        notifyDataSetChanged();
    }

    public void setSelectedId(@Nullable String id) {
        selectedId = id;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = places.get(position);
        holder.bind(place, selectedId != null && selectedId.equals(place.getId()));
        holder.itemView.setOnClickListener(v -> listener.onPlaceClick(place));
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    static final class PlaceViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView address;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textPlaceTitle);
            address = itemView.findViewById(R.id.textPlaceAddress);
        }

        void bind(Place place, boolean selected) {
            title.setText(place.getTitle());
            address.setText(place.getAddress());
            title.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
            int bg = selected ? 0x33FF9800 : 0xFFFFFFFF;
            itemView.setBackgroundColor(bg);
        }
    }
}
