package it.uniba.dib.sms232419.pronuntiapp.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.accessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AccountViewModel accountViewModel =
                new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        String userDisplyName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        TextView account_username = root.findViewById(R.id.account_username);
        account_username.setText(userDisplyName);

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        TextView account_email = root.findViewById(R.id.account_email);
        account_email.setText(userEmail);

        TextView logout = root.findViewById(R.id.logout_textView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), accessoActivity.class));
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