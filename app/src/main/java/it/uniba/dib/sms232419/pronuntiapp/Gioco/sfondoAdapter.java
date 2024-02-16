package it.uniba.dib.sms232419.pronuntiapp.Gioco;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class sfondoAdapter extends RecyclerView.Adapter<sfondoAdapter.ViewHolder> {
    Context context;
    ArrayList<Integer> arrayList;
    OnItemClickListener onItemClickListener;
    View selectedItemView; // Variabile per tenere traccia dell'elemento selezionato

    public sfondoAdapter(Context context, ArrayList<Integer> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    // Imposta il contorno della prima immagine come selezionato di default
    public void setDefaultSelectedItemView(View view) {
        selectedItemView = view;
        updateImageViewBorder(view);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sfondo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(arrayList.get(position));
        holder.itemView.setOnClickListener(view -> {
            // Al click, aggiorna il contorno della CardView per indicare la selezione
            updateImageViewBorder(holder.itemView);
            onItemClickListener.onClick(holder.imageView, position);
        });
    }

    // Metodo per aggiornare il contorno della CardView quando un'immagine viene selezionata
    private void updateImageViewBorder(View itemView) {
        if (selectedItemView != null) {
            View prevBorderView = selectedItemView.findViewById(R.id.image_border);
            prevBorderView.setVisibility(View.GONE);
        }

        View currentBorderView = itemView.findViewById(R.id.image_border);
        currentBorderView.setVisibility(View.VISIBLE);

        selectedItemView = itemView;
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carousel_image_view);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ImageView imageView, Integer path);
    }
}
