package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class ImpostazioniGiocoFragment extends Fragment{

    GiocoActivity giocoActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        giocoActivity = (GiocoActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_impostazioni_gioco, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.carousel_recycler_view);
        ArrayList<Integer> arrayList = new ArrayList<>();

        arrayList.add(R.drawable.deserto);
        arrayList.add(R.drawable.antartide);
        arrayList.add(R.drawable.giungla);

        sfondoAdapter adapter = new sfondoAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(adapter);

        RelativeLayout impostazioni = view.findViewById(R.id.impostazioni_gioco);
        TextView sfondoSelezionato = view.findViewById(R.id.sfondo_selezionato);
        adapter.setOnItemClickListener(new sfondoAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, Integer path) {
                switch (path){
                    case 0:
                        giocoActivity.sfondo = 0;
                        sfondoSelezionato.setText("Deserto");
                        break;
                    case 1:
                        giocoActivity.sfondo = 1;
                        sfondoSelezionato.setText("Antartide");
                        break;
                    case 2:
                        giocoActivity.sfondo = 2;
                        sfondoSelezionato.setText("Giungla");
                        break;
                }
            }
        });
    }
}