package efuture.domain.msg;

import lombok.Data;

import java.util.Date;

/**
 * Created by user on 2017-08-07.
 */
@Data
public class MsgVO {

    private String seq;            // 방번호
    private String sendId;      // 발송 아이디
    private String msg;
    private Date date = new Date();
}
