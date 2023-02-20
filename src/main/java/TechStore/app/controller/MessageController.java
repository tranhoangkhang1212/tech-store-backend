package TechStore.app.controller;

import TechStore.app.dto.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageController {
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message send(Message message) throws Exception {
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return message;
    }
}