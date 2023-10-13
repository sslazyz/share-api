package top.dong.share.content.feign;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBonusMsgDTO {
    /**
     * 为谁加积分
     */
    private Long userId;
    /**
     * 加多少分
     */
    private Integer bonus;
    /**
     * 信息描述
     *
     */
    private String description;
    /**
     * 积分事件(签到，兑换，投稿)
     */
    private String event;
}
