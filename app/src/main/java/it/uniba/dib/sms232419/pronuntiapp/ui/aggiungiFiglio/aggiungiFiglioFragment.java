package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungiFiglio;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.MainActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;

public class aggiungiFiglioFragment extends Fragment {

    EditText nomeFiglio,congnomeFiglio, codiceFiscaleFiglio, emailLogopedista;

    Button confermaAggiungiFiglio;
    MainActivity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aggiungi_figli_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nomeFiglio = view.findViewById(R.id.nome_figlio);
        congnomeFiglio = view.findViewById(R.id.cognome_figlio);
        codiceFiscaleFiglio = view.findViewById(R.id.codiFiscale_figlio);
        emailLogopedista = view.findViewById(R.id.email_logopedista_figlio);
        confermaAggiungiFiglio = view.findViewById(R.id.conferma_aggiungi_figlio_button);
        confermaAggiungiFiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeFiglio.getText().toString();
                String cognome = congnomeFiglio.getText().toString();
                String codiceFiscale = codiceFiscaleFiglio.getText().toString();
                String emailLogopedista = aggiungiFiglioFragment.this.emailLogopedista.getText().toString();
                if(nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || emailLogopedista.isEmpty()){
                    Toast.makeText(mActivity, "Registrazione fallita", Toast.LENGTH_SHORT).show();
                }else{
                    String genitoreUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String , Object> figlio = new HashMap<>();
                    figlio.put("Nome", nome);
                    figlio.put("cognome", cognome);
                    figlio.put("codiceFiscale", codiceFiscale);
                    figlio.put("Logopedista", emailLogopedista);
                    db.collection("/genitori/"+genitoreUid+"/figli")
                            .add(figlio)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(mActivity, "Figlio aggiunto con successo", Toast.LENGTH_SHORT).show();
                                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                        navController.navigate(R.id.navigation_home);
                                    }else{
                                        Toast.makeText(mActivity, "Registrazione fallita", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });

    }


}
