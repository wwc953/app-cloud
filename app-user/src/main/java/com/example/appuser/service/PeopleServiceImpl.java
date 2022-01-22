package com.example.appuser.service;

import com.example.appuser.dao.PeopleMapper;
import com.example.appuser.model.People;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Random;

@Service
@Transactional(rollbackFor = Exception.class)
public class PeopleServiceImpl {
    @Autowired
    PeopleMapper mapper;

    public void batchCreate() {
        for (int i = 0; i < 100; i++) {

            People p = new People();
            StringBuilder sb2 = new StringBuilder();
            for (int j = 0; j < RandomUtils.nextInt(50); j++) {
                sb2.append(getRandomChar());
            }
            p.setAddress(sb2.toString());

            p.setAge(RandomUtils.nextInt(1000));

            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 2; j++) {
                sb.append(getRandomChar());
            }
            p.setName(sb.toString());

            p.setCreateTime(DateUtils.addDays(new Date(),RandomUtils.nextInt(90)));
            p.setUpdateTime(DateUtils.addYears(new Date(),RandomUtils.nextInt(5)));

            mapper.insertSelective(p);
        }
    }

    private static char getRandomChar() {
        String str = "";
        int hightPos;
        int lowPos;
        Random random = new Random();
        hightPos = (176 + Math.abs(random.nextInt(39)));
        lowPos = (161 + Math.abs(random.nextInt(93)));
        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();
        try {
            str = new String(b, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("错误");
        }
        return str.charAt(0);
    }
}
