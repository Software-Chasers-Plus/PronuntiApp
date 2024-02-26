package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GiocoFragment extends Fragment {
    private GiocoActivity giocoActivity;
    private Scheda scheda;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null && getArguments().getParcelable("scheda") != null) {
            scheda = getArguments().getParcelable("scheda");
            Log.d("GiocoFragment", "Scheda: " + scheda.getNome() + " caricata");
        }
        giocoActivity = (GiocoActivity) getActivity();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gioco, container, false);
        GameView gameView = new GameView(getContext(), null,scheda);
        LinearLayout layout = view.findViewById(R.id.linearLayout_gioco);
        layout.addView(gameView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!giocoActivity.mediaPlayer.isPlaying()){
            giocoActivity.mediaPlayer.start();
        }
    }
}
