package efuture.domain.msg;

import lombok.Data;

/**
 * 로그인 정보
 * Created by user on 2017-03-29.
 */
@Data
public class MsgMemberVO {

    private int seq;
    private String userid;  // 로그인 아이디
    private String name;    // 로그인 이름
    private String grade;   // 권한
    private String dept;    // 부서
    private boolean loginYn;

    private String sessionId; // 세션 ID

}
