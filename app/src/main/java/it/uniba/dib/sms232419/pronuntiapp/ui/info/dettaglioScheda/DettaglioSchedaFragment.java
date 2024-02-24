package it.uniba.dib.sms232419.pronuntiapp.ui.info.dettaglioScheda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentDettaglioSchedaBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.HomeViewModel;

public class DettaglioSchedaFragment extends Fragment {

    private static final String TAG = "DettaglioSchedaFragment";
    private FragmentDettaglioSchedaBinding binding;
    private List<Esercizio> esercizi;
    private List<String> dateEsercizi;
    private List<String> completamentoEsercizi;
    private Scheda scheda;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private static final int FETCH_TERMINATO = 1;
    private static int NUMERO_ESERCIZI;
    private static int NUMERO_ESERCIZI_SCARICATI;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FETCH_TERMINATO:
                    mostraEsercizi();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            // recupero la scheda dal bundle passato al fragment
            scheda = getArguments().getParcelable("scheda");

            Log.d(TAG, "Scheda recuperato: "+ scheda.getNome());
        } else {
            Log.d(TAG, "Bundle nullo");
        }

        esercizi = new ArrayList<>();
        dateEsercizi = new ArrayList<>();
        completamentoEsercizi = new ArrayList<>();

        //recupera gli esercizi della scheda
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        NUMERO_ESERCIZI = scheda.getEsercizi().size();
        NUMERO_ESERCIZI_SCARICATI = 0;
         for(ArrayList<String> esercizioScheda : scheda.getEsercizi()){
             dateEsercizi.add(esercizioScheda.get(2));
             completamentoEsercizi.add(esercizioScheda.get(1));
             db.collection("esercizi")
                .document(esercizioScheda.get(0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Map<String, Object> esercizio = document.getData();
                                Esercizio es = new Esercizio(document.getId(),
                                        esercizio.get("nome").toString(),
                                        esercizio.get("logopedista").toString(),
                                        esercizio.get("tipologia").toString());
                                esercizi.add(es);
                                NUMERO_ESERCIZI_SCARICATI++;
                                if(NUMERO_ESERCIZI_SCARICATI == NUMERO_ESERCIZI){
                                    Message msg = mHandler.obtainMessage(FETCH_TERMINATO);
                                    mHandler.sendMessage(msg);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
        });
         }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentDettaglioSchedaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setto il titolo della scheda
        TextView nomeScheda = view.findViewById(R.id.nome_scheda_dettaglio);
        nomeScheda.setText(scheda.getNome());

        progressBar = view.findViewById(R.id.progressIndicator_dettaglio_scheda);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recycler_view_esercizi_scheda);
        recyclerView.setVisibility(View.GONE);

    }

    public void mostraEsercizi(){
        // Creo l'adapter per la recycler view
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(new EserciziDettaglioSchedaAdapter(getActivity().getApplicationContext(), esercizi, dateEsercizi, completamentoEsercizi));
    }
}
