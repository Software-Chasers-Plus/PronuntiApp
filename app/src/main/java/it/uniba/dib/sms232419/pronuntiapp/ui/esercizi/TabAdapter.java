package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EsercizioDenominazioneImmagine();
            case 1:
                return new EsercizioRipetizioneParole();
            case 2:
                return new EsercizioRiconoscimentoCoppie();
            default:
                throw new IllegalArgumentException("Posizione non valida" + position);
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
