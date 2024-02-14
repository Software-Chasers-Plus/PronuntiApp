package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

// Interfaccia per gestire gli eventi di clic sugli elementi della lista dei figli
public interface ClickEserciziListener {
    void onItemClick(int position);

    void onDeleteClick(int position);
}

