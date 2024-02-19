package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentPrenotazioniBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;
import it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi.ImageAdapter;
import it.uniba.dib.sms232419.pronuntiapp.ui.esercizi.EserciziViewModel;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.FigliAdapter;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.HomeViewModel;

public class PrenotazioniFragment extends Fragment implements ClickPrenotazioniListener {

    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private MainActivityGenitore mainActivityGenitore;

    private FragmentPrenotazioniBinding binding;

    private FloatingActionButton buttonAggiungiPrenotazione;

    private RecyclerView recyclerView;


    private Genitore genitore;

    FirebaseFirestore db;

    // Array contenente gli ID delle risorse delle immagini

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

        // Recuperiamo le prenotazioni dall'activity
        if (mainActivityGenitore.prenotazioni != null) {
            prenotazioni = mainActivityGenitore.prenotazioni;
            Log.d("HomeFragment", "Prenotazioni recuperate: " + prenotazioni.size());
        } else {
            Log.d("HomeFragment", "Prenotazioni non recuperate");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PrenotazioniViewModel prenotazioniViewModel =
                new ViewModelProvider(this).get(PrenotazioniViewModel.class);

        binding = FragmentPrenotazioniBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.prenotazioni_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityGenitore.getApplicationContext()));

        // Ottieni gli ID degli avatar dai figli
        recyclerView.setAdapter(new PrenotazioniAdapter(mainActivityGenitore.getApplicationContext(), prenotazioni, db,this));


        buttonAggiungiPrenotazione = view.findViewById(R.id.aggiungi_prenotazione_button);
        buttonAggiungiPrenotazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("prenotazioni", (ArrayList<? extends Parcelable>) prenotazioni);
                navController.navigate(R.id.navigation_aggiungi_prenotazione, bundle);
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

    @Override
    public void onEliminaClick(int position) {
        Log.d("PrenotazioniFragment","Cliccato bottone eliminazione");
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Eliminazione prenotazione\n");
        builder.setMessage("Sei sicuro di voler eliminare questa prenotazione?");
        builder.setCancelable(false); // L'utente non può chiudere il dialog cliccando fuori da esso

        // Personalizzazione dello stile dell'AlertDialog
        builder.setIcon(R.drawable.alert_svgrepo_com); // Icona critica

        // Aggiunta dei pulsanti
        builder.setPositiveButton("Si", (dialog, which) -> {
            // Cancellazione prenotazione

            db.collection("prenotazioni").document(prenotazioni.get(position).getPrenotazioneId()).delete();
            prenotazioni.remove(prenotazioni.get(position));
            recyclerView.getAdapter().notifyDataSetChanged();

        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });


        // Mostra il dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
