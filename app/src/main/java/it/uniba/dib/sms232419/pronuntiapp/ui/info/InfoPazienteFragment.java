package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.ConfirmationDialog;
import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;
import it.uniba.dib.sms232419.pronuntiapp.ui.esercizi.EserciziAdapter;
import it.uniba.dib.sms232419.pronuntiapp.ui.esercizi.EserciziFragment;
import it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino.ClickSchedeBambinoListener;
import it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino.SchedeBambinoAdapter;


public class InfoPazienteFragment extends Fragment implements ClickSchedeBambinoListener {

    //in questo caso rappresenta il paziente e non il figlio
    private Figlio paziente;

    private ArrayList<Scheda> schedeNonCompletateList, schedeCompletateList;

    private static final String TAG = "InfoPazienteFragment";

    String pazienteId;

    private RecyclerView recyclerViewSchedeNonCompletate, recyclerViewSchedeCompletate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il paziente dal bundle passato al fragment
            paziente = getArguments().getParcelable("figlio");

            Log.d("InfoPazienteFragment", "Paziente recuperato: "+paziente.getNome());
        }else{
            Log.d("InfoPazienteFragment", "Bundle non nullo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dettagli_paziente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inizializzazione recyclerView per le schede non completate
        recyclerViewSchedeNonCompletate = view.findViewById(R.id.schede_recycler_view_paziente);
        recyclerViewSchedeNonCompletate.setLayoutManager(new LinearLayoutManager(requireContext()));

        //Inizializzazione recyclerView per le schede completate
        recyclerViewSchedeCompletate = view.findViewById(R.id.schede_completate_recycler_view_paziente);
        recyclerViewSchedeCompletate.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView nomepaziente = view.findViewById(R.id.nome_paziente);
        nomepaziente.setText(paziente.getNome());

        TextView cognomepaziente = view.findViewById(R.id.cognome_paziente);
        cognomepaziente.setText(paziente.getCognome());

        TextView codiceFiscalepaziente = view.findViewById(R.id.codiceFiscale_paziente);
        codiceFiscalepaziente.setText(paziente.getCodiceFiscale());

        TextView dataNascitapaziente = view.findViewById(R.id.data_nascita_paziente);
        dataNascitapaziente.setText(paziente.getDataNascita());

        TextView schedeNonCreate = view.findViewById(R.id.text_dashboard);
        TextView schedeCompletateNonCreate = view.findViewById(R.id.text_completate_dashboard);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        Log.d(TAG, "Utente corrente: " + userId);

        TextView emailGenitorepaziente = view.findViewById(R.id.genitore);

        //Recupero email del genitore
        db.collection("genitori")
                .document(paziente.getEmailGenitore())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String emailLogopedista = documentSnapshot.getString("Email");
                            if (emailLogopedista != null && !emailLogopedista.isEmpty()) {
                                emailGenitorepaziente.setText(emailLogopedista);
                            }
                        } else {
                            Log.d(TAG, "Il documento del logopedista non esiste");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Errore nel recuperare l'email del logopedista: " + e.getMessage());
                        emailGenitorepaziente.setText("Errore nel recuperare l'email del logopedista");
                    }
                });
        //Recupero id del paziente
        db.collection("figli")
                .whereEqualTo("codiceFiscale", codiceFiscalepaziente.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            pazienteId = document.getId();
                            Log.d(TAG, "PazienteId: " + pazienteId);
                            //Recupero le schede del paziente
                            db.collection("schede")
                                    .whereEqualTo("logopedista", userId)
                                    .whereEqualTo("figlio", pazienteId)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            schedeNonCompletateList = new ArrayList<>();
                                            schedeCompletateList = new ArrayList<>();

                                            for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                Log.d(TAG, "Scheda prima della conversione: " + document2.getString("nome"));
                                                Scheda schedaHolder = FirebaseHelper.creazioneScheda(document2);
                                                if(schedaHolder.checkIsCompletata()){
                                                    schedeCompletateList.add(schedaHolder);
                                                } else {
                                                    schedeNonCompletateList.add(schedaHolder);
                                                }
                                            }

                                            Log.d(TAG, "Schede disponibili: " + schedeNonCompletateList.size());

                                            if (!schedeNonCompletateList.isEmpty()) {
                                                Log.d(TAG, "Scheda dopo la conversione: " + schedeNonCompletateList.get(0).getNome());
                                                recyclerViewSchedeNonCompletate.setAdapter(new SchedeBambinoAdapter(requireContext(), schedeNonCompletateList, InfoPazienteFragment.this));
                                                recyclerViewSchedeNonCompletate.getAdapter().notifyDataSetChanged();
                                            } else {
                                                schedeNonCreate.setVisibility(View.VISIBLE);
                                                Log.d(TAG, "Nessun scheda disponibile");
                                            }

                                            if (!schedeCompletateList.isEmpty()) {
                                                recyclerViewSchedeCompletate.setAdapter(new SchedeBambinoAdapter(requireContext(), schedeCompletateList, InfoPazienteFragment.this));
                                                recyclerViewSchedeCompletate.getAdapter().notifyDataSetChanged();
                                            }else {
                                                schedeCompletateNonCreate.setVisibility(View.VISIBLE);
                                                Log.d(TAG, "Nessuna scheda completata");
                                            }

                                        } else {
                                            Log.e(TAG, "Errore durante la query per le schede disponibili", task2.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "Errore durante la query per i figli", task.getException());
                    }
                });


        //Creazione di una scheda per il paziente
        ExtendedFloatingActionButton creaScheda = view.findViewById(R.id.extended_fab_crea_scheda);
        creaScheda.setOnClickListener(v -> {

            // salvo i pazienti ne bundle per passarli al fragment successivo
            Bundle bundle = new Bundle();
            bundle.putParcelable("paziente", paziente);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
            navController.navigate(R.id.navigation_creazione_scheda, bundle);
        });
    }

    @Override
    public void onItemClick(int position, boolean completata) {
        //TODO: implementarte il dettaglio della scheda
        if(completata){
            Bundle bundle = new Bundle();
            bundle.putParcelable("scheda", schedeCompletateList.get(position));
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
            navController.navigate(R.id.navigation_dettaglio_scheda_logopedista, bundle);
        }else {
            Bundle bundle = new Bundle();
            bundle.putParcelable("scheda", schedeNonCompletateList.get(position));
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
            navController.navigate(R.id.navigation_dettaglio_scheda_logopedista, bundle);
        }
    }

    @Override
    public void onEliminaClick(int position, boolean completata) {
        final Scheda[] scheda = {null};

        ConfirmationDialog.showConfirmationDialog(getContext(), "Sei sicuro di voler eliminare questo elemento?", new ConfirmationDialog.ConfirmationListener() {
            @Override
            public void onConfirm() {
                if (completata) {
                    scheda[0] = schedeCompletateList.get(position);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("schede")
                            .whereEqualTo("nomeScheda", scheda[0].getNome())
                            .whereEqualTo("logopedista", scheda[0].getLogopedista())
                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    db.collection("schede").document(document.getId()).delete();
                                }
                            });
                    schedeCompletateList.remove(position);
                    recyclerViewSchedeCompletate.getAdapter().notifyDataSetChanged();
                }
                else{
                    scheda[0] = schedeNonCompletateList.get(position);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("schede")
                            .whereEqualTo("nomeScheda", scheda[0].getNome())
                            .whereEqualTo("logopedista", scheda[0].getLogopedista())
                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    db.collection("schede").document(document.getId()).delete();
                                }
                            });
                    schedeNonCompletateList.remove(position);
                    recyclerViewSchedeNonCompletate.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onCancel() {
                // Azione di eliminazione annullata
            }
        });

    }

    @Override
    public void onAvviaGiocoClick(int position, boolean completata) {
        //Non è necessario implementaro poichè il logopedista non può avviare il gioco
    }
}