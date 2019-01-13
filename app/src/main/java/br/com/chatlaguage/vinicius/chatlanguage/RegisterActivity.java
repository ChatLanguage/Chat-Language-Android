package br.com.chatlaguage.vinicius.chatlanguage;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEditUsername;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnEnter;
    private EditText mUsername;
    private Button mBtnImage;
    private Uri mSelecetedUri;
    private ImageView mImgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);

        mEditEmail = findViewById(R.id.edit_email);
        mEditPassword = findViewById(R.id.edit_password);
        mBtnEnter = findViewById(R.id.btn_enter);
        mUsername = findViewById(R.id.edit_username);
        mBtnImage = findViewById(R.id.btn_selected_photo);
        mImgPhoto = findViewById(R.id.img_photo);
        mEditUsername = findViewById(R.id.edit_username);

        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });


        mBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            mSelecetedUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelecetedUri);
                mImgPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                mBtnImage.setAlpha(0);
            } catch(IOException e) {

            }

        }

    }
    public void selectPhoto() {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);
    }
    public void createUser() {
       String email = mEditEmail.getText().toString();
       String senha = mEditPassword.getText().toString();
        String nome = mEditUsername.getText().toString();
       if(nome == null || nome.isEmpty() || email == null || email.isEmpty() || senha.isEmpty() || senha == null) {
           Toast.makeText(this, "Nome, Senha e E-mail devem ser preenchidas",Toast.LENGTH_SHORT).show();
           return;
       }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()) {
                               Log.i("Sucesso", task.getResult().getUser().getUid());
                                saveUserInFirebase();
                           }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Error",e.getMessage().toString());
            }
        });
    }

    private void saveUserInFirebase() {
            String fileName = UUID.randomUUID().toString();
            final StorageReference ref  = FirebaseStorage.getInstance().getReference("/images/" + fileName);
            ref.putFile(mSelecetedUri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Teste",e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                                Log.i("teste",uri.toString());

                                String uid = FirebaseAuth.getInstance().getUid();
                                String nome = mEditUsername.getText().toString();
                                String profileUri = uri.toString();
                                User user = new User(uid,nome, profileUri);

                            FirebaseFirestore.getInstance().collection("users")
                                    .document(uid)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(RegisterActivity.this, MyChatActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                        Log.i("teste", e.getMessage());
                                }
                            });

                        }
                    });
                }
            });
    }


}
