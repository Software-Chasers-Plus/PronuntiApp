package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentEserciziBinding;

public class EserciziFragment extends Fragment {

    private FragmentEserciziBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EserciziViewModel dashboardViewModel =
                new ViewModelProvider(this).get(EserciziViewModel.class);

        binding = FragmentEserciziBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;


        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton aggiungi_esercizio_bottone = view.findViewById(R.id.raised_button);

        Intent intent = new Intent(getActivity(), CreazioneEsercizi.class);

        aggiungi_esercizio_bottone.setOnClickListener(v -> startActivity(intent));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}