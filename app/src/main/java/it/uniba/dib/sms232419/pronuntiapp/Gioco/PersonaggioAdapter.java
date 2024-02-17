package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class PersonaggioAdapter extends RecyclerView.Adapter<PersonaggioAdapter.ViewHolder> {
    Context context;
    ArrayList<Integer> arrayList;
    PersonaggioAdapter.OnItemClickListener onItemClickListener;
    View selectedItemView; // Variabile per tenere traccia dell'elemento selezionato

    public PersonaggioAdapter(Context context, ArrayList<Integer> arrayList) {
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
    public PersonaggioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_personaggio_impostazioni, parent, false);
        return new PersonaggioAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonaggioAdapter.ViewHolder holder, int position) {
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
            View prevBorderView = selectedItemView.findViewById(R.id.image_border_personaggio);
            prevBorderView.setVisibility(View.GONE);
        }

        View currentBorderView = itemView.findViewById(R.id.image_border_personaggio);
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
            imageView = itemView.findViewById(R.id.carousel_image_view_personaggio);
        }
    }

    public void setOnItemClickListener(PersonaggioAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ImageView imageView, Integer path);
    }
}
