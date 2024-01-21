package it.uniba.dib.sms232419.pronuntiapp.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        // retrieve nome genitore
        String userDisplayName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        TextView account_username = root.findViewById(R.id.account_username);
        account_username.setText(userDisplayName);

        // retrieve email genitore
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        TextView account_email = root.findViewById(R.id.account_email);
        account_email.setText(userEmail);


        // retrieve codice fiscale
        db.collection("logopedisti")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // DocumentSnapshot contiene i dati del documento
                        if (task.getResult().exists()) {
                            String codiceFiscale = task.getResult().getString("CodiceFiscale");
                            // Ora puoi fare qualcosa con il codice fiscale, ad esempio stamparlo
                            TextView account_codice_fiscale = root.findViewById(R.id.codice_fiscale);
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


        TextView logout = root.findViewById(R.id.logout_textView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), AccessoActivity.class));
                getActivity().finish();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}