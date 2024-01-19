package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentPrenotazioniBinding;
import it.uniba.dib.sms232419.pronuntiapp.ui.esercizi.EserciziViewModel;

public class PrenotazioniFragment extends Fragment {

    private FragmentPrenotazioniBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EserciziViewModel dashboardViewModel =
                new ViewModelProvider(this).get(EserciziViewModel.class);

        binding = FragmentPrenotazioniBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
