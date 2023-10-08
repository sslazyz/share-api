package top.dong.share.content.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.dong.share.content.domain.entity.MidUserShare;
import top.dong.share.content.domain.entity.Share;
import top.dong.share.content.mapper.MidUserShareMapper;
import top.dong.share.content.mapper.ShareMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;

    @Resource
    private MidUserShareMapper midUserShareMapper;

    /**
     * 查询某个用户可见的资源列表
     */
    public List<Share> getList(String title, Long userId) {
        //构造查询条件
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        //按照 id 降序 查询所有数据
        if (StringUtils.isNotEmpty(title)) {
            wrapper.like(Share::getTitle, title);
        }
        //过滤出所有已经通过审核的数据并需要显示的数据
        wrapper.eq(Share::getAuditStatus, "PASS").eq(Share::getShowFlag, true);
        //执行条件查询
        List<Share> shares = shareMapper.selectList(wrapper);

        //处理后的 Share 数据列表
        List<Share> sharesDeal;
        //如果用户未登录，那么 downloadUrl 全部设置为 null
        if (userId == null) {
            sharesDeal = shares.stream().peek(share -> share.setDownloadUrl(null)).collect(Collectors.toList());

        }
        //如果用户登录了，查询mid_user_share,如果没有数据，那么这条 share 的downloadurl 也设置为 null
        else {
            sharesDeal=shares.stream().peek(share->{
                MidUserShare midUserShare=midUserShareMapper.selectOne(new QueryWrapper<MidUserShare>().lambda()
                        .eq(MidUserShare::getUserId,userId)
                        .eq(MidUserShare::getShareId,share.getId()));
                if (midUserShare==null){
                    share.setDownloadUrl(null);
                }
            }).collect(Collectors.toList());
        }
        return sharesDeal;
    }
}
