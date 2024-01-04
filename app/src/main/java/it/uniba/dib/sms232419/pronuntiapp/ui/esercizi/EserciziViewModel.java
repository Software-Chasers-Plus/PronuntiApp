package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EserciziViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public EserciziViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}