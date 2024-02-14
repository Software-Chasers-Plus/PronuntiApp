package it.uniba.dib.sms232419.pronuntiapp.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentAccountBinding;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentAccountLogopedistaBinding;

public class AccountFragmentLogopedista extends Fragment {

    private FragmentAccountLogopedistaBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountLogopedistaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    // La logica è stata spostata in onViewCreater per evitare di fare andare l'applicazione in ANR
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve nome e cognome logopedista
        TextView nomeLogopedista = view.findViewById(R.id.dettaglio_acccount_nome_logopedista);
        EditText nomeFull = view.findViewById(R.id.dettaglio_account_logopedista_fullname);
        db.collection("logopedisti")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            nomeLogopedista.setText("Ciao " + task.getResult().getString("Nome") + "!");
                            nomeFull.setText(task.getResult().getString("Nome") + " " + task.getResult().getString("Cognome"));
                        }
        });

        // Retrieve DataRegistrazione
        db.collection("logopedisti")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        // Set DataRegistrazione
                        TextView account_data_registrazione = view.findViewById(R.id.dettaglio_account_data_registrazione_logopedista);
                        account_data_registrazione.setText(documentSnapshot.getString("DataRegistrazione"));
                    }
                });


        // Conto il numero di pazienti del logopedista
        db.collection("figli").whereEqualTo("logopedista", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        TextView account_numero_pazienti = view.findViewById(R.id.dettaglio_account_logopedista_nro_pazienti);
                        account_numero_pazienti.setText(String.valueOf(task.getResult().size()));
                    }
                });


        // Retrieve matricola logopedista
        db.collection("logopedisti")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        // Set matricola
                        EditText account_matricola = view.findViewById(R.id.dettaglio_account_logopedista_matricola);
                        account_matricola.setText(documentSnapshot.getString("Matricola"));
                    }
                });

        // Retrieve email logopedista
        TextView account_email = view.findViewById(R.id.dettaglio_account_logopedista_email);
        account_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        LinearLayout logout = view.findViewById(R.id.logout_textView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), AccessoActivity.class));
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}