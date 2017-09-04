package efuture.persistence;

import efuture.domain.msg.MsgDTO;
import efuture.domain.msg.MsgVO;
import efuture.domain.msg.RoomVO;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * Created by user on 2017-08-07.
 */
public interface MsgDAO {
    /*  방 유무 체크   */
    MsgDTO existRoom(MsgDTO vo);
    /*  최근 방 seq 가져오기 */
    int maxRoomSeq(MsgDTO vo);
    /*  방 만들기 */
    int insertRoom(@Param("seq") int seq,@Param("userId") String userId);   // seq, userid
    /* 채팅 내용 저장 */
    int insertMsg(MsgVO vo);
    /* 채팅 내용 리스트 */
    ArrayList<MsgVO> getMsgList(MsgDTO vo);

    ArrayList<String> roomMemberList(int seq);
    ArrayList<String> getRoomList(String userId);
}
