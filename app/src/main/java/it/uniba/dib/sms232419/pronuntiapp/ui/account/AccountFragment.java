package it.uniba.dib.sms232419.pronuntiapp.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Document;

import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms232419.pronuntiapp.GiocoActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve nome e cognome genitore
        db.collection("genitori")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                            // Set nome
                            EditText account_nome_genitore = view.findViewById(R.id.dettaglio_account_nome_genitore);
                            account_nome_genitore.setText(documentSnapshot.getString("Nome"));

                            // Set cognome
                            EditText account_cognome_genitore = view.findViewById(R.id.dettaglio_account_cognome_genitore);
                            account_cognome_genitore.setText(documentSnapshot.getString("Cognome"));

                    }
                });

        // Retrieve DataRegistrazione
        db.collection("genitori")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        // Set DataRegistrazione
                        TextView account_data_registrazione = view.findViewById(R.id.dettaglio_account_data_registrazione);
                        account_data_registrazione.setText(documentSnapshot.getString("DataRegistrazione"));
                    }
                });

        // Conto numero figli del genitore loggato
        db.collection("figli").whereEqualTo("genitore", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        long nroFigli = task.getResult().size();
                        TextView account_nro_figli = view.findViewById(R.id.dettaglio_account_nro_figli);
                        account_nro_figli.setText(String.valueOf(nroFigli));
                    }
                });

        // Controllo se l'utente loggato è un genitore
        db.collection("genitori")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        // Set DataRegistrazione
                        TextView account_tipo_utente = view.findViewById(R.id.dettaglio_account_tipo_utente);
                        account_tipo_utente.setText("Genitore");
                    }
                });

        // Retrieve email
        EditText account_email = view.findViewById(R.id.dettaglio_account_email_genitore);
        account_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        /*
        // retrieve codice fiscale
        db.collection("genitori")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // DocumentSnapshot contiene i dati del documento
                        if (task.getResult().exists()) {
                            String codiceFiscale = task.getResult().getString("CodiceFiscale");
                            // Ora puoi fare qualcosa con il codice fiscale, ad esempio stamparlo
                            TextView account_codice_fiscale = view.findViewById(R.id.codice_fiscale);
                            account_codice_fiscale.setText(codiceFiscale);
                        }
                    } else {
                        // Gestisci eventuali errori nel recupero dei dati
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }
                });
         */

        LinearLayout logout = view.findViewById(R.id.logout_textView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Credo dialog per confermare il logout
                // Creo dialog di conferma
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Stai per effettuare il logout.\n");
                builder.setMessage("Sei sicuro di voler uscire?");
                builder.setCancelable(false); // L'utente non può chiudere il dialog cliccando fuori da esso

                // Personalizzazione dello stile dell'AlertDialog
                builder.setIcon(R.drawable.alert_svgrepo_com); // Icona critica

                // Aggiunta dei pulsanti
                builder.setPositiveButton("Si", (dialog, which) -> {
                    // Logout
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), AccessoActivity.class));
                    getActivity().finish();
                });

                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });


                // Mostra il dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}