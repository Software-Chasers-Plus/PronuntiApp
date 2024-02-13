package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232419.pronuntiapp.MainActivityLogopedista;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentEserciziBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi.MostraPazientiFragment;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.FigliAdapter;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.HomeFragment;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.HomeFragmentLogopedista;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EserciziFragment extends Fragment implements ClickEserciziListener{

    private FragmentEserciziBinding binding;
    Map<String, Object> esercizi = null;
    ArrayList<Esercizio> eserciziList = new ArrayList<>();

    private RecyclerView recyclerView;

    MainActivityLogopedista mainActivityLogopedista;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EserciziViewModel dashboardViewModel =
                new ViewModelProvider(this).get(EserciziViewModel.class);

        binding = FragmentEserciziBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainActivityLogopedista = (MainActivityLogopedista) getActivity();

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_esercizi);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MaterialButton aggiungi_esercizio_bottone = view.findViewById(R.id.raised_button);
        TextView textView = view.findViewById(R.id.text_dashboard);

        //Intenti per il passaggio tra le activity
        Intent intent = new Intent(getActivity(), CreazioneEsercizi.class);
        aggiungi_esercizio_bottone.setOnClickListener(v -> startActivity(intent));

        //Creazione riferimento al database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Recupero id del logopedista
        String userId = null;
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("EsercizioDenominazioneImmagine", "ID dell'utente attualmente loggato: " + userId);
        } else {
            Log.d("EsercizioDenominazioneImmagine", "Nessun utente attualmente loggato");
        }

        //Recupero dei dati degli esercizi da firebase
        db.collection("esercizi")
                .whereEqualTo("logopedista", userId) // Sostituisci "campo" con il nome del campo e "valore" con il valore da filtrare
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Itera sui risultati della query
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Ottenere i dati da ogni documento
                        esercizi = document.getData();
                        eserciziList.add(new Esercizio(esercizi.get("nome").toString(), esercizi.get("tipologia").toString(),esercizi.get("logopedista").toString()));
                        // Fai qualcosa con i dati
                        Log.d("EsercizioDenominazioneImmagine", document.getId() + " => " + esercizi);
                    }
                })
                .addOnFailureListener(e -> {
                    // Gestisci eventuali errori
                    Log.d("EsercizioDenominazioneImmagine", "Errore nel recuperare i documenti filtrati", e);
                });

        db.collection("esercizi")
                .whereEqualTo("logopedista", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eserciziList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> esercizioData = document.getData();

                            Esercizio nuovoEsercizio = new Esercizio(
                                    esercizioData.get("nome").toString(),
                                    esercizioData.get("logopedista").toString(),
                                    esercizioData.get("tipologia").toString());

                            eserciziList.add(nuovoEsercizio);

                            Log.d("Esercizi Fragment", document.getId() + " => " + nuovoEsercizio);
                        }

                        if (!eserciziList.isEmpty()) {
                            textView.setVisibility(View.GONE);
                            recyclerView.setAdapter(new EserciziAdapter(requireContext(), eserciziList, EserciziFragment.this));
                            recyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            textView.setVisibility(View.VISIBLE);
                            Log.d("EserciziFragment", "Nessun paziente disponibile senza logopedista");
                        }

                    } else {
                        Log.e("Errore query", "Errore durante la query per gli esercizi disponibili", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(int position) {

    }
}