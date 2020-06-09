package co.intentservice.chatui.sample;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import co.intentservice.chatui.models.ChatMessage;

public class MessageViewModel extends AndroidViewModel {

    private MessageRepository mRepository;

    private LiveData<List<ChatMessage>> mAllMessages;

    public MessageViewModel (Application application) {
        super(application);
        mRepository = new MessageRepository(application);
        mAllMessages = mRepository.getAllMessages();
    }

    LiveData<List<ChatMessage>> getAllMessages() { return mAllMessages; }

    public void insert(ChatMessage message) { mRepository.insert(message); }
}
