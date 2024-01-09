package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungiFiglio;


import android.app.DatePickerDialog;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.MainActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class AggiungiFiglioFragment extends Fragment {

    List<Figlio> figli;

    Button confermaAggiungiFiglio;

    MainActivity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();

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
}
