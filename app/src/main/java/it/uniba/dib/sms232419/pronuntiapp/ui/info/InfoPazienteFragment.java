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

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private ArrayList<Scheda> schedaList;

    private static final String TAG = "InfoPazienteFragment";

    String pazienteId;

    private RecyclerView recyclerView;

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

        recyclerView = view.findViewById(R.id.schede_recycler_view_paziente);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView nomepaziente = view.findViewById(R.id.nome_paziente);
        nomepaziente.setText(paziente.getNome());

        TextView cognomepaziente = view.findViewById(R.id.cognome_paziente);
        cognomepaziente.setText(paziente.getCognome());

        TextView codiceFiscalepaziente = view.findViewById(R.id.codiceFiscale_paziente);
        codiceFiscalepaziente.setText(paziente.getCodiceFiscale());

        TextView dataNascitapaziente = view.findViewById(R.id.data_nascita_paziente);
        dataNascitapaziente.setText(paziente.getDataNascita().toString());

        TextView emailGenitorepaziente = view.findViewById(R.id.genitore);
        emailGenitorepaziente.setText(paziente.getEmailGenitore());

        TextView schedeNonCreate = view.findViewById(R.id.text_dashboard);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String userId = currentUser.getUid();

        Log.d(TAG, "Utente corrente: " + userId);

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
                                            schedaList = new ArrayList<>();

                                            for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                Log.d(TAG, "Scheda prima della conversione: " + document2.getString("nome"));
                                                schedaList.add(FirebaseHelper.creazioneScheda(document2));
                                            }

                                            Log.d(TAG, "Schede disponibili: " + schedaList.size());

                                            if (!schedaList.isEmpty()) {
                                                Log.d(TAG, "Scheda dopo la conversione: " + schedaList.get(0).getNome());
                                                recyclerView.setAdapter(new SchedeBambinoAdapter(requireContext(), schedaList, InfoPazienteFragment.this));
                                                recyclerView.getAdapter().notifyDataSetChanged();
                                            } else {
                                                schedeNonCreate.setVisibility(View.VISIBLE);
                                                Log.d(TAG, "Nessun scheda disponibile");
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
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("scheda", (Parcelable) schedaList.get(position));
        Log.d(TAG, "Scheda passata: " + schedaList.get(position).getNome());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);

        //TODO: implementarte il dettaglio della scheda
    }

    @Override
    public void onEliminaClick(int position) {
        ConfirmationDialog.showConfirmationDialog(getContext(), "Sei sicuro di voler eliminare questo elemento?", new ConfirmationDialog.ConfirmationListener() {
            @Override
            public void onConfirm() {
                Scheda scheda = schedaList.get(position);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("schede")
                                .whereEqualTo("nomeScheda", scheda.getNome())
                                .whereEqualTo("logopedista", scheda.getLogopedista())
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("schede").document(document.getId()).delete();
                    }
                        });
                schedaList.remove(position);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
                // Azione di eliminazione annullata
            }
        });

    }

    @Override
    public void onAvviaGiocoClick(int position) {
        //Non è necessario implementaro poichè il logopedista non può avviare il gioco
    }
}