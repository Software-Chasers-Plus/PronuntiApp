package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;

public class AggiungiPazienteFragment extends Fragment {
    private Button ricercaPaziente, aggiungiPaziente;
    private Logopedista logopedista;
    private TextInputEditText tokenPaziente;
    private LinearProgressIndicator progressBar;
    private ImageView avatarPaziente;
    private TextView nomePaziente, dataNascitaPaziente, codiceFiscalePaziente,emailGenitore;
    private String figlioUid;
    private MaterialCardView cardView;
    private FirebaseFirestore db;
    private static final int PAZIENTE_TROVATO = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAZIENTE_TROVATO:
                    progressBar.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupero i figli dal bundle passato al fragment
        if (getArguments() != null) {
            logopedista = getArguments().getParcelable("logopedista");
        }

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aggiungi_paziente, container, false);
        // Ottieni l'activity contenitore
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Ottieni l'istanza della BottomNavigationView dall'activity
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);

        // Nascondi la BottomNavigationView impostando la visibilità a GONE
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenPaziente = view.findViewById(R.id.token_paziente_aggiunta);

        ricercaPaziente = view.findViewById(R.id.bottone_ricerca_paziente_aggiunta);

        progressBar = view.findViewById(R.id.progressBar_aggiunta_paziente);

        cardView = view.findViewById(R.id.cardView_info_paziente_aggiunta);

        avatarPaziente = view.findViewById(R.id.avatar_paziente_aggiunta);

        nomePaziente = view.findViewById(R.id.nome_paziente_aggiunta);

        dataNascitaPaziente = view.findViewById(R.id.data_nascita_paziente_aggiunta);

        codiceFiscalePaziente = view.findViewById(R.id.codice_fiscale_paziente_aggiunta);

        emailGenitore = view.findViewById(R.id.email_genitore_paziente_aggiunta);
        ricercaPaziente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = tokenPaziente.getText().toString();
                if (token.isEmpty()) {
                    tokenPaziente.setError("Inserisci il token");
                    tokenPaziente.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    db.collection("figli").whereEqualTo("token", token).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("AggiungiPazienteFragment", "Figlio trovato: " + document.get("nome").toString());
                                if(document.get("logopedista").toString().isEmpty()){
                                    figlioUid = document.getId();
                                    avatarPaziente.setImageResource(Integer.valueOf(document.get("idAvatar").toString()));
                                    nomePaziente.setText(document.get("nome").toString() + " " + document.get("cognome").toString());
                                    dataNascitaPaziente.setText(document.get("dataNascita").toString());
                                    codiceFiscalePaziente.setText(document.get("codiceFiscale").toString());
                                    db.collection("genitori")
                                            .document(document.get("genitore").toString())
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    DocumentSnapshot document1 = task1.getResult();
                                                    if (document1.exists()) {
                                                        emailGenitore.setText(document1.get("Email").toString());
                                                        mHandler.sendEmptyMessage(PAZIENTE_TROVATO);
                                                    } else {
                                                        Log.e("AggiungiPazienteFragment", "Errore nella ricerca del genitore");
                                                    }
                                                }
                                            });
                                }else{
                                    Toast.makeText(getContext(), "Il paziente è già associato ad un logopedista", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            Log.e("AggiungiPazienteFragment", "Errore nella ricerca del figlio");
                        }
                    });
                }
            }
        });

        aggiungiPaziente = view.findViewById(R.id.bottone_conferma_aggiunti_paziente);
        aggiungiPaziente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (figlioUid != null) {
                    db.collection("figli")
                            .document(figlioUid)
                            .update("logopedista", logopedista.getUID());
                    Toast.makeText(getContext(), "Paziente aggiunto correttamente", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                    navController.navigate(R.id.navigation_home_logopedista);
                } else {
                    Log.e("AggiungiPazienteFragment", "Figlio nullo");
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Ottieni l'activity contenitore
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Ottieni l'istanza della BottomNavigationView dall'activity
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);

        // Riporta la BottomNavigationView visibile
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

}
