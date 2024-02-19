package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import es.dmoral.toasty.Toasty;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class AggiungiPrenotazioneFragment extends Fragment {

    private List<Prenotazione> prenotazioni;

    private List<String> logopedisti;

    private String selectedLogopedist="";
    private MainActivityGenitore mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivityGenitore) getActivity();

        // Recupero i figli dal bundle passato al fragment
        if (getArguments() != null) {
            prenotazioni = getArguments().getParcelableArrayList("prenotazioni");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.aggiungi_prenotazioni_fragment, container, false);

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
        super.onViewCreated(view,savedInstanceState);
        EditText logopedistaPrenotazione=view.findViewById(R.id.logopedista_inputPrenotazione);
        EditText dataPrenotazione=view.findViewById(R.id.data_inputPrenotazione);
        EditText oraPrenotazione=view.findViewById(R.id.ora_inputPrenotazione);
        ImageView iconaCalendario = view.findViewById(R.id.imageViewCalendarPrenotazione);

        iconaCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendarioCorrente = Calendar.getInstance();
                int anno = calendarioCorrente.get(Calendar.YEAR);
                int mese = calendarioCorrente.get(Calendar.MONTH);
                int giorno = calendarioCorrente.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                               // La data selezionata dall'utente
                                String dataSelezionata = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dataPrenotazione.setText(dataSelezionata);
                            }
                        }, anno, mese, giorno);
                datePickerDialog.show();
            }
        });

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        logopedisti=new ArrayList<>();
        HashMap<String,String> logopedistiId=new HashMap<>();
        db.collection("logopedisti").get().addOnCompleteListener(querySnapshot -> {
            if(querySnapshot.isSuccessful())
            {
                for(QueryDocumentSnapshot document: querySnapshot.getResult()) {
                    if(document.getId()!=null && document.get("Email")!= null)
                    {
                        logopedisti.add( document.get("Email").toString());
                        logopedistiId.put(document.get("Email").toString(),document.getId());
                    }

                }
            }
        });

        TextInputLayout textInputLayout = view.findViewById(R.id.menuLogopedisti);
        AutoCompleteTextView autoCompleteTextView = textInputLayout.findViewById(R.id.autoCompleteTextView);

        // Creare un ArrayAdapter per popolare l'AutoCompleteTextView con le email dei logopedisti
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, logopedisti);
        autoCompleteTextView.setAdapter(adapter);

        // Gestire la selezione dell'AutoCompleteTextView
        autoCompleteTextView.setOnItemClickListener((parent,view1, position, id) -> {
            // Ottenere l'elemento selezionato (email del logopedista)
            selectedLogopedist = (String) parent.getItemAtPosition(position);

            // Ora puoi utilizzare selectedLogopedist come desideri (ad esempio, salvarlo o mostrarlo in un Toast)
            // Esempio di utilizzo:
        });

        Button confermaAggiungiPrenotazione = view.findViewById(R.id.conferma_aggiungi_prenotazione_button);
        confermaAggiungiPrenotazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logopedista = logopedistiId.get(selectedLogopedist);
                String data = dataPrenotazione.getText().toString().trim();
                String ora = oraPrenotazione.getText().toString().trim();
                if (logopedista==null || data.isEmpty() || ora.isEmpty()) {
                    Toast.makeText(getContext(), "Inserisci tutti i dati!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Salvataggio prenotazione nel database
                String genitoreUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db= FirebaseFirestore.getInstance();
                Map<String,Object> prenotazione=new HashMap<>();
                prenotazione.put("genitore",genitoreUid);
                prenotazione.put("logopedista",logopedista);
                prenotazione.put("data",data);
                prenotazione.put("ora",ora);
                prenotazione.put("conferma",false);
                db.collection("prenotazioni")
                        .add(prenotazione)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful())
                                {
                                    prenotazioni.add(new Prenotazione(data,ora,logopedista,genitoreUid));
                                    Toasty.success(mActivity, "Prenotazione aggiunta con successo!", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                    navController.navigate(R.id.navigation_prenotazioni);

                                }
                            }
                        });
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

    // Metodo dell'interfaccia per la gestione della selezione dell'immagine


    // Genera un token univoco a partire dal codice fiscale per ogni figlio
    public static String generateTokenFromString(String codiceFiscale) {
        // Converti la stringa univoca in un array di byte
        byte[] bytes = codiceFiscale.getBytes();

        // Genera un UUID basato sulla stringa univoca
        UUID uuid = UUID.nameUUIDFromBytes(bytes);

        // Converte l'UUID in una stringa
        String token = uuid.toString();

        // Rimuovi eventuali trattini dalla stringa generata
        token = token.replaceAll("-", "");

        return token;
    }
}
