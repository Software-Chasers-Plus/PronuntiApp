package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class ClasssificaGiocoHolderView extends RecyclerView.ViewHolder {
    TextView posizioneTextView, nomeTextView, punteggioTextView;

    private ClickClassificaGiocoListener clickClassificaGiocoListener;

    public ClasssificaGiocoHolderView(View itemView, ClickClassificaGiocoListener listener) {
        super(itemView);
        this.clickClassificaGiocoListener = listener;

        posizioneTextView = itemView.findViewById(R.id.posizioneTextView);
        nomeTextView = itemView.findViewById(R.id.nomeTextView);
        punteggioTextView = itemView.findViewById(R.id.punteggioTextView);

        itemView.setOnClickListener(v -> {
            if(clickClassificaGiocoListener != null){
                clickClassificaGiocoListener.onBambinoClick(getAdapterPosition());
            }
        });
    }
}
