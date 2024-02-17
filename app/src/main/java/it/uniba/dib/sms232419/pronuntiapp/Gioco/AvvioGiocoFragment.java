package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.uniba.dib.sms232419.pronuntiapp.LoginFragment;
import it.uniba.dib.sms232419.pronuntiapp.R;


public class AvvioGiocoFragment extends Fragment {

    private boolean isMediaPlayerPaused = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.avvio_gioco_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Debug per verificare quando il MediaPlayer viene avviato
        Log.d("AvvioGiocoFragment", "MediaPlayer avviato");

        ImageView backButton = view.findViewById(R.id.exit_button_start_game);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        ImageView impostazioni = view.findViewById(R.id.settings_button_start_game);
        impostazioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia la transizione al fragment ImpostazioniGiocoFragment e lo aggiunge al back stack
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.avvio_gioco_fragment, ImpostazioniGiocoFragment.class, null)
                        .addToBackStack(null) // Aggiunge al back stack
                        .commit();
            }
        });

        ImageView startButton = view.findViewById(R.id.play_button_start_game);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Avvia la transizione al fragment GiocoFragment e lo aggiunge al back stack
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.avvio_gioco_fragment, GiocoFragment.class, null)
                        .addToBackStack(null) // Aggiunge al back stack
                        .commit();
            }
        });
    }
}
