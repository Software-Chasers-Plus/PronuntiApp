package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class PrenotazioniAdapter extends RecyclerView.Adapter<PrenotazioniHolderView> {
    private Context context;
    private List<Prenotazione> items;
    ClickPrenotazioniListener clickPrenotazioniListener;

    private FirebaseFirestore db;


    public PrenotazioniAdapter(Context context, List<Prenotazione> items, FirebaseFirestore db, ClickPrenotazioniListener listener) {
        this.context = context;
        this.items = items;
        this.clickPrenotazioniListener = listener;
        this.db = db;
    }

    @NonNull
    @Override
    public PrenotazioniHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrenotazioniHolderView(LayoutInflater.from(context).inflate(R.layout.item_prenotazioni_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PrenotazioniHolderView holder, int position) {
        Prenotazione prenotazione = items.get(position);
        holder.textViewDataPrenotazione.setText(prenotazione.getData());
        holder.textViewOraPrenotazione.setText(prenotazione.getOra());

        Log.d("PrenotazioniAdapter","prenotazione:"+prenotazione.getLogopedista());

        db.collection("logopedisti")
                .document(prenotazione.getLogopedista())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String emailLogopedista = documentSnapshot.getString("Email");
                            if (emailLogopedista != null) {
                                holder.textViewLogopedistaFiglio.setText(emailLogopedista);
                            } else {
                                holder.textViewLogopedistaFiglio.setText("Email logopedista");

                            }
                        } else {
                            Log.d("PrenotazioniAdapter", "Il documento del logopedista non esiste");
                        }
                    }
                });

        // Imposta il click listener sull'elemento della RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (clickPrenotazioniListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    // Passa l'indice dell'elemento cliccato al listener
                    clickPrenotazioniListener.onItemClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

