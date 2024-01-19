package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungiFiglio;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.FigliAdapter;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.HomeFragment;

public class AggiungiFiglioFragment extends Fragment implements ClickLogopedistiSimiliListener {

    List<Figlio> figli;

    List<Logopedista> logopedisti = new ArrayList<>();

    Button confermaAggiungiFiglio;

    MainActivityGenitore mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivityGenitore) getActivity();

        //recupero i figli dal bundle passato al fragment
        if(getArguments() != null){
            figli = getArguments().getParcelableArrayList("figli");
            Log.d("aggiungiFiglioFragment", "Figli recuperati: "+figli.size());
        }else{
            Log.d("aggiungiFiglioFragment", "Bundle nullo");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aggiungi_figli_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Figlio figlioDaAggiungere;
        EditText nomeFiglio = view.findViewById(R.id.nome_figlio);
        EditText congnomeFiglio = view.findViewById(R.id.cognome_figlio);
        EditText codiceFiscaleFiglio = view.findViewById(R.id.codiFiscale_figlio);
        EditText emaillogopedista = view.findViewById(R.id.email_logopedista_figlio);
        RecyclerView logopedistiRecyclerView = view.findViewById(R.id.logopedistiSimiliRecyclerView);
        emaillogopedista.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            //rende la recycler view dei logopedisti visibile solo quando l'edit text ha il focus
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    logopedistiRecyclerView.setVisibility(View.GONE);
                }else{
                    logopedistiRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        emaillogopedista.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailLogopedistaDaCercare = s.toString();
                Log.d("AggiungiFiglioFragment", "email logopedista: "+emailLogopedistaDaCercare);
                if(!emailLogopedistaDaCercare.isEmpty()) {
                    // Converti la stringa in due stringhe, una rappresentante la stringa precedente e una rappresentante la successiva
                    String emailInferiore = emailLogopedistaDaCercare;
                    String emailSuperiore = emailLogopedistaDaCercare + "\uf8ff";

                    // Esegui la query per recuperare tutti i logopedisti con eamil simile a quella inseirita
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("logopedisti")
                            .whereGreaterThanOrEqualTo("Email", emailInferiore)
                            .whereLessThanOrEqualTo("Email", emailSuperiore)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        logopedisti.clear();
                                        Log.d("AggiungiFiglioFragment", "Query eseguita");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> logopedista = document.getData();
                                            logopedisti.add(new Logopedista(logopedista.get("Nome").toString(), logopedista.get("Cognome").toString(),
                                                    logopedista.get("Email").toString(), logopedista.get("CodiceFiscale").toString(), logopedista.get("Abilitazione").toString() == "true" ? true : false,
                                                    document.getId()));
                                        }
                                        Log.d("AggiungiFiglioFragment", "Logopedisti trovati: " + logopedisti.size());
                                        logopedistiRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity.getApplicationContext()));
                                        logopedistiRecyclerView.setAdapter(new LogopedistiSimiliAdapter(mActivity.getApplicationContext(), logopedisti, AggiungiFiglioFragment.this));
                                    } else {
                                        Log.d("AggiungiFiglioFragment", "Query fallita");
                                    }
                                }
                            });
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        EditText editTextDate = view.findViewById(R.id.data_nascita_figlio);
        ImageView iconaCalendario = view.findViewById(R.id.imageViewCalendar);
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
                                editTextDate.setText(dataSelezionata);
                            }
                        }, anno, mese, giorno);
                datePickerDialog.show();
            }
        });

        TextView errNome = view.findViewById(R.id.err_nome);
        TextView errCognome = view.findViewById(R.id.err_cognome);
        TextView errCodiceFiscale = view.findViewById(R.id.err_codiceFiscale);
        TextView errDataNascita = view.findViewById(R.id.err_dataNascita);
        TextView errLogopedista = view.findViewById(R.id.err_logopedista);

        confermaAggiungiFiglio = view.findViewById(R.id.conferma_aggiungi_figlio_button);
        confermaAggiungiFiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nomeFiglio.getText().toString().isEmpty()) {
                    errNome.setVisibility(View.VISIBLE);
                } else{
                    errNome.setVisibility(View.GONE);
                }

                if (congnomeFiglio.getText().toString().isEmpty()) {
                    errCognome.setVisibility(View.VISIBLE);
                }else{
                    errCognome.setVisibility(View.GONE);
                }

                if (codiceFiscaleFiglio.getText().toString().isEmpty()) {
                    errCodiceFiscale.setVisibility(View.VISIBLE);
                }else{
                    errCodiceFiscale.setVisibility(View.GONE);
                }

                if (editTextDate.getText().toString().isEmpty()) {
                    errDataNascita.setVisibility(View.VISIBLE);
                }else {
                    errDataNascita.setVisibility(View.GONE);
                }

                if (emaillogopedista.getText().toString().isEmpty()) {
                    errLogopedista.setVisibility(View.VISIBLE);
                }else{
                    errLogopedista.setVisibility(View.GONE);
                }

                if(errNome.getVisibility() == View.VISIBLE || errCognome.getVisibility() == View.VISIBLE || errCodiceFiscale.getVisibility() == View.VISIBLE || errDataNascita.getVisibility() == View.VISIBLE || errLogopedista.getVisibility() == View.VISIBLE){
                    return;
                }

                boolean logopedistaTrovato = false;
                for(int i=0; i<logopedisti.size(); i++){
                    if(logopedisti.get(i).getEmail().equals(emaillogopedista.getText().toString())){
                        logopedistaTrovato = true;
                    }
                }

                if(!logopedistaTrovato){
                   errLogopedista.setText("Logopedista non trovato");
                   errLogopedista.setVisibility(View.VISIBLE);;
                   return;
                }


                String genitoreUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String , Object> figlio = new HashMap<>();
                figlio.put("nome", nomeFiglio.getText().toString());
                figlio.put("cognome", congnomeFiglio.getText().toString());
                figlio.put("codiceFiscale", codiceFiscaleFiglio.getText().toString());
                figlio.put("dataNascita", editTextDate.getText().toString());
                figlio.put("logopedista", emaillogopedista.getText().toString());
                figlio.put("genitore", genitoreUid);
                db.collection("figli")
                        .add(figlio)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    figli.add(new Figlio(nomeFiglio.getText().toString(), congnomeFiglio.getText().toString(),
                                            codiceFiscaleFiglio.getText().toString(), emaillogopedista.getText().toString(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(), editTextDate.getText().toString()));
                                    Toast.makeText(mActivity, "Figlio aggiunto con successo", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                    navController.navigate(R.id.navigation_home);
                                }else{
                                    Toast.makeText(mActivity, "Registrazione fallita", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Log.d("AggiungiFiglioFragment", "Logopedista selezionato: "+logopedisti.get(position).getEmail());
        EditText emailLogopedista = getView().findViewById(R.id.email_logopedista_figlio);
        emailLogopedista.setText(logopedisti.get(position).getEmail());
        logopedisti.clear();
        RecyclerView logopedistiRecyclerView = getView().findViewById(R.id.logopedistiSimiliRecyclerView);
        logopedistiRecyclerView.setVisibility(View.GONE);
        emailLogopedista.clearFocus();
    }
}
