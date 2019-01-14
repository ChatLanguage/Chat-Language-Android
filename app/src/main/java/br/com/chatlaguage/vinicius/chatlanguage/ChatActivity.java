package br.com.chatlaguage.vinicius.chatlanguage;

import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private GroupAdapter adapter;
    private User user;
    private User me;
    private EditText editChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chat);

        user = (User) getIntent().getExtras().getParcelable("user");
        getSupportActionBar().setTitle(user.getUsername());

        RecyclerView rv = findViewById(R.id.recycler_chat);
        editChat = findViewById(R.id.edit_chat);
        Button btnChat = findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(User.class);
                        fetchMessages();
                    }
                });

    }

    private void fetchMessages() {
        if(me != null) {
            String fromId = me.getUuid();
            String toId = user.getUuid();

            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                            if(documentChanges != null) {
                                for (DocumentChange doc: documentChanges
                                     ) {
                                    if (doc.getType() == DocumentChange.Type.ADDED) {
                                        Message message = doc.getDocument().toObject(Message.class);
                                        adapter.add(new MessageItem(message));

                                    }
                                }
                            }
                        }
                    });
        }


    }

    private void sendMessage() {
        String text = editChat.getText().toString();
        editChat.setText(null);
        String fromId = FirebaseAuth.getInstance().getUid();
        String toId = user.getUuid();
        long timestamp = System.currentTimeMillis();


        Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);

        if(!message.getText().isEmpty()) {
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste",e.getMessage(), e);
                        }
                    });
            FirebaseFirestore.getInstance().collection("/conversations")
                    .document(toId)
                    .collection(fromId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Teste",e.getMessage(), e);
                        }
                    });





        }
    }

    private class MessageItem extends Item<ViewHolder> {


        private final br.com.chatlaguage.vinicius.chatlanguage.Message message;

        private MessageItem(br.com.chatlaguage.vinicius.chatlanguage.Message message) {
            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtMsg = viewHolder.itemView.findViewById(R.id.txt_msg);
            ImageView imgMessage = viewHolder.itemView.findViewById(R.id.img_message_user);

            txtMsg.setText(message.getText());
            Picasso.get()
                    .load(user.getProfileUrl())
                    .into(imgMessage);
        }
        @Override
        public int getLayout() {
            return message.getFromId() == FirebaseAuth.getInstance().getUid()
                    ? R.layout.item_from_message
                    : R.layout.item_to_message;
        }
    }

}
