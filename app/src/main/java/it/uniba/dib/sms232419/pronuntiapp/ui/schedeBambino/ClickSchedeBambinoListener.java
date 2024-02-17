package it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino;

import android.widget.EditText;

import com.google.android.material.card.MaterialCardView;

public interface ClickSchedeBambinoListener {
    void onItemClick(int position);

    void onEliminaClick(int position);

    void onAvviaGiocoClick(int position);
}
