package it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class SchedeBambinoAdapter extends RecyclerView.Adapter<SchedeBambinoHolderView>{
    Context context;

    List<Scheda> items;

    private ClickSchedeBambinoListener clickSchedeBambinoListener;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public SchedeBambinoAdapter(Context context, List<Scheda> items, ClickSchedeBambinoListener listener) {
        this.context = context;
        this.items = items;
        this.clickSchedeBambinoListener = listener;
    }

    @NonNull
    @Override
    public SchedeBambinoHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SchedeBambinoHolderView(LayoutInflater.from(context).inflate(R.layout.singola_scheda_cardview, parent, false), clickSchedeBambinoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SchedeBambinoHolderView holder, int position) {
        holder.textViewNomeScheda.setText(items.get(position).getNome());
        holder.textViewNumeroEsercizi.setText(context.getResources().getString(R.string.esercizi)+ String.valueOf(items.get(position).getNumeroEsercizi()));
        holder.textViewStatoScheda.setText(items.get(position).getStato());
        holder.textViewEserciziCompletati.setText(context.getResources().getString(R.string.esercizi_completati_scheda) + ": "+ String.valueOf(items.get(position).getEserciziCompletati()));


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Ottenere l'istanza di FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Ottieni l'utente attualmente autenticato
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Verifica se è un genitore
        db.collection("genitori").document(currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            holder.avviaGiocoButton.setVisibility(View.VISIBLE);
                            holder.eliminaButton.setVisibility(View.GONE);
                        }
                    }
                });

        // Verifica se è un logopedista
        db.collection("logopedisti").document(currentUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            holder.eliminaButton.setVisibility(View.VISIBLE);
                            holder.avviaGiocoButton.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
