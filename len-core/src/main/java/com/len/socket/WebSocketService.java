package com.len.socket;

import com.len.util.LenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint(value = "/webSocket/{userId}")
public class WebSocketService {

    private static ConcurrentHashMap<String, WebSocketService> socketClient = new ConcurrentHashMap<>();

    private Session session;

    public static ConcurrentHashMap<String, WebSocketService> getClients() {
        return socketClient;
    }

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        this.session = session;
        socketClient.put(userId, this);
        log.info("【websocket消息】有新的连接, 总数:{}", socketClient.size());
    }

    @OnClose
    public void onClose(Session session) {
        socketClient.remove(session.getId());
        log.info("【websocket消息】连接断开, 总数:{}", socketClient.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端发来的消息:{}", message);
    }

    /**
     * 推消息给个人
     *
     * @param response
     * @param userId
     * @throws IOException
     */
    public void sendMessage(LenResponse response, String userId) throws IOException {
        WebSocketService webSocketService = socketClient.get(userId);
        if (webSocketService != null) {
            try {
                webSocketService.session.getBasicRemote().sendObject(response);
            } catch (EncodeException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 广播消息
     *
     * @param response
     */
    public void sendMessageBroad(LenResponse response) {
        for (Entry<String, WebSocketService> webSocket : socketClient.entrySet()) {
            log.info("【websocket消息】广播消息, message={}", response);
            try {
                webSocket.getValue().session.getBasicRemote().sendObject(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
