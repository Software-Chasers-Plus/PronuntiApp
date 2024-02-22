package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class GameView extends View {

    private Bitmap backgroundImage;
    private final Bitmap checkpointImage;
    private int[] checkpointXPositions;
    private int[] checkpointYPositions;
    private final int checkpointWidth;
    private final int checkpointHeight;
    private final Paint roadPaint;
    private final GiocoActivity giocoActivity;
    private int xPersonaggio,yPersonaggio;
    private Bitmap personaggioImage;
    private int checkPointAttuale;
    private boolean personaggioInMovimento = false;
    private int xBottone,yBottone;
    private long mInizioAnimazione = 0L;
    private static final int DISEGNA_BOTTONE = 1;
    private static final long DURATA_ANIMAZIONE = 1000L;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == DISEGNA_BOTTONE) {
                long elapsedTime = SystemClock.uptimeMillis() - mInizioAnimazione;
                if (elapsedTime >= DURATA_ANIMAZIONE) {
                    personaggioInMovimento = false;
                    invalidate();
                }
            }
        }
    };
    private final Scheda scheda;
    public GameView(Context context, AttributeSet attrs, Scheda scheda) {
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
        this.scheda = scheda;
        Log.d("GameView", "Numero esercizi: " + scheda.getNumeroEsercizi());
        // Calcola le posizioni dei checkpoint in base alle dimensioni dello schermo
        calculateCheckpointPositions();

        // Inizializza il pennello per disegnare la strada
        roadPaint = new Paint();
        roadPaint.setColor(Color.GRAY);
        roadPaint.setStrokeWidth(20);
        roadPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.leone);
        switch (giocoActivity.personaggioSelezionato){
            case 0:
                personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.leone);
                break;
            case 1:
                personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.husky);
                break;
            case 2:
                personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.serpente);
                break;
            case 3:
                personaggioImage = BitmapFactory.decodeResource(getResources(), R.drawable.pinguino);
                break;
        }
        xPersonaggio = checkpointXPositions[0];
        yPersonaggio = checkpointYPositions[0];
        checkPointAttuale = 0;
    }

    private void calculateCheckpointPositions() {
        int numCheckpoints = scheda.getNumeroEsercizi(); // Numero di checkpoint desiderati
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        // Calcola lo spazio verticale tra i checkpoint
        int topMargin = (int) (screenHeight * 0.2); // Margine superiore
        int bottomMargin = (int) (screenHeight * 0.3); // Margine inferiore

        // Calcola le posizioni dei checkpoint lungo un percorso sinuoso
        checkpointXPositions = new int[numCheckpoints];
        checkpointYPositions = new int[numCheckpoints];


        if(numCheckpoints == 1){
            checkpointXPositions[0] = screenWidth / 2 - checkpointWidth / 2;
            checkpointYPositions[0] = screenHeight / 2 - checkpointHeight / 2;
        }else {
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
        Paint paintCircle = new Paint();
        paintCircle.setColor(Color.RED);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setStrokeWidth(5); // Imposta lo spessore del contorno

        int centerX, centerY;

        for (int i = 0; i < checkpointXPositions.length; i++) {
            // Ridimensiona l'immagine del checkpoint
            Bitmap scaledCheckpoint = Bitmap.createScaledBitmap(checkpointImage, checkpointWidth, checkpointHeight, true);
            canvas.drawBitmap(scaledCheckpoint, checkpointXPositions[i], checkpointYPositions[i], null);

            // Calcola il centro del checkpoint
            centerX = checkpointXPositions[i] + checkpointWidth / 2;
            centerY = checkpointYPositions[i] + checkpointHeight / 2;

            // Disegna un cerchio rosso attorno al checkpoint
            if(scheda.getEsercizi().get(i).get(1).equals("completato"))
                paintCircle.setColor(Color.GREEN);
            else
                paintCircle.setColor(Color.RED);

            canvas.drawCircle(centerX, centerY, checkpointWidth / 2 + 5, paintCircle); // Aggiungi 5 per compensare lo spessore del contorno

            if((!personaggioInMovimento && i == checkPointAttuale) && !scheda.getEsercizi().get(i).get(1).equals("completato")){
                // Disegna l'immagine solo se il checkpoint è stato raggiunto
                int imageWidth = 100; // Larghezza dell'immagine
                int imageHeight = 100; // Altezza dell'immagine
                xBottone = centerX - imageWidth / 2;
                yBottone = (centerY + checkpointHeight / 2)+40;
                Bitmap playButtonLevel = BitmapFactory.decodeResource(getResources(), R.drawable.bottone_avvia_gioco);
                Bitmap scaledImage = Bitmap.createScaledBitmap(playButtonLevel, imageWidth, imageHeight, true);
                canvas.drawBitmap(scaledImage, xBottone, yBottone, null);
            }

        }
        // Calcola il centro del checkpoint
        centerX = xPersonaggio + checkpointWidth / 2;
        centerY = yPersonaggio + checkpointHeight / 2;
        int personaggioWidth = 170; // Larghezza del personaggio
        int personaggioHeight = 170; // Altezza del personaggio
        Bitmap scaledPersonaggio = Bitmap.createScaledBitmap(personaggioImage, personaggioWidth, personaggioHeight, true);
        canvas.drawBitmap(scaledPersonaggio, centerX - personaggioWidth / 2, centerY - personaggioHeight / 2, null);
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
                    if(i == checkPointAttuale + 1 || i == checkPointAttuale - 1) {
                        checkPointAttuale = i;
                        // Esegui un'azione quando viene toccato un checkpoint
                        ObjectAnimator animX = ObjectAnimator.ofFloat(this, "personaggioX", xPersonaggio, checkpointXPositions[i]);
                        ObjectAnimator animY = ObjectAnimator.ofFloat(this, "personaggioY", yPersonaggio, checkpointYPositions[i]);
                        AnimatorSet animSetXY = new AnimatorSet();
                        animSetXY.playTogether(animX, animY);
                        animSetXY.setDuration(1000); // Imposta la durata dell'animazione in millisecondi (1 secondo)
                        personaggioInMovimento = true;
                        mInizioAnimazione = SystemClock.uptimeMillis();
                        final Message goAheadMessage = mHandler.obtainMessage(DISEGNA_BOTTONE);
                        mHandler.sendMessageAtTime(goAheadMessage, mInizioAnimazione + DURATA_ANIMAZIONE);
                        animSetXY.start();
                        break;
                    }
                } else if (touchX >= xBottone && touchX <= xBottone + 170
                        && touchY >= yBottone && touchY <= yBottone + 170) {
                    if(!scheda.getEsercizi().get(checkPointAttuale).get(1).equals("completato")){
                        giocoActivity.avviaEsercizio(scheda.getEsercizi().get(checkPointAttuale));
                    }
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

    // Metodi getter e setter per personaggioX
    public float getPersonaggioX() {
        return xPersonaggio;
    }

    public void setPersonaggioX(float x) {
        xPersonaggio = (int) x;
        invalidate(); // Richiama onDraw() per ridisegnare la vista con la nuova posizione del personaggio
    }

    // Metodi getter e setter per personaggioY
    public float getPersonaggioY() {
        return yPersonaggio;
    }

    public void setPersonaggioY(float y) {
        yPersonaggio = (int) y;
        invalidate(); // Richiama onDraw() per ridisegnare la vista con la nuova posizione del personaggio
    }
}
