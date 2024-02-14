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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;

public class HomeFragment extends Fragment implements ClickFigliListener{

    private MainActivityGenitore mainActivityGenitore;

    private FragmentHomeBinding binding;

    private FloatingActionButton buttonAggiungiFiglio;

    private Genitore genitore;

    private List<Figlio> figli = new ArrayList<>();

    private int[] avatarIds = {
            R.drawable.bambino_1,
            R.drawable.bambino_2,
            R.drawable.bambino_3,
            R.drawable.bambino_4,
            R.drawable.bambino_5,
            R.drawable.bambino_6
    };

    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityGenitore = (MainActivityGenitore) getActivity();
        db = FirebaseFirestore.getInstance();

        // Creiamo un oggetto genitore con i dati dell'utente loggato
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
                                genitore = new Genitore(nuovoGenitore.get("Nome").toString(),
                                        nuovoGenitore.get("Cognome").toString(),
                                        nuovoGenitore.get("Email").toString(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                                // Recupera l'UID del logopedista da ogni figlio e ottieni l'email corrispondente
                                for (Figlio figlio : figli) {
                                    String logopedistaUid = figlio.getLogopedista();
                                    if (logopedistaUid != null && !logopedistaUid.isEmpty()) {
                                        db.collection("logopedisti")
                                                .document(logopedistaUid)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot logopedistaDocument = task.getResult();
                                                            if (logopedistaDocument.exists()) {
                                                                String emailLogopedista = logopedistaDocument.getString("Email");
                                                                // Fai qualcosa con l'email del logopedista
                                                                Log.d("HomeFragment", "Email logopedista: " + emailLogopedista);
                                                                // Aggiorna l'interfaccia utente per mostrare l'email del logopedista
                                                                // Ad esempio, impostala direttamente in una TextView
                                                                String logopedistaEmail = "Email Logopedista: " + emailLogopedista;
                                                            } else {
                                                                Log.d("HomeFragment", "Il logopedista non esiste");
                                                            }
                                                        } else {
                                                            Log.d("HomeFragment", "Errore nel recuperare il logopedista", task.getException());
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Se il campo logopedista è vuoto, nascondi le view dell'email
                                        Log.d("HomeFragment", "Il campo logopedista è vuoto per il figlio: " + figlio.getNome());
                                    }
                                }
                            } else {
                                // Stampa nel log un messaggio di errore
                                Log.d("HomeFragment", "No genitore con id:" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                startActivity(new Intent(mainActivityGenitore, AccessoActivity.class));
                                mainActivityGenitore.finish();
                            }
                        } else {
                            Log.d("HomeFragment", "Task fallito");
                            startActivity(new Intent(mainActivityGenitore, AccessoActivity.class));
                            mainActivityGenitore.finish();
                        }
                    }
                });

        // Recuperiamo i figli dall'activity
        if (mainActivityGenitore.figli != null) {
            figli = mainActivityGenitore.figli;
            Log.d("HomeFragment", "Figli recuperati: " + figli.size());
        } else {
            Log.d("HomeFragment", "Figli non recuperati");
        }
    }



    @Override
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
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityGenitore.getApplicationContext()));

        // Ottieni gli ID degli avatar dai figli
        List<Integer> avatarIdsList = new ArrayList<>();
        for (Figlio figlio : figli) {
            if (figlio.getIdAvatar() >= 0) {
                Log.d("HomeFragment", "ID avatar valido per il figlio: " + figlio.getIdAvatar());
                avatarIdsList.add(figlio.getIdAvatar());
            } else {
                Log.e("HomeFragment", "ID avatar non valido per il figlio: " + figlio.getNome());
            }
        }

        recyclerView.setAdapter(new FigliAdapter(mainActivityGenitore.getApplicationContext(), figli, avatarIdsList, db,this));


        buttonAggiungiFiglio = view.findViewById(R.id.aggiungi_figlio_button);
        buttonAggiungiFiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("figli", (ArrayList<? extends Parcelable>) figli);
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
