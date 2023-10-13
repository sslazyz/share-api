package top.dong.share.content.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import top.dong.share.common.exception.BusinessException;
import top.dong.share.common.exception.BusinessExceptionEnum;
import top.dong.share.common.resp.CommonResp;
import top.dong.share.common.util.JwtUtil;
import top.dong.share.common.util.SnowUtil;
import top.dong.share.content.domain.entity.User;



import java.util.Date;

@FeignClient(value = "user-service",path = "/user")
public interface UserService {
    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    CommonResp<User> getUser(@PathVariable Long id);

    /**
     * 调用用户中心修改用户积分接口
     * @param userAddBonusMsgDTO 积分信息
     * @return
     */
    @PutMapping("/update-bonus")
    CommonResp<User> updateBonus(@RequestBody UserAddBonusMsgDTO userAddBonusMsgDTO);


}
