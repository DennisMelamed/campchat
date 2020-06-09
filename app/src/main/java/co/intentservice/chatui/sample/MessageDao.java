package co.intentservice.chatui.sample;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import co.intentservice.chatui.models.ChatMessage;

@Dao
public interface MessageDao {

    // allowing the insert of the same message multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ChatMessage message);

    @Query("DELETE FROM message_table")
    void deleteAll();

    @Query("SELECT * from message_table ORDER BY timestamp ASC")
    LiveData<List<ChatMessage>> getAllMessages();


}