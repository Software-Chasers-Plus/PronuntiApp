package it.uniba.dib.sms232419.pronuntiapp.ui.account;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentAccountLogopedistaBinding;

public class AccountFragmentLogopedista extends Fragment {

    private FragmentAccountLogopedistaBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountLogopedistaBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    // La logica è stata spostata in onViewCreater per evitare di fare andare l'applicazione in ANR
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve nome e cognome logopedista
        TextView nomeLogopedista = view.findViewById(R.id.dettaglio_acccount_nome_logopedista);
        EditText nomeFull = view.findViewById(R.id.dettaglio_account_logopedista_fullname);
        db.collection("logopedisti")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .get()
                .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            nomeLogopedista.setText("Ciao, " + task.getResult().getString("Nome") + "!");
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

                        if(documentSnapshot.getBoolean("Albo")){
                            LinearLayout alboLayout = view.findViewById(R.id.layoutInAlbo);
                            alboLayout.setVisibility(View.VISIBLE);
                            TextView labelAlbo = view.findViewById(R.id.inAlboMessaggio);
                            labelAlbo.setText(R.string.la_matricola_contenuta_nell_albo_dei_logopedisti);
                            ImageView albo = view.findViewById(R.id.inAlboIcona);
                            albo.setImageResource(R.drawable.confirm_svgrepo_com);
                        }
                        else{
                            LinearLayout alboLayout = view.findViewById(R.id.layoutInAlbo);
                            alboLayout.setVisibility(View.VISIBLE);
                            TextView labelAlbo = view.findViewById(R.id.inAlboMessaggio);
                            labelAlbo.setText(R.string.la_tua_matricola_non_contenuta_nell_albo_dei_logopedisti);
                            ImageView albo = view.findViewById(R.id.inAlboIcona);
                            albo.setVisibility(View.GONE);
                        }

                    }
                });

        // Retrieve email logopedista
        TextView account_email = view.findViewById(R.id.dettaglio_account_logopedista_email);
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