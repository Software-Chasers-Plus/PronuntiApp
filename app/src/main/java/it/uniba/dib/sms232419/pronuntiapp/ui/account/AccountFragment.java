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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve nome e cognome genitore
        db.collection("genitori")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
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
                        // Set DataRegistrazione
                        TextView account_tipo_utente = view.findViewById(R.id.dettaglio_account_tipo_utente);
                        account_tipo_utente.setText(R.string.genitore);
                    }
                });

        // Retrieve email
        EditText account_email = view.findViewById(R.id.dettaglio_account_email_genitore);
        account_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        LinearLayout logout = view.findViewById(R.id.logout_textView);
        logout.setOnClickListener(v -> {
            // Credo dialog per confermare il logout
            // Creo dialog di conferma
            BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(requireActivity())
                    .setTitle("Stai per effettuare il logout.")
                    .setAnimation(R.raw.logout_anim)
                    .setMessage("Sei sicuro di voler uscire?")
                    .setCancelable(false)
                    .setPositiveButton("Si", (dialogInterface, which) -> {
                        // Logout
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), AccessoActivity.class));
                        requireActivity().finish();
                    })
                    .setNegativeButton("No", (dialogInterface, which) -> dialogInterface.dismiss())
                    .setAnimation("logout_anim.json")
                    .build();

            mDialog.show();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}