package top.dong.share.user.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusEventLog {
    private Long id;
    private Long userId;
    private Integer value;
    private String description;
    private String event;
    private Date createTime;
}
