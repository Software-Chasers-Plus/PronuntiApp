package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentPrenotazioniBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class PrenotazioniFragment extends Fragment implements ClickPrenotazioniListener {

    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private MainActivityGenitore mainActivityGenitore;

    private FragmentPrenotazioniBinding binding;

    private FloatingActionButton buttonAggiungiPrenotazione;

    private RecyclerView recyclerView;

    TextView textViewNoBookings,textViewBookings;

    ClickPrenotazioniListener clickPrenotazioniListener;

    private Genitore genitore;

    FirebaseFirestore db;

    // Array contenente gli ID delle risorse delle immagini

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityGenitore = (MainActivityGenitore) getActivity();
        db = FirebaseFirestore.getInstance();
        clickPrenotazioniListener=this;

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
                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
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
        textViewNoBookings = view.findViewById(R.id.no_prenotazioni);
        textViewBookings = view.findViewById(R.id.intestazione_prenotazioni);
        recyclerView = view.findViewById(R.id.prenotazioni_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityGenitore.getApplicationContext()));

       prenotazioni = new ArrayList<>();


        db = FirebaseFirestore.getInstance();
        db.collection("prenotazioni")
                .whereEqualTo("genitore", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> prenotazione = document.getData();


                                if (prenotazione.get("logopedista") != null) {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            prenotazione.get("logopedista").toString(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            prenotazione.get("note").toString()
                                    ));
                                } else {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            "",
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            prenotazione.get("note").toString()
                                    ));
                                }
                            }
                            if(prenotazioni.isEmpty())
                            {
                                textViewNoBookings.setVisibility(View.VISIBLE);
                                textViewBookings.setVisibility(View.GONE);
                            }
                            else
                            {
                                textViewNoBookings.setVisibility(View.GONE);
                                textViewBookings.setVisibility(View.VISIBLE);
                            }
                            Log.d("PrenotazioniFragment", "Prenotazioni:"+prenotazioni.size());
                            recyclerView.setAdapter(new PrenotazioniAdapter(mainActivityGenitore.getApplicationContext(), prenotazioni, db,clickPrenotazioniListener));
                        }
                    }
                });





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

        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                .setTitle("Eliminazione prenotazione")
                .setMessage("Sei sicuro di voler eliminare questa prenotazione?")
                .setCancelable(false)
                .setAnimation(R.raw.delete_anim)
                .setPositiveButton("Si", (dialogInterface, which) -> {
                    // Cancellazione prenotazione
                    db.collection("prenotazioni").document(prenotazioni.get(position).getPrenotazioneId()).delete();
                    prenotazioni.remove(prenotazioni.get(position));
                    if(prenotazioni.isEmpty())
                    {
                        textViewNoBookings.setVisibility(View.VISIBLE);
                        textViewBookings.setVisibility(View.GONE);
                    }
                    else
                    {
                        textViewNoBookings.setVisibility(View.GONE);
                        textViewBookings.setVisibility(View.VISIBLE);
                    }
                    recyclerView.getAdapter().notifyDataSetChanged();
                    dialogInterface.dismiss();
                })
                .setNegativeButton("No", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .setAnimation("delete_anim.json")
                .build();


        // Show Dialog
        mDialog.show();

    }

}
