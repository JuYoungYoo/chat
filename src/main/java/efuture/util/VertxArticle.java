package efuture.util;

import com.google.gson.Gson;
import com.nhncorp.mods.socket.io.SocketIOServer;
import com.nhncorp.mods.socket.io.SocketIOSocket;
import com.nhncorp.mods.socket.io.impl.DefaultSocketIOServer;
import com.nhncorp.mods.socket.io.impl.Room;
import efuture.domain.msg.MsgMemberVO;
import efuture.domain.msg.MsgVO;
import efuture.service.MsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.impl.DefaultVertx;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 2017-07-26.l
 * 접속자 login했을 시는 onConnect로 해결이 되지만,
 * 페이지 변동 될 때마다 연결이 끊기게(disconnect) 된다.
 * 1. facebook를 본 결과 onConnection은 원할하게 되지만, disconnect는 하지 않고 oncconection 시 DB에 정보를 담아 새로고침 시 login한 List를 불러오는 방식
 * 2. naver cafe 접속 리스트 또한 새로고침 버튼 클릭 시 변경이 되는 방식이다.
 * - socket.io 자체가 팝업을 기반으로 만들어,
 */
@Component
@PropertySource("classpath:properties/vertx.properties")
public class VertxArticle extends Verticle{

    @Autowired
    private MsgService msgService;
    @Autowired
    private Environment env;
    private SocketIOServer io;
    private ArrayList<MsgMemberVO> memberList = new ArrayList<>();
    private HashMap<String, Room> rooms = new HashMap<>();

    private Gson gson = new Gson();

    public void start() {
        int port = Integer.parseInt(env.getProperty("port"));
        final Vertx vertx = new DefaultVertx();
        final HttpServer server = vertx.createHttpServer();   // HttpServer 생성
        io = new DefaultSocketIOServer(vertx, server);
        io.sockets().onConnection(new Handler<SocketIOSocket>() {   // io.sockets().onConnection() : Connection 이벤트 연결
            public void handle(final SocketIOSocket socket) {
                socket.on("connection", new Handler<JsonObject>() {
                    public void handle(JsonObject jsonObject) {
                        MsgMemberVO user = gson.fromJson(jsonObject.toString(), MsgMemberVO.class);
                        user.setLoginYn(true);
                        if(memberList.contains(user)){
                            for (int i = 0; i < memberList.size(); i++) {
                                if (memberList.get(i).getUserid().equals(user.getUserid())) {
                                    memberList.set(i,user);
                                    /*  페이지 전환 */
                                    ArrayList<String> roomList = msgService.getRoomList(jsonObject.getString("userid"));
                                    Room room;
                                    for (String roomKey : roomList) {
                                        socket.join(roomKey);
                                        if (rooms.containsKey(roomKey)) {
                                            room = rooms.get(roomKey);
                                            room.push(socket.getId());
                                            rooms.put(roomKey, room);
                                        }
                                    }
                                }
                            }
                        }else{
                            memberList.add(user);
                        }
                        io.sockets().emit("memberlist", gson.toJson(memberList));
                    }
                });

                socket.on("disconn", new Handler<JsonObject>() {    // socket.on : 특정 키 명으로 요청시 동작
                    public void handle(JsonObject jsonObject) {
                        MsgMemberVO user = gson.fromJson(jsonObject.toString(), MsgMemberVO.class);
                        user.setLoginYn(false);

                        ArrayList<String> roomList = null;
                        roomList = msgService.getRoomList(user.getUserid());

                        if (!roomList.isEmpty()) {
                            for (String roomKey : roomList) {
                                if (rooms.containsKey(roomKey)) {
                                    socket.leave(roomKey);
                                    Room room = rooms.get(roomKey);
                                    room.remove(socket.getId());
                                    rooms.put(roomKey, room);
                                }
                            }
                        }
                        io.sockets().emit("memberlist", gson.toJson(memberList));
                    }
                });
                // 메세지 전송
                socket.on("send", new Handler<JsonObject>() {
                    public void handle(JsonObject jsonObject) {
                        MsgVO msg = new MsgVO();
                        msg.setSeq(jsonObject.getString("seq"));
                        msg.setSendId(jsonObject.getString("sendId"));
                        msg.setMsg(jsonObject.getString("msg"));
                        try {
                            msgService.insertMsg(msg);
                            io.sockets().in(jsonObject.getString("seq")).emit("resp", gson.toJson(msg));
                        } catch (Exception e) {
                            socket.in(msg.getSeq()).to(msg.getSendId()).emit("resp", "메세지 전송에 실패하였습니다.");
                            e.printStackTrace();
                        }
                    }
                });
                // 채팅방 입장
                socket.on("join", new Handler<JsonObject>() {
                    public void handle(final JsonObject jsonObject) {
                        String roomSeq = jsonObject.getString("seq");
                        socket.join(roomSeq);
                        socket.set("room", jsonObject, new Handler<Void>() {
                            @Override
                            public void handle(Void aVoid) {
                                String roomSeq = jsonObject.getString("seq");
                                Room room;
                                if (!rooms.containsKey(roomSeq)) room = new Room();
                                else room = rooms.get(roomSeq);
                                room.push(socket.getId());
                                rooms.put(roomSeq, room);
//                                socket.in(roomSeq).broadcast().emit("msg", jsonObject); // 입장 잘됬는지 확인 이벤트
                            }
                        });
                    }
                });
            }
        });
        server.listen(port);
    }
    public SocketIOServer getIo() {
        return io;
    }

    @PostConstruct  // 서버 ON시
    public void initOnApplication() {
        this.start();
    }

    @PreDestroy  // 서버 OFF시
    public void initOffApplication() {
        this.stop();
    }

}
