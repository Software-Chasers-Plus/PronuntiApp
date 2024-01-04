package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class figliHolderView extends RecyclerView.ViewHolder {
    ImageView imageViewFiglio;
    TextView textViewNomeFiglio, textViewLogopedistaFiglio;
    public figliHolderView(@NonNull View itemView) {
        super(itemView);
        imageViewFiglio = itemView.findViewById(R.id.figlio_imageview);
        textViewNomeFiglio = itemView.findViewById(R.id.nome_figlio);
        textViewLogopedistaFiglio = itemView.findViewById(R.id.logopedista_figlio);
    }
}
