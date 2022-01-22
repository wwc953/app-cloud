package com.example.appuser.converter.example;

import com.example.appuser.converter.ResponseResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 转码示例
 */
@RestController
public class CoverController {
    @PostMapping("/reve")
    public ResponseResult<CoverBean> reve() {
        CoverBean convBean = new CoverBean();
        convBean.setDevCls("01");
        ResponseResult<CoverBean> result = new ResponseResult<>();
        result.setCode("00000");
        result.setData(convBean);
        return result;
    }

    @PostMapping("/reveList")
    public ResponseResult<List<CoverBean>> reveList() {
        List<CoverBean> list = new ArrayList<>();
        CoverBean convBean = new CoverBean();
        convBean.setDevCls("01");
        convBean.setTestPerson("11022");
        list.add(convBean);
        CoverBean convBean2 = new CoverBean();
        convBean2.setDevCls("02");
        convBean2.setTestPerson("22099");
        list.add(convBean2);
        ResponseResult<List<CoverBean>> result = new ResponseResult<>();
        result.setCode("00000");
        result.setData(list);
        return result;
    }
}
