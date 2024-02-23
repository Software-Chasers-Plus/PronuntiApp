package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.ClickFigliListener;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.FigliAdapter;

public class MostraPazientiFragment extends Fragment implements ClickFigliListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private List<Figlio> pazientiDisponibili;
    private final List<Integer> idAvatarList = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mostra_paziente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.pazienti_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        db = FirebaseFirestore.getInstance();

        db.collection("figli")
                .whereEqualTo("logopedista", "")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            pazientiDisponibili = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> figlioData = document.getData();

                                Figlio nuovoPaziente = new Figlio(
                                        figlioData.get("nome").toString(),
                                        figlioData.get("cognome").toString(),
                                        figlioData.get("codiceFiscale").toString(),
                                        "",
                                        figlioData.get("genitore").toString(),
                                        figlioData.get("dataNascita").toString(),
                                        Integer.parseInt(figlioData.get("idAvatar").toString()),
                                        figlioData.get("token").toString(),
                                        (long)figlioData.get("punteggioGioco"),
                                        Integer.parseInt(figlioData.get("sfondoSelezionato").toString()),
                                        Integer.parseInt(figlioData.get("personaggioSelezionato").toString())
                                );

                                pazientiDisponibili.add(nuovoPaziente);
                                idAvatarList.add(Integer.parseInt(figlioData.get("idAvatar").toString())); // Aggiungi l'idAvatar alla lista
                            }

                            if (!pazientiDisponibili.isEmpty()) {
                                recyclerView.setAdapter(new FigliAdapter(requireContext(), pazientiDisponibili, idAvatarList, db, MostraPazientiFragment.this));
                                recyclerView.getAdapter().notifyDataSetChanged();
                            } else {
                                Log.d("AggiungiPazienteFragment", "Nessun paziente disponibile senza logopedista");
                            }

                        } else {
                            Log.e("Errore query", "Errore durante la query per i pazienti disponibili", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        if (position >= 0 && position < pazientiDisponibili.size()) {
            Figlio figlioCliccato = pazientiDisponibili.get(position);

            // salvo i pazienti ne bundle per passarli al fragment successivo
            Bundle bundle = new Bundle();
            bundle.putParcelable("figlio", figlioCliccato);

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
            navController.navigate(R.id.navigation_aggiungi_paziente, bundle);
        } else {
            Log.e("AggiungiPazienteFragment", "Invalid ArrayList or Index: " + position);
        }
    }
}
