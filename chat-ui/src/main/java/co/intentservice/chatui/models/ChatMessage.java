package co.intentservice.chatui.models;

import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.concurrent.TimeUnit;


/**
 * Chat Message model used when ChatMessages are required, either to be sent or received,
 * all messages that are to be shown in the chat-ui must be contained in this model.
 */
@Entity(tableName = "message_table")
@TypeConverters(ChatMessage.MessageTypeConverter.class)
public class ChatMessage {
    @NonNull
    private String message;
    @PrimaryKey
    @NonNull
    private long timestamp;
    @NonNull
    @TypeConverters(ChatMessage.MessageTypeConverter.class)
    private Type type;
    @NonNull
    private String sender;

//    public ChatMessage(String message, long timestamp, Type type) {
//        this.message = message;
//        this.timestamp = timestamp;
//        this.type = type;
//    }

    public ChatMessage(String message, long timestamp, Type type, String sender) {
//        this(message, timestamp, type);
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.sender = sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }


    public String getFormattedTime() {

        long oneDayInMillis = TimeUnit.DAYS.toMillis(1); // 24 * 60 * 60 * 1000;

        long timeDifference = System.currentTimeMillis() - timestamp;

        return timeDifference < oneDayInMillis
                ? DateFormat.format("hh:mm a", timestamp).toString()
                : DateFormat.format("dd MMM - hh:mm a", timestamp).toString();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public enum Type {
        SENT, RECEIVED
    }
    public static class MessageTypeConverter {

        @TypeConverter
        public ChatMessage.Type toType(int typeInt) {
            if(typeInt == 0)
            {
                return ChatMessage.Type.SENT;
            }
            else
            {
                return ChatMessage.Type.RECEIVED;
            }

        }

        @TypeConverter
        public int fromType(ChatMessage.Type messageType) {
            if(messageType == ChatMessage.Type.SENT) {
                return 0;
            }
            else{
                return 1;
            }
        }
    }

}
