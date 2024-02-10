package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class AggiungiPazienteFragment extends Fragment {
    private Figlio figlio;
    private Button aggiungiPaziente;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            figlio = bundle.getParcelable("figlio");

            if (figlio != null) {
                Log.d("AggiungiPazienteFragment", "Figlio recuperato: " + figlio.getNome());
            } else {
                Log.e("AggiungiPazienteFragment", "Il figlio nel bundle è null");
            }
        } else {
            Log.e("AggiungiPazienteFragment", "Bundle nullo");
        }

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_aggiungi_paziente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aggiungiPaziente = view.findViewById(R.id.aggiungi_paziente_button);
        aggiungiPaziente.setOnClickListener(v -> aggiungiPazienteClick());
    }

    private void aggiungiPazienteClick() {
        if (figlio != null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                aggiungiLogopedistaAiFigli(user);
            } else {
                Log.e("AggiungiPazienteFragment", "Utente corrente non trovato");
                Toast.makeText(getContext(), "Utente non trovato. Effettua il login e riprova.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("AggiungiPazienteFragment", "Figlio non valido");
            Toast.makeText(getContext(), "Figlio non valido", Toast.LENGTH_SHORT).show();
        }
    }

    private void aggiungiLogopedistaAiFigli(FirebaseUser currentUser) {

        db.collection("figli")
                .whereEqualTo("codiceFiscale", figlio.getCodiceFiscale())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Ottieni l'ID del documento
                        String docId = document.getId();

                        // Esegui l'aggiornamento del campo "logopedista" su questo documento
                        db.collection("figli")
                                .document(docId)
                                .update("logopedista", currentUser.getUid())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("AggiungiPazienteFragment", "Campo logopedista aggiornato con successo per il figlio: ");
                                    Toast.makeText(getContext(), "Campo logopedista aggiornato con successo", Toast.LENGTH_SHORT).show();
                                    // Puoi navigare ad un'altra schermata qui se necessario
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("AggiungiPazienteFragment", "Errore durante l'aggiornamento del campo logopedista per il figlio: " );
                                    Toast.makeText(getContext(), "Errore durante l'aggiornamento del campo logopedista", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Gestisci eventuali errori nella query
                    Log.d("AggiungiPazienteFragment", "Errore durante il recupero dei figli: " + e.getMessage());
                    Toast.makeText(getContext(), "Errore durante il recupero dei figli", Toast.LENGTH_SHORT).show();
                });


    }

}
