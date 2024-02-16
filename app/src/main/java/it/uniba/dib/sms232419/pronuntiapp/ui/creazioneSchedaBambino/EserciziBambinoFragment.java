package it.uniba.dib.sms232419.pronuntiapp.ui.creazioneSchedaBambino;

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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // List to store the positions of checked CardViews
    private List<Integer> checkedPositions = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il paziente dal bundle passato al fragment
            paziente = getArguments().getParcelable("paziente");

            Log.d(TAG, "Paziente recuperato: "+paziente.getNome());
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
        } else {
            checkedPositions.remove(Integer.valueOf(position));
        }
    }

    @Override
    public void onDettaglioClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("esercizio", (Parcelable) eserciziList.get(position));
        Log.d(TAG, "Esercizio passato: " + eserciziList.get(position).getNome());
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);

        switch (eserciziList.get(position).getTipologia().toString()){
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

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleziona una data");
        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Converte la data selezionata in un oggetto Calendar
            Calendar calendarioSelezionato = Calendar.getInstance();
            calendarioSelezionato.setTimeInMillis(selection);

            int anno = calendarioSelezionato.get(Calendar.YEAR);
            int mese = calendarioSelezionato.get(Calendar.MONTH);
            int giorno = calendarioSelezionato.get(Calendar.DAY_OF_MONTH);

            // La data selezionata dall'utente
            String dataSelezionata = giorno + "/" + (mese + 1) + "/" + anno;
            dataEsercizio.setText(dataSelezionata);
        });
        materialDatePicker.show(requireFragmentManager(), "DATE_PICKER");
    }

    public Esercizio creazioneEsercizio(QueryDocumentSnapshot document) {
        Map<String, Object> esercizioData = document.getData();

        switch (esercizioData.get("tipologia").toString()){
            case "1":
                return new EsercizioTipologia1(
                        document.getId().toString(),
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
                        document.getId().toString(),
                        esercizioData.get("nome").toString(),
                        esercizioData.get("logopedista").toString(),
                        esercizioData.get("tipologia").toString(),
                        esercizioData.get("audio").toString(),
                        esercizioData.get("trascrizione_audio").toString());

            case "3":
                return new EsercizioTipologia3(
                        document.getId().toString(),
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

    private void caricaSchedaFirebase(String nomeScheda, String userId, String pazienteId, ArrayList<String> eserciziSelezionati, RecyclerView recyclerView, List<Integer> checkedPositions, FirebaseFirestore db){

        LinearProgressIndicator progressIndicator = getView().findViewById(R.id.progressBarCreazioneScheda);
        progressIndicator.setVisibility(View.VISIBLE);

        // Crea un oggetto Map per contenere i dati da inserire nel documento
        Map<String, Object> data = new HashMap<>();
        data.put("nomeScheda", nomeScheda);
        data.put("logopedista", userId);
        data.put("figlio", pazienteId);


        Log.d(TAG, "Esercizi selezionati ehrthrhr: " + eserciziSelezionati);
        for (int i = 0; i < eserciziSelezionati.size(); i++) {
            ArrayList<String> eserciziSchedaCompleti = new ArrayList<>();

            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(checkedPositions.get(i));
            TextInputEditText giornoEsercizio = viewHolder.itemView.findViewById(R.id.giorno_esercizio);
            TextInputLayout textInputLayoutGiornoEsercizio = viewHolder.itemView.findViewById(R.id.giorno_esercizio_layout);
            if(giornoEsercizio.getText().toString().isEmpty()){
                textInputLayoutGiornoEsercizio.setErrorIconDrawable(null);
                textInputLayoutGiornoEsercizio.setError("Inserisci una data");
                return;
            }


            eserciziSchedaCompleti.add(eserciziSelezionati.get(i));
            eserciziSchedaCompleti.add("non completato");
            eserciziSchedaCompleti.add(giornoEsercizio.getText().toString());

            Log.d(TAG, "Esercizio: " + eserciziSchedaCompleti);
            data.put("esercizio" + i, eserciziSchedaCompleti);
        }

        // Aggiungi i dati a una nuova raccolta con un ID generato automaticamente
        db.collection("schede")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    progressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Scheda creata con successo", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot aggiunto con ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    progressIndicator.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Errore durante il caricamento della scheda", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Errore durante l'aggiunta del documento", e);
                });

    }

    private void addScheda(FirebaseFirestore db, String nomeScheda, String userId) {
        //Creo un array per salvare gli esercizi selezionati
        ArrayList<String> eserciziSelezionati = new ArrayList<>();

        Log.d(TAG, "Esercizi ricevuti: " + checkedPositions);
        for (int i = 0; i < checkedPositions.size(); i++) {
            //Recupero l'id dell'esercizio selezionato
            db.collection("esercizi")
                    .whereEqualTo("nome", eserciziList.get(checkedPositions.get(i)).getNome().toString())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "Esercizio ID: " + document.getId());
                                eserciziSelezionati.add(document.getId());
                                Log.d(TAG, "Esercizi selezionati diocane: " + eserciziSelezionati);
                            }
                            //Salvo la scheda nel database
                            caricaSchedaFirebase(nomeScheda, userId, pazienteId, eserciziSelezionati, recyclerView, checkedPositions, db);
                        } else {
                            Log.e(TAG, "Errore durante la query per gli esercizi", task.getException());
                        }
                    });
        }
    }
 }
