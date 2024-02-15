package it.uniba.dib.sms232419.pronuntiapp.ui.eserciziBambino;

import android.widget.EditText;

import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;

public interface ClickEserciziBambinoListener {
    void onItemClick(int position);

    void onItemLongClick(int position, MaterialCardView cardView);
    void onDettaglioClick(int position);

    void onCalendarioClick(int position, EditText dataEsercizio);
}
