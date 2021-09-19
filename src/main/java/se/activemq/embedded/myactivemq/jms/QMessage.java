package se.activemq.embedded.myactivemq.jms;

public class QMessage {
    public QHeader header;
    public Object body = null;
    public QMessage() { }
    public QMessage(QHeader header, Object body) {
        this.header = header;
        this.body = body;
    }
}
