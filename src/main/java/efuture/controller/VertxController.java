package efuture.controller;

import com.google.gson.Gson;
import efuture.domain.msg.MsgDTO;
import efuture.domain.msg.MsgVO;
import efuture.service.MsgService;
import efuture.util.VertxArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by user on 2017-08-04.
 */
@Controller
@RequestMapping("/chat")
public class VertxController {

    private static final Logger logger = LoggerFactory.getLogger(VertxController.class);
    @Autowired private MsgService msgService;
    @Autowired private VertxArticle vertxArticle;
    private Gson gson = new Gson();

    /**
     * [채팅방 접속 방법1] 신규 채팅방 개설
     * @param vo
     * @return
     */
    @RequestMapping(value = "/inroom", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> inRoom(MsgDTO vo){
        HashMap<String,Object> resultMap = new HashMap<>();
        ArrayList<MsgVO> content = null;
        MsgDTO room = msgService.existRoom(vo);
        vo.setMemberList(new String[]{vo.getSendId(), vo.getReceiveId()});
        vo.setSeq(room.getSeq());
        vo.setExistYn(room.getExistYn());
        if(vo.getExistYn()){
            // 이전 채팅 내역 가져오기
            content = msgService.getMsgList(vo);
            Collections.reverse(content);
        }else{
            // 새방
            vo.setSeq(String.valueOf(msgService.maxRoomSeq(vo) + 1)); // seq 생성
            for(String member : vo.getMemberList()) msgService.insertRoom(Integer.parseInt(vo.getSeq()), member); // 방 만들기
        }
        vertxArticle.getIo().sockets().emit("joinlist", gson.toJson(vo)); // 방으로 초대
        resultMap.put("param", vo);
        resultMap.put("content", content);
        return resultMap;
    }

    /**
     * [ 채팅방 접속 방법 2 ] 기존에 방이 생성되어있는 경우
     * @param vo room seq 번호 아는 경우
     * @return
     */
    @RequestMapping("/content")
    @ResponseBody
    public ArrayList<MsgVO> contentList(MsgDTO vo){
        ArrayList<MsgVO> msgList = null;
        msgList = msgService.getMsgList(vo);    // 이전 채팅내용
        Collections.reverse(msgList);
        return msgList;
    }
}
