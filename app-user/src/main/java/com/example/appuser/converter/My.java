package com.example.appuser.converter;

import com.example.appuser.converter.annotion.TransfUser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Component
public class My extends BaseConverter {

    @Override
    protected String getRelaValue(Set<String> annotationType, String codeCls, String sourceValue) {
        String writeValue = "";
        if (!StringUtils.isEmpty(codeCls)) {
//                        writeValue=StCodeUtil.getCodeName(me.getCodeCls(),writeValue);
            writeValue = "mmmmmmm";
        } else if (annotationType.contains(TransfUser.class.getName())) {
            // String rs = (Optional).ofNullable(cache.hget("sys_user_id_map",sysUserId)).orElse(null);
            if (sourceValue == "11022") {
                writeValue = "张三";
            }
            if (sourceValue == "22099") {
                writeValue = "历史";
            }
        }
        return writeValue;
    }

}
