package it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino;

import android.widget.EditText;

import com.google.android.material.card.MaterialCardView;

public interface ClickEserciziBambinoListener {
    void onItemClick(int position, MaterialCardView cardView);

    void onDettaglioClick(int position);

    void onCalendarioClick(int position, EditText dataEsercizio);
}
