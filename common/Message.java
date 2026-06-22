package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sender;
    private String recipient;
    private String content;
    private MessageType type;
    private String timestamp;

    public Message(String sender, String recipient, String content, MessageType type) {
        this.sender    = sender;
        this.recipient = recipient;
        this.content   = content;
        this.type      = type;
        this.timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // null recipient = broadcast to all
    public Message(String sender, String content, MessageType type) {
        this(sender, null, content, type);
    }

    public String getSender()    { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent()   { return content; }
    public MessageType getType() { return type; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}
