package co.intentservice.chatui.sample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;

public class MainActivity extends AppCompatActivity {

    private MessageViewModel mMessageViewModel;

    private PortClosedBroadcastReceiver onNoticePortClosed;
    private PortOpenBroadcastReceiver onNoticePortOpen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the service that deals with LoRA transmissions
        Intent service_intent = new Intent(this, LoraHandler.class);
        startService(service_intent);


        // Connect to message database
        ChatView chatView = findViewById(R.id.chat_view);
        mMessageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        // add message to database when we send it
        chatView.setOnSentMessageListener(chatMessage -> {
            mMessageViewModel.insert(chatMessage);
            return true;
        });





        // display the messages in the database when it updates.
        mMessageViewModel.getAllMessages().observe(this, messages -> {
            // Update the cached copy of the words in the adapter.
            chatView.clearMessages();
            //chatView.addMessage(last_message);
            chatView.addMessages(new ArrayList<>(messages));

        });




        // change the color of the indicator when the dongle connects
        onNoticePortOpen = new PortOpenBroadcastReceiver();
        onNoticePortClosed = new PortClosedBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNoticePortOpen, new IntentFilter("portCreated"));
        LocalBroadcastManager.getInstance(this).registerReceiver(onNoticePortClosed, new IntentFilter("portClosed"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, LoraHandler.class));
    }





    private class PortOpenBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ImageView img = findViewById(R.id.imageView);
            img.setImageResource(android.R.drawable.presence_online);

        }
    }

    private class PortClosedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ImageView img = findViewById(R.id.imageView);
            img.setImageResource(android.R.drawable.presence_busy);

        }
    }


}
