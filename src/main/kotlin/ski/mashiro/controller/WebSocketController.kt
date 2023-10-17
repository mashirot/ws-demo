package ski.mashiro.controller

import com.github.yitter.idgen.YitIdHelper
import org.springframework.web.bind.annotation.*
import ski.mashiro.ws.WebSocketServer

@RestController
@RequestMapping("/api/rest/ws")
class WebSocketController(
    private val webSocketServer: WebSocketServer,
) {

    @PostMapping("/broadcast")
    fun broadcast(@RequestBody msg: String) {
        webSocketServer.broadcast(msg)
    }

    @PostMapping("/sendMsg/user/{userId}")
    fun sendMsg2User(@PathVariable userId: Long, @RequestBody msg: String) {
        webSocketServer.sendMsg2User(userId, msg)
    }

    @PostMapping("/sendMsg/users")
    fun sendMsg2Users(@RequestParam userIds: LongArray, @RequestBody msg: String) {
        webSocketServer.sendMsg2Users(userIds, msg)
    }

    @GetMapping("/userId")
    fun getUserId(): String {
        return YitIdHelper.nextId().toString()
    }
}