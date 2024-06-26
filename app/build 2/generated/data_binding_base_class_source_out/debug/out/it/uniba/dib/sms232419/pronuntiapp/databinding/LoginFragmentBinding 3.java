// Generated by view binder compiler. Do not edit!
package it.uniba.dib.sms232419.pronuntiapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import it.uniba.dib.sms232419.pronuntiapp.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LoginFragmentBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button loginButton;

  @NonNull
  public final EditText loginEmail;

  @NonNull
  public final EditText loginPassword;

  @NonNull
  public final TextView scegliRegistrazione;

  private LoginFragmentBinding(@NonNull LinearLayout rootView, @NonNull Button loginButton,
      @NonNull EditText loginEmail, @NonNull EditText loginPassword,
      @NonNull TextView scegliRegistrazione) {
    this.rootView = rootView;
    this.loginButton = loginButton;
    this.loginEmail = loginEmail;
    this.loginPassword = loginPassword;
    this.scegliRegistrazione = scegliRegistrazione;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LoginFragmentBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LoginFragmentBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.login_fragment, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LoginFragmentBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.login_button;
      Button loginButton = ViewBindings.findChildViewById(rootView, id);
      if (loginButton == null) {
        break missingId;
      }

      id = R.id.login_email;
      EditText loginEmail = ViewBindings.findChildViewById(rootView, id);
      if (loginEmail == null) {
        break missingId;
      }

      id = R.id.login_password;
      EditText loginPassword = ViewBindings.findChildViewById(rootView, id);
      if (loginPassword == null) {
        break missingId;
      }

      id = R.id.scegli_registrazione;
      TextView scegliRegistrazione = ViewBindings.findChildViewById(rootView, id);
      if (scegliRegistrazione == null) {
        break missingId;
      }

      return new LoginFragmentBinding((LinearLayout) rootView, loginButton, loginEmail,
          loginPassword, scegliRegistrazione);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
