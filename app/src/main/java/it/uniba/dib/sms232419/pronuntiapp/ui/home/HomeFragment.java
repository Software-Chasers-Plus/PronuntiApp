package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.MainActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.ui.aggiungiFiglio.aggiungiFiglioFragment;

public class HomeFragment extends Fragment implements ClickFigliListener{

    private MainActivity mainActivity;

    private FragmentHomeBinding binding;

    private Button buttonAggiungiFiglio;

    private Genitore genitore;

    private List<Figlio> figli = new ArrayList<>();


    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        db = FirebaseFirestore.getInstance();

        //creo un oggetto genitore con i dati dell'utente loggato
        db.collection("genitori")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("HomeFragment", "Genitore Trovato");
                                Map<String, Object> nuovoGenitore = document.getData();
                                genitore = new Genitore(nuovoGenitore.get("Nome").toString(), nuovoGenitore.get("Cognome").toString(),
                                        nuovoGenitore.get("CodiceFiscale").toString(), nuovoGenitore.get("Email").toString(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            } else {
                                //stampa nel log un messaggio di errore
                                Log.d("HomeFragment", "No genitore con id:"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                                startActivity(new Intent(mainActivity, AccessoActivity.class));
                                mainActivity.finish();
                            }
                        } else {
                            Log.d("HomeFragment", "Task fallito");
                            startActivity(new Intent(mainActivity, AccessoActivity.class));
                            mainActivity.finish();
                        }
                    }
                });

        //recupero i figli del genitore
        db.collection("figli")
                .whereEqualTo("genitore", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("HomeFragment", "Figli trovati");
                            figli.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> nuovoFiglio = document.getData();
                                figli.add(new Figlio(nuovoFiglio.get("nome").toString(), nuovoFiglio.get("cognome").toString(),
                                        nuovoFiglio.get("codiceFiscale").toString(), nuovoFiglio.get("logopedista").toString(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid(), nuovoFiglio.get("dataNascita").toString()));
                            }
                        }
                    }
                });


    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.figli_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity.getApplicationContext()));
        recyclerView.setAdapter(new figliAdapter(mainActivity.getApplicationContext(), figli, HomeFragment.this));

        buttonAggiungiFiglio = view.findViewById(R.id.aggiungi_figlio_button);
        buttonAggiungiFiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);
                Log.d("HomeFragment", "Figli passati: "+figli.size());
                navController.navigate(R.id.navigation_aggiungi_figlio, bundle);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onItemClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("figlio", figli.get(position));
        Log.d("HomeFragment", "Figlio passato: "+figli.get(position).getNome());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        navController.navigate(R.id.navigation_info_figlio, bundle);
    }
}
