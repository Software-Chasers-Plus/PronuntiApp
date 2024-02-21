package it.uniba.dib.sms232419.pronuntiapp;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class RecordAudio {

    private static final String LOG_TAG = "AudioRecordTest";

    private static MediaRecorder recorder = null;
    private static MediaPlayer   player = null;

    // Requesting permission to RECORD_AUDIO

    public static void onRecord(boolean start, String fileName) {
        if (start) {
            startRecording(fileName);
        } else {
            stopRecording();
        }
    }

    public static void onPlay(boolean start, String fileName) {
        if (start) {
            startPlaying(fileName);
        } else {
            stopPlaying();
        }
    }

    private static void startPlaying(String fileName) {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private static void stopPlaying() {
        player.release();
        player = null;
    }

    private static void startRecording(String fileName) {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private static void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }
}