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
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class PrenotazioniLogopedistaAdapter extends RecyclerView.Adapter<PrenotazioniLogopedistaHolderView>{
    private final Context context;
    private final List<Prenotazione> items;
    ClickPrenotazioniLogopedistaListener clickPrenotazioniLogopedistaListener;

    private final FirebaseFirestore db;


    public PrenotazioniLogopedistaAdapter(Context context, List<Prenotazione> items, FirebaseFirestore db, ClickPrenotazioniLogopedistaListener listener) {
        this.context = context;
        this.items = items;
        this.clickPrenotazioniLogopedistaListener = listener;
        this.db = db;
    }

    @NonNull
    @Override
    public PrenotazioniLogopedistaHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PrenotazioniLogopedistaHolderView(LayoutInflater.from(context).inflate(R.layout.item_prenotazioni_logopedista_view, parent, false), clickPrenotazioniLogopedistaListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PrenotazioniLogopedistaHolderView holder, int position) {
        Prenotazione prenotazione = items.get(position);
        holder.textViewDataPrenotazione.setText(prenotazione.getData());
        holder.textViewOraPrenotazione.setText(prenotazione.getOra());
        holder.textViewNotePrenotazione.setText(prenotazione.getNote());

        db.collection("prenotazioni")
                .document(prenotazione.getPrenotazioneId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            boolean conferma = documentSnapshot.getBoolean("conferma");
                            if (conferma) {
                                holder.confermaButton.setText("Confermato");
                                holder.confermaButton.setEnabled(false);
                                holder.imageViewNonConfermato.setVisibility(View.GONE);
                                holder.imageViewConfermato.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("PrenotazioniAdapter", "Il documento del logopedista non esiste");
                            }
                        }
                    }
                });


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
                if (clickPrenotazioniLogopedistaListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    // Passa l'indice dell'elemento cliccato al listener
                    clickPrenotazioniLogopedistaListener.onItemClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
