package com.example.apputil.transform.core;

import com.example.appstaticutil.response.ResponseResult;
import com.example.apputil.transform.annotion.EnableTransform;
import com.example.apputil.transform.annotion.TransfCode;
import com.example.apputil.transform.annotion.TransfUser;
import com.example.apputil.transform.model.TransformMethod;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class TransformConverter extends MappingJackson2HttpMessageConverter {

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        beforeWriternal(object, type);
        super.writeInternal(object, type, outputMessage);
    }

    private void beforeWriternal(Object object, Type type) {
        beforeWriternal(object, type, false);
    }

    private void beforeWriternal(Object object, Type type, Boolean flag) {
        if (type != null && ParameterizedTypeImpl.class.getName().equals(type.getClass().getName())) {
            ParameterizedType tp = (ParameterizedType) type;
            Type[] types = tp.getActualTypeArguments();
            if (types.length > 0 && ResponseResult.class.getName().equals(tp.getRawType().getTypeName())) {
                String name = types[0].getClass().getName();
                if (ParameterizedTypeImpl.class.getName().equals(name)) {
                    ParameterizedType subType = (ParameterizedType) types[0];
                    Type[] subTypes = subType.getActualTypeArguments();
                    //ResponseResult<List<VO>>
                    if (subTypes.length > 0
                            && List.class.getName().equals(subType.getRawType().getTypeName())
                            && Class.class.getName().equals(subTypes[0].getClass().getName())) {
                        Class clazz = (Class) subTypes[0];
                        dataHandler(object, clazz, true);
                    }
                    //ResponseResult<VO>
                } else if (Class.class.getName().equals(name)) {
                    Class clazz = (Class) types[0];
                    dataHandler(object, clazz, false);
                }
            }
        } else if (flag) {
            if (Class.class.getName().equals(type.getClass().getName())) {
                Class clazz = (Class) type;
                dataHandler(object, clazz, true);
            } else if (Class.class.getName().equals(type.getClass().getName())) {
                Class clazz = (Class) type;
                dataHandler(object, clazz, false);
            }
        }
    }

    private void dataHandler(Object object, Class<T> clazz, Boolean isArray) {
        Annotation ant = clazz.getAnnotation(EnableTransform.class);
        if (ant == null) {
            return;
        }
        List<Field> fds = Lists.newArrayList();
        fds.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class<? super T> tempClass = clazz.getSuperclass();
        while (tempClass != null) {
            fds.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        try {
            ResponseResult<Object> res = (ResponseResult) object;
            if (res != null && res.getData() != null && !fds.isEmpty()) {
                spiBug(fds, clazz, res.getData(), isArray);
            }
        } catch (Exception e) {
            spiBug(fds, clazz, object, isArray);
        }
    }

    private void spiBug(List<Field> fds, Class clazz, Object data, boolean isArray) {
        //??????get/set??????
        List<TransformMethod> methodList = Lists.newArrayList();
        getMethods(fds, methodList, clazz);
        if (!methodList.isEmpty()) {
            //??????
            dataObjectHandler(data, methodList, isArray);
        }

        //????????????VO??????VO???List<VO>
        fds.forEach(f -> {
            EnableTransform enTr = f.getAnnotation(EnableTransform.class);
            if (enTr == null) {
                return;
            }
            Class<?> subClazz = f.getType();
            if (List.class.getName().equals(subClazz.getTypeName())) {
                try {
                    PropertyDescriptor subDescriptor = new PropertyDescriptor(f.getName(), clazz);

                    if (data instanceof List) {
                        for (Object vo : (List) data) {
                            Object subObject = subDescriptor.getReadMethod().invoke(vo);
                            Type gType = f.getGenericType();
                            if (gType != null && gType instanceof ParameterizedType) {
                                ParameterizedType pt = (ParameterizedType) gType;
                                subClazz = (Class<?>) pt.getActualTypeArguments()[0];
                            }
                            beforeWriternal(subObject, subClazz, true);
                        }
                    } else {
                        Object subObject = subDescriptor.getReadMethod().invoke(data);
                        Type gType = f.getGenericType();
                        if (gType != null && gType instanceof ParameterizedType) {
                            ParameterizedType pt = (ParameterizedType) gType;
                            subClazz = (Class<?>) pt.getActualTypeArguments()[0];
                        }
                        beforeWriternal(subObject, subClazz, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    PropertyDescriptor subDescriptor = new PropertyDescriptor(f.getName(), clazz);
                    Object subObject = subDescriptor.getReadMethod().invoke(data);
                    beforeWriternal(subObject, subClazz, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void dataObjectHandler(Object data, List<TransformMethod> methodList, boolean isArray) {
        if (isArray) {
            List<Object> list = (List<Object>) data;
            list.forEach(obj -> {
                dataSingleHandler(obj, methodList);
            });
        } else {
            dataSingleHandler(data, methodList);
        }
    }

    private void dataSingleHandler(Object obj, List<TransformMethod> methodList) {
        methodList.forEach(me -> {
            try {
                if (me.getReadMethod().invoke(obj) != null) {
                    String sourceValue = String.valueOf(me.getReadMethod().invoke(obj));
                    String writeValue = sourceValue;
                    if (StringUtils.isNotBlank(me.getCodeCls())) {
//                        writeValue= StCodeUtil.getCodeName(me.getCodeCls(),writeValue);
                        // TODO ?????????????????????
                        writeValue = "ttttt";
                    }
                    me.getWriteMethod().invoke(obj, StringUtils.isBlank(writeValue) ? sourceValue : writeValue);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getMethods(List<Field> fds, List<TransformMethod> methodList, Class clazz) {
        fds.forEach(k -> {
            try {
                TransfCode code = k.getAnnotation(TransfCode.class);
                TransfUser user = k.getAnnotation(TransfUser.class);
                String sourceName = "";
                String codeCls = "";
                Set<String> annoSet = new HashSet<>();

                if (user != null) {
                    sourceName = user.valueFrom();
                    annoSet.add(TransfUser.class.getName());
                }
                if (code != null) {
                    sourceName = code.valueFrom();
                    codeCls = code.codeType();
                    annoSet.add(TransfCode.class.getName());
                }
                String targetName = k.getName();
                if (StringUtils.isNotBlank(sourceName) && StringUtils.isNotBlank(targetName)) {
                    PropertyDescriptor sourceDescriptor = new PropertyDescriptor(sourceName, clazz);
                    PropertyDescriptor targetDescriptor = new PropertyDescriptor(targetName, clazz);
                    Method writeMethod = targetDescriptor.getWriteMethod();
                    Method readMethod = sourceDescriptor.getReadMethod();
                    TransformMethod tm = new TransformMethod(readMethod, writeMethod, codeCls, annoSet);
                    methodList.add(tm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


}
