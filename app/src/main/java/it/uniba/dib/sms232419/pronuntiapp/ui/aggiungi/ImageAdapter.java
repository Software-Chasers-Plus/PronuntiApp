package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Integer[] images = {R.drawable.bambino_1, R.drawable.bambino_2, R.drawable.bambino_3, R.drawable.bambino_4, R.drawable.bambino_5, R.drawable.bambino_6};
    private int selectedImageId = -1;
    private OnImageSelectedListener listener;

    public ImageAdapter() {
    }

    public interface OnImageSelectedListener {
        void onImageSelected(int imageId);
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Integer imageResId = images[position];
        holder.imageView.setImageResource(imageResId);

        if (imageResId == selectedImageId) {
            holder.imageView.setBackgroundResource(R.drawable.selected_border);
        } else {
            holder.imageView.setBackgroundResource(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageId = images[position];
                Log.d("ImageAdapter", "Selected image: " + selectedImageId);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onImageSelected(selectedImageId);
                    listener.onImageSelected(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public int getSelectedImageId() {
        return selectedImageId;
    }

    // ViewHolder per le immagini
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}