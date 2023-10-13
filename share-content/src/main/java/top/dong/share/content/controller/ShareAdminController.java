package top.dong.share.content.controller;


import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.dong.share.content.domain.entity.Share;
import top.dong.share.content.service.ShareService;

import java.util.List;

@RestController
@RequestMapping("/admin/share")
public class ShareAdminController {

    @Resource
    private ShareService shareService;

    @GetMapping(value = "/list")
    public List<Share> getSharesNotYet() {
        return shareService.queryShareSNotYet();
    }
}
