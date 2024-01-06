package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class figliHolderView extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView imageViewFiglio;

    TextView textViewNomeFiglio, textViewLogopedistaFiglio;

    private ClickFigliListener clickFigliListener;

    // Costruttore che riceve un'istanza di ClickListener
    public figliHolderView(View itemView, ClickFigliListener listener) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);
        // Salva il riferimento all'istanza di ClickListener
        this.clickFigliListener = listener;

        imageViewFiglio = itemView.findViewById(R.id.figlio_imageview);
        textViewNomeFiglio = itemView.findViewById(R.id.nome_figlio);
        textViewLogopedistaFiglio = itemView.findViewById(R.id.logopedista_figlio);
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if(clickFigliListener != null){
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickFigliListener.onItemClick(getAdapterPosition());
        }
    }
}
