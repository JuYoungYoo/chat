package efuture.domain.msg;

import lombok.Data;

/**
 * Created by user on 2017-08-07.
 */
@Data
public class MsgDTO {

    private String seq;            // 방번호
    private String sendId;      // 발송 아이디
    private String receiveId;   // 수신 아이디
    private boolean existYn = false;    // 새로 만든 방 or 기존에 생성되어있는 방인지 판단 ( true : 기존 , false : 생성 ) > 생성 시 tb_msg_context insert
    private String[] memberList;

    private int startNo = 0;
    private int endNo = 10;

    public boolean getExistYn() {
        return existYn;
    }
}
