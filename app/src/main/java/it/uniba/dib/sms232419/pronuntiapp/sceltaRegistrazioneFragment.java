package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class sceltaRegistrazioneFragment extends Fragment {
    private accessoActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (accessoActivity) context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scelta_registrazione_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.scelta_genitore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.login_signup_fragment, new registrazioneGenitoreFragment(), null)
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.findViewById(R.id.scelta_logopedista).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.login_signup_fragment, new registrazioneLogopedistaFragment(), null)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
