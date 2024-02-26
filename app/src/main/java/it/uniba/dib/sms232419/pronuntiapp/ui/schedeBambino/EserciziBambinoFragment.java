package it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityLogopedista;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia2;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia3;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;


public class EserciziBambinoFragment extends Fragment implements ClickEserciziBambinoListener{

    String TAG = "Esercizi Bambino Fragment";
    Map<String, Object> esercizi = null;

    List<DocumentSnapshot> result;
    Figlio paziente;
    String  pazienteId;
    ArrayList<Esercizio> eserciziList = new ArrayList<>();
    private RecyclerView recyclerView;
    MainActivityLogopedista mainActivityLogopedista;
    private static int NUMERO_ESERCIZI_SCHEDA = 0;
    private static int NUMERO_ESERCIZI_CARICATI = 0;
    private ArrayList<String> eserciziSelezionati = new ArrayList<>();
    // List to store the positions of checked CardViews
    private final List<Integer> checkedPositions = new ArrayList<>();
    private Map<Integer, String> esercizioDataSelezionata = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il paziente dal bundle passato al fragment
            paziente = getArguments().getParcelable("paziente");

            Log.d(TAG, "Paziente recuperato: " + paziente.getNome());
        }else{
            Log.d(TAG, "Bundle nullo");
        }

        mainActivityLogopedista = (MainActivityLogopedista) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.creazioneschedabambino, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_creazione_scheda);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MaterialButton conferma_scheda_bottone = view.findViewById(R.id.extended_fab_conferma_scheda);
        TextView textView = view.findViewById(R.id.text_dashboard);
        TextInputLayout textInputLayoutNomeScheda = view.findViewById(R.id.TextFieldNomeScheda);

        //Creazione riferimento al database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //Recupero id del logopedista
        String userId;
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "ID dell'utente attualmente loggato: " + userId);
        } else {
            userId = null;
            Log.d(TAG, "Nessun utente attualmente loggato");
        }

        //Recupero esercizi dal database
        db.collection("esercizi")
                .whereEqualTo("logopedista", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eserciziList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eserciziList.add(creazioneEsercizio(document));
                        }

                        Log.d(TAG, "Esercizi disponibili: " + eserciziList.size());

                        if (!eserciziList.isEmpty()) {
                            textView.setVisibility(View.GONE);
                            recyclerView.setAdapter(new EserciziBambinoAdapter(requireContext(), eserciziList, EserciziBambinoFragment.this));
                            recyclerView.getAdapter().notifyDataSetChanged();
                        } else {
                            textView.setVisibility(View.VISIBLE);
                            Log.d(TAG, "Nessun paziente disponibile senza logopedista");
                        }

                    } else {
                        Log.e(TAG, "Errore durante la query per gli esercizi disponibili", task.getException());
                    }
                });

        conferma_scheda_bottone.setOnClickListener(v -> {
            if (!checkedPositions.isEmpty()) {
                //Creazione di una raccolta su firebase con i dati dell'esercizio

                //Recupero il nome della scheda
                String nomeScheda = textInputLayoutNomeScheda.getEditText().getText().toString();
                if(nomeScheda.isEmpty()){
                    textInputLayoutNomeScheda.setError("Inserisci un nome per la scheda");
                    return;
                }

                db.collection("schede")
                        .whereEqualTo("nomeScheda", nomeScheda)
                        .get()
                        .addOnSuccessListener(task -> {
                            if (!task.getDocuments().isEmpty()) {
                                textInputLayoutNomeScheda.setError("Nome non disponibile");
                                Log.d(TAG, "Nome scheda già scelto");
                            } else {
                                //Definisco il CF
                                String codiceFiscale = paziente.getCodiceFiscale();

                                //Recupero l'id del paziente
                                db.collection("figli")
                                        .whereEqualTo("codiceFiscale", codiceFiscale)
                                        .get()
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                                    pazienteId = document.getId();
                                                    NUMERO_ESERCIZI_CARICATI = 0;
                                                    NUMERO_ESERCIZI_SCHEDA = checkedPositions.size();
                                                    addScheda(db, nomeScheda, userId);

                                                }
                                            } else {
                                                Log.e(TAG, "Errore durante la query per i figli", task2.getException());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Gestisci eventuali errori durante l'esecuzione della query
                            Log.e(TAG, "Errore durante il recupero dei documenti", e);
                        });

            } else {
                Snackbar.make(view, "Seleziona almeno un esercizio", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onItemClick(int position, MaterialCardView cardView) {
        //Seleziona la card view dopo una pressione prolungata
        cardView.setChecked(!cardView.isChecked());

        if (cardView.isChecked()) {
            checkedPositions.add(position);
            eserciziList.get(position).setChcked(true);
        } else {
            checkedPositions.remove(Integer.valueOf(position));
            eserciziList.get(position).setChcked(false);
        }
    }

    @Override
    public void onDettaglioClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("esercizio", eserciziList.get(position));
        Log.d(TAG, "Esercizio passato: " + eserciziList.get(position).getNome());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);

        switch (eserciziList.get(position).getTipologia()){
            case "1":
                navController.navigate(R.id.navigation_dettaglio_esercizio1, bundle);
                break;
            case "2":
                navController.navigate(R.id.navigation_dettaglio_esercizio2, bundle);
                break;
            case "3":
                navController.navigate(R.id.navigation_dettaglio_esercizio3, bundle);
                break;
        }
    }

    @Override
    public void onCalendarioClick(int position, EditText dataEsercizio) {
        // Get tomorrow's date
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1); // Add 1 day to get tomorrow's date

        CalendarConstraints.Builder calendarConstraints = new CalendarConstraints.Builder().setStart(tomorrow.getTimeInMillis());

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleziona una data")
                .setCalendarConstraints(calendarConstraints.build())
                .build();

        materialDatePicker.show(getChildFragmentManager(), "DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Converte la data selezionata in un oggetto Calendar
            Calendar calendarioSelezionato = Calendar.getInstance();
            calendarioSelezionato.setTimeInMillis(selection);

            int anno = calendarioSelezionato.get(Calendar.YEAR);
            int mese = calendarioSelezionato.get(Calendar.MONTH);
            int giorno = calendarioSelezionato.get(Calendar.DAY_OF_MONTH);

            // La data selezionata dall'utente
            String dataSelezionata = giorno + "/" + (mese + 1) + "/" + anno;

            // Ottieni la data corrente
            Date dataCorrente = new Date();
            // Definisci il formato per la data inserita dall'utente
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            Date dataInserita;
            try {
                dataInserita = dateFormat.parse(dataSelezionata);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if(dataInserita.compareTo(dataCorrente) < 0)
            {
                Toasty.error(getContext(), "La data inserita deve essere successiva a quella odierna", Toast.LENGTH_SHORT).show();
                return;
            }

            dataEsercizio.setText(dataSelezionata);
            Log.d(TAG, "Data esercizio: " + dataSelezionata);
            Log.d(TAG, "Posizione: " + position);
            esercizioDataSelezionata.put(new Integer(position), dataSelezionata);
        });

    }

    public Esercizio creazioneEsercizio(QueryDocumentSnapshot document) {
        Map<String, Object> esercizioData = document.getData();

        switch (esercizioData.get("tipologia").toString()){
            case "1":
                return new EsercizioTipologia1(
                        document.getId(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("immagine").toString(),
                        esercizioData.get("descrizioneImmagine").toString(),
                        esercizioData.get("audio1").toString(),
                        esercizioData.get("audio2").toString(),
                        esercizioData.get("audio3").toString());
            case "2":
                return new EsercizioTipologia2(
                        document.getId(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("audio").toString(),
                        esercizioData.get("trascrizione_audio").toString());

            case "3":
                return new EsercizioTipologia3(
                        document.getId(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("audio").toString(),
                        esercizioData.get("immagine1").toString(),
                        esercizioData.get("immagine2").toString(),
                        (long)esercizioData.get("immagine_corretta"));
        }

        return null;
    }

    private void caricaSchedaFirebase(String nomeScheda, String userId, String pazienteId, ArrayList<String> eserciziSelezionati, List<Integer> checkedPositions, FirebaseFirestore db) {
        LinearProgressIndicator progressIndicator = getView().findViewById(R.id.progressBarCreazioneScheda);
        progressIndicator.setVisibility(View.VISIBLE);

        //Selezione casuale dello sfondo
        Random rand = new Random();
        int randomNumber = rand.nextInt(5) + 1;

        // Crea un oggetto Map per contenere i dati da inserire nel documento
        Map<String, Object> data = new HashMap<>();
        data.put("nomeScheda", nomeScheda);
        data.put("logopedista", userId);
        data.put("figlio", pazienteId);
        data.put("sfondo", randomNumber);

        for (int i = 0; i < checkedPositions.size(); i++) {
            int checkedPosition = checkedPositions.get(i);
            Log.d(TAG, "Posizione selezionata: " + checkedPosition);
            if (checkedPosition != RecyclerView.NO_POSITION) {
                ArrayList<String> eserciziSchedaCompleti = new ArrayList<>();

                String dataEsercizio = esercizioDataSelezionata.get(checkedPosition);
                if (dataEsercizio == null || dataEsercizio.isEmpty()) {
                    Toasty.error(getContext(), "Inserisci una data per l'esercizio", Toast.LENGTH_SHORT).show();
                    return;
                }
                eserciziSchedaCompleti.add(eserciziSelezionati.get(i));
                eserciziSchedaCompleti.add("non completato");
                eserciziSchedaCompleti.add(dataEsercizio);
                Log.d(TAG, "Esercizi scheda completi: " + eserciziSchedaCompleti);
                data.put("esercizio" + i, eserciziSchedaCompleti);
            } else {
                // Handle invalid checked position
                Log.e(TAG, "Invalid checked position: " + checkedPosition);
            }
        }

        // Aggiungi i dati a una nuova raccolta con un ID generato automaticamente
        db.collection("schede")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    progressIndicator.setVisibility(View.GONE);
                    Toasty.success(getContext(), "Scheda creata con successo", Toast.LENGTH_SHORT, true).show();
                    Log.d(TAG, "DocumentSnapshot aggiunto con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setVisibility(View.GONE);
                    Toasty.error(getContext(), "Errore durante il caricamento della scheda", Toast.LENGTH_SHORT, true).show();
                    Log.w(TAG, "Errore durante l'aggiunta del documento", e);
                });
    }


    private void addScheda(FirebaseFirestore db, String nomeScheda, String userId) {
        db.collection("esercizi")
                .whereEqualTo("nome", eserciziList.get(checkedPositions.get(NUMERO_ESERCIZI_CARICATI)).getNome())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            eserciziSelezionati.add(document.getId());
                        }
                    } else {
                        Log.e(TAG, "Errore durante la query per gli esercizi", task.getException());
                    }

                    NUMERO_ESERCIZI_CARICATI++;

                    // Check if all queries have completed
                    if (NUMERO_ESERCIZI_CARICATI == NUMERO_ESERCIZI_SCHEDA) {
                        caricaSchedaFirebase(nomeScheda, userId, pazienteId, eserciziSelezionati, checkedPositions, db);
                    }else{
                        addScheda(db, nomeScheda, userId);
                    }
                });

        /*
        AtomicInteger completedQueries = new AtomicInteger(0); // Counter for completed queries

        for (int i = 0; i < checkedPositions.size(); i++) {
            final int finalI = i;
            db.collection("esercizi")
                    .whereEqualTo("nome", eserciziList.get(checkedPositions.get(finalI)).getNome())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                eserciziSelezionati.add(document.getId());
                            }
                        } else {
                            Log.e(TAG, "Errore durante la query per gli esercizi", task.getException());
                        }

                        // Increment the counter
                        int count = completedQueries.incrementAndGet();

                        // Check if all queries have completed
                        if (count == checkedPositions.size()) {
                            caricaSchedaFirebase(nomeScheda, userId, pazienteId, eserciziSelezionati, checkedPositions, db);
                        }
                    });
        }

         */
    }


}
