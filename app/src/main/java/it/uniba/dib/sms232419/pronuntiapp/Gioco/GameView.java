package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class GameView extends View {

    private Bitmap backgroundImage;
    private Bitmap checkpointImage;
    private int[] checkpointXPositions;
    private int[] checkpointYPositions;
    private int checkpointWidth;
    private int checkpointHeight;
    private Paint roadPaint;
    private GiocoActivity giocoActivity;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        giocoActivity = (GiocoActivity) context;
        //seleziona l'immagine di sfondo
        switch (giocoActivity.sfondoSelezionato){
            case 0:
                backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.sfondo1_deserto);
                break;
            case 1:
                backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.sfondo2_antartide);
                break;
            case 2:
                backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.sfondo3_giungla);
                break;
        }
        // Carica l'immagine dello sfondo e del checkpoint
        checkpointImage = BitmapFactory.decodeResource(getResources(), R.drawable.checkpoint_image);

        // Imposta le dimensioni del checkpoint
        checkpointWidth = 200; // Larghezza ideale del checkpoint
        checkpointHeight = 200; // Altezza ideale del checkpoint

        // Calcola le posizioni dei checkpoint in base alle dimensioni dello schermo
        calculateCheckpointPositions();

        // Inizializza il pennello per disegnare la strada
        roadPaint = new Paint();
        roadPaint.setColor(Color.GRAY);
        roadPaint.setStrokeWidth(20);
        roadPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private void calculateCheckpointPositions() {
        int numCheckpoints = 7; // Numero di checkpoint desiderati
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calcola lo spazio verticale tra i checkpoint
        int verticalGap = (int) (screenHeight * 0.8) / (numCheckpoints - 1);
        int topMargin = (int) (screenHeight * 0.2); // Margine superiore
        int bottomMargin = (int) (screenHeight * 0.3); // Margine inferiore

        // Calcola le posizioni dei checkpoint lungo un percorso sinuoso
        checkpointXPositions = new int[numCheckpoints];
        checkpointYPositions = new int[numCheckpoints];

        for (int i = 0; i < numCheckpoints; i++) {
            if (i % 2 == 0) { // Se i è pari, muovi orizzontalmente
                checkpointXPositions[i] = screenWidth / 4; // Posizione X centrata
                checkpointYPositions[i] = topMargin + i * (screenHeight - topMargin - bottomMargin) / (numCheckpoints - 1); // Posizione Y lungo il percorso verticale
            } else { // Se i è dispari, muovi verticalmente
                checkpointXPositions[i] = screenWidth / 4 + screenWidth / 3; // Posizione X spostata a destra
                checkpointYPositions[i] = topMargin + (i - 1) * (screenHeight - topMargin - bottomMargin) / (numCheckpoints - 1); // Posizione Y lungo il percorso verticale
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Disegna lo sfondo
        canvas.drawBitmap(backgroundImage, null, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()), null);

        // Disegna le strade che collegano i checkpoint
        for (int i = 0; i < checkpointXPositions.length - 1; i++) {
            // Calcola i punti di controllo per disegnare la strada
            int startX = checkpointXPositions[i] + checkpointWidth / 2;
            int startY = checkpointYPositions[i] + checkpointHeight / 2;
            int endX = checkpointXPositions[i + 1] + checkpointWidth / 2;
            int endY = checkpointYPositions[i + 1] + checkpointHeight / 2;
            int controlX1 = startX;
            int controlY1 = startY + (endY - startY) / 3;
            int controlX2 = endX;
            int controlY2 = endY - (endY - startY) / 3;

            // Disegna la strada
            canvas.drawPath(createBezierPath(startX, startY, controlX1, controlY1, controlX2, controlY2, endX, endY), roadPaint);
        }

        // Disegna i checkpoint
        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(5); // Imposta lo spessore del contorno

        for (int i = 0; i < checkpointXPositions.length; i++) {
            // Ridimensiona l'immagine del checkpoint
            Bitmap scaledCheckpoint = Bitmap.createScaledBitmap(checkpointImage, checkpointWidth, checkpointHeight, true);
            canvas.drawBitmap(scaledCheckpoint, checkpointXPositions[i], checkpointYPositions[i], null);

            // Calcola il centro del checkpoint
            int centerX = checkpointXPositions[i] + checkpointWidth / 2;
            int centerY = checkpointYPositions[i] + checkpointHeight / 2;

            // Disegna un cerchio rosso attorno al checkpoint
            canvas.drawCircle(centerX, centerY, checkpointWidth / 2 + 5, redPaint); // Aggiungi 5 per compensare lo spessore del contorno

            // Disegna un personaggio sopra il primo checkpoint
            if (i == 0) {
                // Assumiamo che il personaggio sia rappresentato da un'immagine chiamata "personaggioImage"
                //seleziona l'immagine del personaggio
                Bitmap personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.leone);
                switch (giocoActivity.personaggioSelezionato){
                    case 0:
                        personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.leone);
                        break;
                    case 1:
                        personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.coccodrillo);
                        break;
                    case 2:
                        personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.serpente);
                        break;
                    case 3:
                        personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.panda);
                        break;
                }
                int personaggioWidth = 100; // Larghezza del personaggio
                int personaggioHeight = 100; // Altezza del personaggio
                Bitmap scaledPersonaggio = Bitmap.createScaledBitmap(personaggioImage, personaggioWidth, personaggioHeight, true);
                canvas.drawBitmap(scaledPersonaggio, centerX - personaggioWidth / 2, centerY - personaggioHeight / 2, null);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Controlla se il tocco è avvenuto su un checkpoint
            for (int i = 0; i < checkpointXPositions.length; i++) {
                if (touchX >= checkpointXPositions[i] && touchX <= checkpointXPositions[i] + checkpointWidth
                        && touchY >= checkpointYPositions[i] && touchY <= checkpointYPositions[i] + checkpointHeight) {
                    // Esegui un'azione quando viene toccato un checkpoint
                    break;
                }
            }
        }
        return true;
    }

    private android.graphics.Path createBezierPath(float startX, float startY, float controlX1, float controlY1, float controlX2, float controlY2, float endX, float endY) {
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(startX, startY);
        path.cubicTo(controlX1, controlY1, controlX2, controlY2, endX, endY);
        return path;
    }
}
