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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class AggiungiPrenotazioneFragment extends Fragment {

    private List<Prenotazione> prenotazioni;

    private List<String> logopedisti;

    private String selectedLogopedist="";
    private MainActivityGenitore mActivity;
    private String uidLog = "";
    boolean prenotazioneTrovataAsync;


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
        RadioGroup oraPrenotazione=view.findViewById(R.id.ora_inputPrenotazione);
        EditText notePrenotazione = view.findViewById(R.id.note_inputPrenotazione);
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
        logopedisti = new ArrayList<>();
        HashMap<String, String> logopedistiId = new HashMap<>();
        db.collection("logopedisti").get().addOnCompleteListener(querySnapshot -> {
            if(querySnapshot.isSuccessful())
            {
                for(QueryDocumentSnapshot document: querySnapshot.getResult()) {
                    if(document.getId() != null && document.get("Email")!= null)
                    {
                        logopedisti.add(document.get("Email").toString());
                        logopedistiId.put(document.get("Email").toString(), document.getId());
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
                String note = notePrenotazione.getText().toString();

                // Ottieni la data corrente
                Date dataCorrente = new Date();
                // Definisci il formato per la data inserita dall'utente
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    if(data.isEmpty())
                    {
                        Toasty.error(getContext(), "Inserisci la data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Date dataInserita = dateFormat.parse(dataPrenotazione.getText().toString());
                    if(dataInserita.compareTo(dataCorrente) < 0)
                    {
                        Toasty.error(getContext(), "La data inserita è precedente a quella odierna", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }


                String ora;
                if(oraPrenotazione.getCheckedRadioButtonId()!=-1) {
                    RadioButton selectedRadioButton = view.findViewById(oraPrenotazione.getCheckedRadioButtonId());
                    ora = selectedRadioButton.getText().toString();
                }
                else{
                    ora="";
                }



                // Ottieni il testo del RadioButton selezionato
                if (logopedista == null || ora.isEmpty() ) {
                    Toasty.error(getContext(), "Inserisci tutti i dati", Toast.LENGTH_SHORT).show();
                    return;
                }

                // controllo che la data inserita non sia empty
                // fare retrieve uid del logopedista data la mail
                db.collection("logopedisti")
                        .whereEqualTo("Email", selectedLogopedist)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful())
                            {
                                for(QueryDocumentSnapshot document: task.getResult()) {
                                    // stampa in log
                                    uidLog = document.getId();
                                    Log.d("AggiungiPrenotazioneFragment", "UID LOGOPEDISTA1: "+uidLog);
                                }

                                db.collection("prenotazioni")
                                        .whereEqualTo("logopedista", uidLog)
                                        .whereEqualTo("data", data.toString())
                                        .whereEqualTo("ora", ora.toString())
                                        .whereEqualTo("conferma", true)
                                        .get()
                                        .addOnCompleteListener(task2 -> {
                                            if(task2.isSuccessful()){
                                                prenotazioneTrovataAsync = !task2.getResult().isEmpty();
                                                if (prenotazioneTrovataAsync) {
                                                    Toasty.error(getContext(), "Prenotazione già esistente", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    // controllo che non sia stata inserita già una richiesta di prenotazione con quell'orario dallo stesso genitore
                                                    db.collection("prenotazioni")
                                                            .whereEqualTo("genitore", FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .whereEqualTo("logopedista", uidLog)
                                                            .whereEqualTo("data", data.toString())
                                                            .whereEqualTo("ora", ora.toString())
                                                            .whereEqualTo("conferma", false)
                                                            .get()
                                                            .addOnCompleteListener(task3 -> {
                                                                if(task3.isSuccessful()){
                                                                    prenotazioneTrovataAsync = !task3.getResult().isEmpty();
                                                                    if (prenotazioneTrovataAsync) {
                                                                        Toasty.warning(getContext(), "Richiesta di prenotazione già esistente, attendi che venga confermata", Toast.LENGTH_LONG).show();
                                                                    }
                                                                    else{
                                                                        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(getActivity())
                                                                                .setAnimation(R.raw.lottie_first_confirm)
                                                                                .setTitle("Sei sicuro di voler procedere?")
                                                                                .setMessage("Il logopedista dovrà CONFERMARE la prenotazionde dell'appuntamento.")
                                                                                .setCancelable(false)
                                                                                .setPositiveButton("Conferma", R.drawable.confirm_svgrepo_com, new BottomSheetMaterialDialog.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialogInterface, int which) {

                                                                                        // Salvataggio prenotazione nel database
                                                                                        String genitoreUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                                                        FirebaseFirestore db= FirebaseFirestore.getInstance();
                                                                                        Map<String, Object> prenotazione = new HashMap<>();
                                                                                        prenotazione.put("genitore", genitoreUid);
                                                                                        prenotazione.put("logopedista", logopedista);
                                                                                        prenotazione.put("data", data);
                                                                                        prenotazione.put("ora", ora);
                                                                                        prenotazione.put("note", note);
                                                                                        prenotazione.put("conferma", false);
                                                                                        db.collection("prenotazioni")
                                                                                                .add(prenotazione)
                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                                        if(task.isSuccessful())
                                                                                                        {
                                                                                                            prenotazioni.add(new Prenotazione(task.getResult().getId(), data, ora, logopedista, genitoreUid, note));
                                                                                                            Toasty.success(mActivity, "Prenotazione aggiunta con successo!", Toast.LENGTH_SHORT).show();
                                                                                                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                                                                                            navController.navigate(R.id.navigation_prenotazioni);
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                        dialogInterface.dismiss();
                                                                                    }
                                                                                })
                                                                                .setNegativeButton("Annulla operazione", R.drawable.delete_icon, new BottomSheetMaterialDialog.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialogInterface, int which) {
                                                                                        dialogInterface.dismiss();
                                                                                    }
                                                                                })
                                                                                .setAnimation("lottie_first_confirm.json")
                                                                                .build();

                                                                        // Show Dialog
                                                                        mBottomSheetDialog.show();
                                                                    }
                                                                }
                                                            });


                                                } // end if asincrono
                                            }
                                        });
                            } // end if
                        });



            } // end onClick() principale
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
