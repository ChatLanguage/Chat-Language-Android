package br.com.chatlaguage.vinicius.chatlanguage;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnEnter;
    private TextView mTxtAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        mEditEmail = findViewById(R.id.edit_email);
        mEditPassword = findViewById(R.id.edit_password);
        mBtnEnter = findViewById(R.id.btn_enter);
        mTxtAccount = findViewById(R.id.txt_account);

      mBtnEnter.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String email = mEditEmail.getText().toString();
              String senha = mEditPassword.getText().toString();

              if(email == null || email.isEmpty() || senha.isEmpty() || senha == null) {
                  Toast.makeText(LoginActivity.this, "Nome e Senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
                  return;
              }

              FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      Log.i("Sucesso", task.getResult().getUser().getUid());
                      Intent intent = new Intent(LoginActivity.this, MyChatActivity.class);
                      startActivity(intent);
                  }
              }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      Log.i("Error",e.getMessage().toString());
                      return;
                  }
              });
          }
      });

        mTxtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
            }
        });

    }
}
