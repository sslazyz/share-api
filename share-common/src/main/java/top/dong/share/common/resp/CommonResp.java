package top.dong.share.common.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonResp<T> {
    /**
     * 业务失败或成功
     */
    private Boolean success = true;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 返回泛型数据，自定义类型
     */
    private T data;

}
