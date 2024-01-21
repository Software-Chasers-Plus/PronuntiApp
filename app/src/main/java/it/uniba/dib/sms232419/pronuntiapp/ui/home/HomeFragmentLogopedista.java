package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityLogopedista;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeLogopedistaBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;


public class HomeFragmentLogopedista extends Fragment implements ClickFigliListener {

    private MainActivityLogopedista mainActivityLogopedista;

    private FragmentHomeLogopedistaBinding binding;

    private Button buttonAggiungiPaziente;

    private Logopedista logopedista;


    // in questo caso rappresentano i pazienti
    private List<Figlio> figli = new ArrayList<>() ;


    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityLogopedista = (MainActivityLogopedista) getActivity();
        db = FirebaseFirestore.getInstance();

        //creo un oggetto logopedista con i dati dell'utente loggato
        db.collection("logopedisti")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("HomeFragmentLogopedista", "Paziente Trovato");
                                Map<String, Object> nuovoLogopedista = document.getData();
                                logopedista = new Logopedista(
                                        nuovoLogopedista.get("Nome").toString(),
                                        nuovoLogopedista.get("Cognome").toString(),
                                        nuovoLogopedista.get("CodiceFiscale").toString(),
                                        nuovoLogopedista.get("Email").toString(),
                                        true,
                                        FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                            } else {
                                //stampa nel log un messaggio di errore
                                Log.d("HomeFragmentLogopedista", "No logopedista con id:"+FirebaseAuth.getInstance().getCurrentUser().getUid());
                                startActivity(new Intent(mainActivityLogopedista, AccessoActivity.class));
                                mainActivityLogopedista.finish();
                            }
                        } else {
                            Log.d("HomeFragmentLogopedista", "Task fallito");
                            startActivity(new Intent(mainActivityLogopedista, AccessoActivity.class));
                            mainActivityLogopedista.finish();
                        }
                    }
                });

        //recupero i figli dall'activity
        //recupero i figli dall'activity
        if(mainActivityLogopedista.figli != null){
            figli = mainActivityLogopedista.figli;
            Log.d("HomeFragmentLogopedista", "Figli recuperati: "+figli.size());
        }else{
            Log.d("HomeFragmentLogopedista", "Figli non recuperati");
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeLogopedistaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Log.d("HomeFragmentLogopedista", "Figli stampati: "+figli.size());
        RecyclerView recyclerView = view.findViewById(R.id.pazienti_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityLogopedista.getApplicationContext()));
        recyclerView.setAdapter(new FigliAdapter(mainActivityLogopedista.getApplicationContext(), figli, HomeFragmentLogopedista.this));


        buttonAggiungiPaziente = view.findViewById(R.id.aggiungi_paziente_button);

        buttonAggiungiPaziente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);
                navController.navigate(R.id.navigation_aggiungi_paziente, bundle);
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
        Log.d("HomeFragmentLogopedista", "Paziente passato: "+figli.get(position).getNome());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
        navController.navigate(R.id.navigation_info_paziente, bundle);
    }
}