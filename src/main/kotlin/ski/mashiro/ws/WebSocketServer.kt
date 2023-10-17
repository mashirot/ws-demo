package ski.mashiro.ws

import jakarta.websocket.OnClose
import jakarta.websocket.OnError
import jakarta.websocket.OnMessage
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.springframework.stereotype.Service
import ski.mashiro.annotation.Slf4j
import ski.mashiro.annotation.Slf4j.Companion.logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

@Service
@Slf4j
@ServerEndpoint("/api/ws/{userId}")
class WebSocketServer {
    private var session: Session? = null
    private var userId: Long? = null

    companion object {
        private val webSockets = CopyOnWriteArraySet< WebSocketServer>()
        private val sessionPool = ConcurrentHashMap<Long, Session>()
    }

    @OnOpen
    fun onOpen(session: Session, @PathParam("userId") userId: Long) {
        this.session = session
        this.userId = userId
        webSockets.add(this)
        sessionPool[userId] = session
        logger.info("【websocket消息】有新的连接, userId = $userId, 总数为: ${webSockets.size}")
    }

    @OnClose
    fun onClose() {
        webSockets.remove(this)
        sessionPool.remove(userId)
        logger.info("【websocket消息】有连接断开, userId = $userId, 总数为: ${webSockets.size}")
    }

    @OnMessage
    fun onMessage(message: String) {
        logger.info("【websocket消息】收到客户端消息: $message");
    }

    @OnError
    fun onError(session: Session, error: Throwable) {
        logger.error("【websocket消息】错误, userId: $userId, 原因: ${error.message}")
    }

    fun broadcast(msg: String) {
        logger.info("【websocket消息】广播消息: $msg")
        webSockets.forEach {
            try {
                if (it.session!!.isOpen) {
                    it.session!!.asyncRemote.sendText(msg)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMsg2User(userId: Long, msg: String) {
        val session = sessionPool[userId] ?: run {
            logger.error("userId: $userId 不在sessionPool中")
            return
        }
        try {
            session.userPrincipal
            if (session.isOpen) {
                logger.info("【websocket消息】单点消息: $msg")
                session.asyncRemote.sendText(msg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMsg2Users(userIds: LongArray, msg: String) {
        userIds.forEach {userId ->
            sendMsg2User(userId, msg)
        }
    }
}