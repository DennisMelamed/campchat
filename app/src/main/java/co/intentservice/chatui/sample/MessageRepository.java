package co.intentservice.chatui.sample;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import co.intentservice.chatui.models.ChatMessage;

class MessageRepository {

    private MessageDao mMessageDao;
    private LiveData<List<ChatMessage>> mAllMessages;

    // Note that in order to unit test the MessageRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    MessageRepository(Application application) {
        MessageRoomDatabase db = MessageRoomDatabase.getDatabase(application);
        mMessageDao = db.messageDao();
        mAllMessages = mMessageDao.getAllMessages();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<ChatMessage>> getAllMessages() {
        return mAllMessages;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(ChatMessage Message) {
        MessageRoomDatabase.databaseWriteExecutor.execute(() -> {
            mMessageDao.insert(Message);
        });
    }
}
