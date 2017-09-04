package efuture.domain.msg;

import lombok.Data;

/**
 * Created by user on 2017-08-09.
 */
@Data
public class RoomVO {

    // room 정보
    // seq
    // name = default receive_id
    // room name

    private int seq;
    private String roomName;
    private String userid;

}
