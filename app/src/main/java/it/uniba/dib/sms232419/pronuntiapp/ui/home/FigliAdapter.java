package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class FigliAdapter extends RecyclerView.Adapter<FigliHolderView> {
    private final Context context;
    private final List<Figlio> items;
    private final List<Integer> avatarIds;
    private final ClickFigliListener clickFigliListener;
    private final FirebaseFirestore db;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();


    public FigliAdapter(Context context, List<Figlio> items, List<Integer> avatarIds, FirebaseFirestore db, ClickFigliListener listener) {
        this.context = context;
        this.items = items;
        this.avatarIds = avatarIds;
        this.db = db;
        this.clickFigliListener = listener;
    }

    @NonNull
    @Override
    public FigliHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FigliHolderView(LayoutInflater.from(context).inflate(R.layout.item_figli_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FigliHolderView holder, int position) {

        Figlio figlio = items.get(position);

        // Controllo se è un genitore o un logopedista
        if (user != null) {
            db.collection("genitori")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                holder.textViewNomeFiglio.setText(figlio.getNome());
                                holder.textViewEtaFiglio.setText(figlio.getDataNascita());
                                Log.d("FigliAdapter", "Data di nascita: " + figlio.getDataNascita());

                                // Verifica se il campo logopedista è vuoto
                                if (figlio.getLogopedista() != null && !figlio.getLogopedista().isEmpty()) {
                                    // Ottieni l'email del logopedista dal database Firebase
                                    db.collection("logopedisti")
                                            .document(figlio.getLogopedista())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {
                                                        String emailLogopedista = documentSnapshot.getString("Email");
                                                        if (emailLogopedista != null) {
                                                            holder.emailLogopedistaLabelTextView.setVisibility(View.VISIBLE);
                                                            holder.emailLogopedistaTextView.setText(emailLogopedista);
                                                            holder.emailLogopedistaTextView.setVisibility(View.VISIBLE);
                                                        } else {
                                                            holder.emailLogopedistaLabelTextView.setVisibility(View.GONE);
                                                            holder.emailLogopedistaTextView.setVisibility(View.GONE);
                                                        }
                                                    } else {
                                                        Log.d("FigliAdapter", "Il documento del logopedista non esiste");
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("FigliAdapter", "Errore nel recuperare l'email del logopedista");
                                                }
                                            });
                                } else {
                                    // Se il campo logopedista è vuoto, visualizza la scritta "Nessun logopedista"
                                    holder.emailLogopedistaLabelTextView.setVisibility(View.VISIBLE);
                                    holder.emailLogopedistaTextView.setText("Nessun logopedista");
                                    holder.emailLogopedistaTextView.setVisibility(View.VISIBLE);
                                }
                            }else{
                                // È un logopedista
                                holder.textViewNomeFiglio.setText(figlio.getNome());
                                holder.textViewEtaFiglio.setText(figlio.getDataNascita());

                                // Ottieni l'email del genitore dal database Firebase
                                db.collection("genitori")
                                        .document(figlio.getEmailGenitore())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    String emailGenitore = documentSnapshot.getString("Email");
                                                    Log.d("FigliAdapter", "Email genitore: " + emailGenitore);
                                                    if (emailGenitore != null) {
                                                        holder.emailLogopedistaTextView.setVisibility(View.VISIBLE);
                                                        TextView email_logopedista_label_figlio = holder.itemView.findViewById(R.id.email_logopedista_label_figlio);
                                                        email_logopedista_label_figlio.setText("Genitore");
                                                        holder.emailLogopedistaTextView.setText(emailGenitore);
                                                    } else {
                                                        holder.emailLogopedistaLabelTextView.setVisibility(View.GONE);
                                                        holder.emailLogopedistaTextView.setVisibility(View.GONE);
                                                    }
                                                } else {
                                                    Log.d("FigliAdapter", "Il documento del logopedista non esiste");
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("FigliAdapter", "Errore nel recuperare l'email del logopedista");
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("FigliAdapter", "Errore nel recuperare il ruolo dell'utente");
                        }
                    });
        }



        // Imposta l'avatar del figlio
        int avatarId = figlio.getIdAvatar();
        int avatarDrawableId;
        switch (avatarId) {
            case 0:
                avatarDrawableId = R.drawable.bambino_1;
                break;
            case 1:
                avatarDrawableId = R.drawable.bambino_2;
                break;
            case 2:
                avatarDrawableId = R.drawable.bambino_3;
                break;
            case 3:
                avatarDrawableId = R.drawable.bambino_4;
                break;
            case 4:
                avatarDrawableId = R.drawable.bambino_5;
                break;
            case 5:
                avatarDrawableId = R.drawable.bambino_6;
                break;
            default:
                avatarDrawableId = R.drawable.bambino;
                break;
        }
        holder.imageViewFiglio.setImageResource(avatarDrawableId);

        // Imposta il click listener sull'elemento della RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (clickFigliListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    // Passa l'indice dell'elemento cliccato al listener
                    clickFigliListener.onItemClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

