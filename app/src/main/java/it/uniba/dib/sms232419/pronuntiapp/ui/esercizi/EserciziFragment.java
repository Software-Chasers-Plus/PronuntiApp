package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.ConfirmationDialog;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityLogopedista;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentEserciziBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;


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
            Log.d("EserciziFragment", "ID dell'utente attualmente loggato: " + userId);
        } else {
            Log.d("EserciziFragment", "Nessun utente attualmente loggato");
        }

        //Recupero esercizi dal database
        db.collection("esercizi")
                .whereEqualTo("logopedista", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eserciziList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eserciziList.add(creazioneEsercizio(document));
                        }

                        Log.d("Esercizi Fragment", "Esercizi disponibili: " + eserciziList.size());

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
        Bundle bundle = new Bundle();
        bundle.putParcelable("esercizio", (Parcelable) eserciziList.get(position));
        Log.d("EserciziFragment", "Esercizio passato: " + eserciziList.get(position).getNome());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);

        switch (eserciziList.get(position).getTipologia().toString()){
            case "1":
                navController.navigate(R.id.navigation_dettaglio_esercizio1, bundle);
                break;
            case "2":
                navController.navigate(R.id.navigation_dettaglio_esercizio2, bundle);
                break;
            case "3":
                navController.navigate(R.id.navigation_dettaglio_esercizio3, bundle);
                break;
        }
    }

    @Override
    public void onDeleteClick(int position) {
        ConfirmationDialog.showConfirmationDialog(getContext(), "Sei sicuro di voler eliminare questo elemento?", new ConfirmationDialog.ConfirmationListener() {
            @Override
            public void onConfirm() {
                Esercizio esercizio = eserciziList.get(position);
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                //Eliminazione dei file multimediali
                esercizio.eliminaFileDaStorage();

                //Eliminazione dell'esercizio dal database
                db.collection("esercizi").document(esercizio.getEsercizioId()).delete();
                eserciziList.remove(position);
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
                // Azione di eliminazione annullata
            }
        });
    }

    public Esercizio creazioneEsercizio(QueryDocumentSnapshot document) {
        Map<String, Object> esercizioData = document.getData();

        switch (esercizioData.get("tipologia").toString()){
            case "1":
                return new EsercizioTipologia1(
                        document.getId().toString(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("immagine").toString(),
                        esercizioData.get("descrizioneImmagine").toString(),
                        esercizioData.get("audio1").toString(),
                        esercizioData.get("audio2").toString(),
                        esercizioData.get("audio3").toString());
            case "2":
                return new EsercizioTipologia2(
                        document.getId().toString(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("audio").toString(),
                        esercizioData.get("trascrizione_audio").toString());

            case "3":
                return new EsercizioTipologia3(
                        document.getId().toString(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("audio").toString(),
                        esercizioData.get("immagine1").toString(),
                        esercizioData.get("immagine2").toString(),
                        (long)esercizioData.get("immagine_corretta"));
        }

        return null;
    }
}