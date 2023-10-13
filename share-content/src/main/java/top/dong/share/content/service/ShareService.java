package top.dong.share.content.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.dong.share.common.resp.CommonResp;
import top.dong.share.content.domain.dto.ExchangeDTO;
import top.dong.share.content.domain.dto.ShareRequestDTO;
import top.dong.share.content.domain.entity.MidUserShare;
import top.dong.share.content.domain.entity.Share;
import top.dong.share.content.domain.entity.User;
import top.dong.share.content.domain.resp.ShareResp;
import top.dong.share.content.feign.UserAddBonusMsgDTO;
import top.dong.share.content.feign.UserService;
import top.dong.share.content.mapper.MidUserShareMapper;
import top.dong.share.content.mapper.ShareMapper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;

    @Resource
    private MidUserShareMapper midUserShareMapper;

    @Resource
    private UserService userService;

    /**
     * 查询某个用户可见的资源列表
     */
    public List<Share> getList(String title,Integer pageNo,Integer pageSize, Long userId) {
        //构造查询条件
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        //按照 id 降序 查询所有数据
        if (StringUtils.isNotEmpty(title)) {
            wrapper.like(Share::getTitle, title);
        }
        //过滤出所有已经通过审核的数据并需要显示的数据
        wrapper.eq(Share::getAuditStatus, "PASS").eq(Share::getShowFlag, true);

        //内置的分页对象
        Page<Share> page = Page.of(pageNo, pageSize);
        //执行条件查询
        List<Share> shares = shareMapper.selectList(page,wrapper);

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

    public ShareResp findById(Long shareId){
        Share share = shareMapper.selectById(shareId);
        CommonResp<User> commonResp = userService.getUser(share.getUserId());
        return ShareResp.builder().share(share).nickname(commonResp.getData().getNickname()).avatarUrl(commonResp.getData().getAvatarUrl()).build();
    }

    public Share exchange(ExchangeDTO exchangeDTO) {
        Long userId = exchangeDTO.getUserId();
        Long shareId = exchangeDTO.getShareId();
        //1,根据 id 查询share，校验需要兑换的资源是否存在
        Share share = shareMapper.selectById(shareId);
        if (share == null) {
            throw new IllegalArgumentException("该分享不存在！");
        }

        //2，如果当前用户已经兑换过该分享，则直接返回该分享(不需要扣积分)
        MidUserShare midUserShare=midUserShareMapper.selectOne(new QueryWrapper<MidUserShare>().lambda()
                .eq(MidUserShare::getUserId,userId)
                .eq(MidUserShare::getShareId,shareId));
        if (midUserShare != null) {
            return share;
        }
        //3,看用户积分是否足够
        CommonResp<User> commonResp=userService.getUser(userId);
        User user = commonResp.getData();
        //兑换这条资源需要的积分
        Integer price = share.getPrice();
        //看积分是否足够
        if (price > user.getBonus()) {
            throw new IllegalArgumentException("用户积分不够!");
        }
        ///4．修改积分(*-1就是负值扣分)
        userService.updateBonus(UserAddBonusMsgDTO.builder().userId(userId).bonus(price * -1).build());
        ///5．向 mid_user_share表插入一条数据，让这个用户对于这条资源拥有了下载权限
        midUserShareMapper.insert(MidUserShare.builder().userId(userId).shareId(shareId).build());
        return share;
    }

    public int contribute(ShareRequestDTO shareRequestDTO) {
        Share share=Share.builder()
                .isOriginal(shareRequestDTO.getIsOriginal())
                .author(shareRequestDTO.getAuthor())
                .price(shareRequestDTO.getPrice())
                .downloadUrl(shareRequestDTO.getDownloadUrl())
                .summary(shareRequestDTO.getSummary())
                .buyCount(0)
                .title(shareRequestDTO.getTitle())
                .userId(shareRequestDTO.getUserId())
                .cover(shareRequestDTO.getCover())
                .createTime(new Date())
                .updateTime(new Date())
                .showFlag(false)
                .auditStatus("NOT_YET")
                .reason("未审核")
                .build();
        return shareMapper.insert(share);
    }

    /**
     * 我的投稿
     * @param pageNo
     * @param pageSize
     * @param userId
     * @return
     */
    public List<Share> myContribute(Integer pageNo,Integer pageSize,Long userId){
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getUserId, userId);
        Page<Share> page = Page.of(pageNo, pageSize);
        return shareMapper.selectList(page, wrapper);
    }

    public List<Share>queryShareSNotYet(){
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Share::getId);
        wrapper.eq(Share::getShowFlag, false)
                .eq(Share::getAuditStatus, "NOT_YET");
        return shareMapper.selectList(wrapper);
    }
}







