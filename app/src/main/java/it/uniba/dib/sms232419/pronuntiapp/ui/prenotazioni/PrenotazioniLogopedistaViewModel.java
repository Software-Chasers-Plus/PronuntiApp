package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrenotazioniLogopedistaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PrenotazioniLogopedistaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is prenotazioni logopedista fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}