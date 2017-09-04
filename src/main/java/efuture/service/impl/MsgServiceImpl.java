package efuture.service.impl;

import efuture.domain.msg.MsgDTO;
import efuture.domain.msg.MsgVO;
import efuture.domain.msg.RoomVO;
import efuture.persistence.MsgDAO;
import efuture.service.MsgService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by user on 2017-08-07.
 */
@Service
public class MsgServiceImpl implements MsgService{

    @Autowired private SqlSession session;

    /*  방 유무 체크  */
    @Override
    public RoomVO getRoomInfo(int seq) {
        return session.getMapper(MsgDAO.class).getRoomInfo(seq);
    }

    @Override
    public MsgDTO existRoom(MsgDTO vo) {
        return session.getMapper(MsgDAO.class).existRoom(vo);
    }
    /*  최근 방 seq 가져오기 */
    @Override
    public int maxRoomSeq(MsgDTO vo) {
        return session.getMapper(MsgDAO.class).maxRoomSeq(vo);
    }
    /*  방 만들기 */
    @Override
    public int insertRoom(int seq, String userId) {
        return session.getMapper(MsgDAO.class).insertRoom(seq, userId);
    }
    /* 채팅 내용 저장 */
    @Override
    public int insertMsg(MsgVO vo) {
        return session.getMapper(MsgDAO.class).insertMsg(vo);
    }
    /* 채팅 내용 리스트 */
    @Override
    public ArrayList<MsgVO> getMsgList(MsgDTO vo) {
        return session.getMapper(MsgDAO.class).getMsgList(vo);
    }
    @Override
    public ArrayList<String> roomMemberList(int seq) {
        return session.getMapper(MsgDAO.class).roomMemberList(seq);
    }
    /* 채팅방 리스트 (userid 기준) */
    @Override
    public ArrayList<String> getRoomList(String userId) {
        return session.getMapper(MsgDAO.class).getRoomList(userId);
    }
}
