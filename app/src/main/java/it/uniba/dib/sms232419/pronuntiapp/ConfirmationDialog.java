package it.uniba.dib.sms232419.pronuntiapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ConfirmationDialog {

    public interface ConfirmationListener {
        void onConfirm();
        void onCancel();
    }

    public static void showConfirmationDialog(Context context, String message, final ConfirmationListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onConfirm();
                        }
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onCancel();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
