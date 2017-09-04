package efuture.service;

import efuture.domain.msg.MsgDTO;
import efuture.domain.msg.MsgVO;
import efuture.domain.msg.RoomVO;

import java.util.ArrayList;

/**
 * 업무시간통계
 * Created by user on 2017-03-29.
 */
public interface MsgService {

    /*  방 유무 체크  */
    MsgDTO existRoom(MsgDTO vo);
    /*  Max seq 가져오기 */
    int maxRoomSeq(MsgDTO vo);
    /*  방 만들기 */
    int insertRoom(int seq, String userId);   // seq, userid
    /* 채팅 내용 저장 */
    int insertMsg(MsgVO vo);
    /* 채팅 내용 리스트 */
    ArrayList<MsgVO> getMsgList(MsgDTO vo);

    /* 채팅방에 속한 사람 리스트 */
    ArrayList<String> getRoomList(String userId);
    /* 채팅방에 속한 사람 리스트 */
    ArrayList<String> roomMemberList(int seq);


}
