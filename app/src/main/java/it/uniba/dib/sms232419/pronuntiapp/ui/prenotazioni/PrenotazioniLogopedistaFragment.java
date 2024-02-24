package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import it.uniba.dib.sms232419.pronuntiapp.MainActivityLogopedista;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentPrenotazioniLogopedistaBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class PrenotazioniLogopedistaFragment extends Fragment implements ClickPrenotazioniLogopedistaListener {

    private List<Prenotazione> prenotazioni = new ArrayList<>();
    private MainActivityLogopedista mainActivityLogopedista;

    private FragmentPrenotazioniLogopedistaBinding binding;

    private RecyclerView recyclerView;

    private Button confermaButton;

    TextView textViewNoBookings,textViewBookings;

    private ClickPrenotazioniLogopedistaListener clickPrenotazioniLogopedistaListener;
    private Logopedista logopedista;

    FirebaseFirestore db;

    // Array contenente gli ID delle risorse delle immagini

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityLogopedista = (MainActivityLogopedista) getActivity();
        db = FirebaseFirestore.getInstance();
        clickPrenotazioniLogopedistaListener=this;


                // Creiamo un oggetto genitore con i dati dell'utente loggato
        db.collection("logopedisti")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("HomeFragment", "Logopedista Trovato");
                                Map<String, Object> nuovoLogopedista = document.getData();
                                logopedista = new Logopedista(nuovoLogopedista.get("Nome").toString(),
                                        nuovoLogopedista.get("Cognome").toString(),
                                        nuovoLogopedista.get("Email").toString(),
                                        nuovoLogopedista.get("Matricola").toString(),
                                        (boolean) nuovoLogopedista.get("Abilitazione"),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                            } else {
                                // Stampa nel log un messaggio di errore
                                Log.d("HomeFragment", "No genitore con id:" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                                startActivity(new Intent(mainActivityLogopedista, AccessoActivity.class));
                                mainActivityLogopedista.finish();
                            }
                        } else {
                            Log.d("HomeFragment", "Task fallito");
                            startActivity(new Intent(mainActivityLogopedista, AccessoActivity.class));
                            mainActivityLogopedista.finish();
                        }
                    }
                });


        if (mainActivityLogopedista.prenotazioni != null) {
            prenotazioni = mainActivityLogopedista.prenotazioni;
            Log.d("HomeFragment", "Prenotazioni recuperate: " + prenotazioni.size());
        } else {
            Log.d("HomeFragment", "Prenotazioni non recuperate");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PrenotazioniLogopedistaViewModel prenotazioniLogopedistaViewModel =
                new ViewModelProvider(this).get(PrenotazioniLogopedistaViewModel.class);

        binding = FragmentPrenotazioniLogopedistaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewNoBookings = view.findViewById(R.id.no_prenotazioni_logopedista);
        textViewBookings = view.findViewById(R.id.intestazione_prenotazioni_logopedista);
        confermaButton=view.findViewById(R.id.conferma_prenotazione_button);
        recyclerView = view.findViewById(R.id.prenotazioni_logopedista_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivityLogopedista.getApplicationContext()));
        prenotazioni = new ArrayList<>();


        db = FirebaseFirestore.getInstance();
        db.collection("prenotazioni")
                .whereEqualTo("logopedista", FirebaseAuth.getInstance().getCurrentUser().getUid())
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
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            prenotazione.get("genitore").toString(),
                                            prenotazione.get("note").toString()
                                    ));
                                } else {
                                    prenotazioni.add(new Prenotazione(
                                            document.getId(),
                                            prenotazione.get("data").toString(),
                                            prenotazione.get("ora").toString(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                            "",
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
                            Log.d("PrenotazioniLogopedistaFragment", "Prenotazioni:"+prenotazioni.size());
                            recyclerView.setAdapter(new PrenotazioniLogopedistaAdapter(mainActivityLogopedista.getApplicationContext(), prenotazioni, db,clickPrenotazioniLogopedistaListener));
                        }
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
    public void onConfermaClick(int position) {
        Log.d("PrenotazioniLogopedistaFragment","Cliccato bottone conferma prenotazione");

        BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                .setTitle("Conferma prenotazione")
                .setMessage("Sei sicuro di voler confermare questa prenotazione?")
                .setCancelable(false)
                .setAnimation(R.raw.confirm_prenotation_logoped)
                .setPositiveButton("Si", (dialogInterface, which) -> {
                    // Conferma prenotazione
                    db.collection("prenotazioni").document(prenotazioni.get(position).getPrenotazioneId()).update("conferma",true);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    dialogInterface.dismiss();
                })
                .setNegativeButton("No", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .setAnimation("confirm_prenotation_logoped.json")
                .build();

        mDialog.show();
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
                    recyclerView.getAdapter().notifyDataSetChanged();
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


