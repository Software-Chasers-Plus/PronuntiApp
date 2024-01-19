package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungiFiglio;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.ClickFigliListener;

public class LogopedistiSimiliHolderView extends RecyclerView.ViewHolder implements View.OnClickListener{
    ImageView imageViewFiglio;

    TextView textViewNomeLogopedista, textViewemailLogopedista;

    private ClickLogopedistiSimiliListener clickLogopedistiSimiliListener;

    // Costruttore che riceve un'istanza di ClickListener
    public LogopedistiSimiliHolderView(View itemView, ClickLogopedistiSimiliListener listener) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);
        // Salva il riferimento all'istanza di ClickListener
        this.clickLogopedistiSimiliListener = listener;

        imageViewFiglio = itemView.findViewById(R.id.logopedista_imageAccount);
        textViewNomeLogopedista = itemView.findViewById(R.id.nome_logopedista);
        textViewemailLogopedista = itemView.findViewById(R.id.email_logopedista);
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if(clickLogopedistiSimiliListener != null){
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickLogopedistiSimiliListener.onItemClick(getAdapterPosition());
        }
    }
}
