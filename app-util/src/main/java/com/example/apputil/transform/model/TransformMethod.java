package com.example.apputil.transform.model;

import java.lang.reflect.Method;
import java.util.Set;

public class TransformMethod {
    private Method readMethod;
    private Method writeMethod;
    private String codeCls;//标准代码
    private Set<String> annotationType;//注解类型

    public TransformMethod(Method readMethod, Method writeMethod) {
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
    }

    public TransformMethod(Method readMethod, Method writeMethod, String codeCls, Set<String> annotationType) {
        this.readMethod = readMethod;
        this.writeMethod = writeMethod;
        this.codeCls = codeCls;
        this.annotationType = annotationType;
    }

    public Method getReadMethod() {
        return readMethod;
    }

    public void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
    }

    public Method getWriteMethod() {
        return writeMethod;
    }

    public void setWriteMethod(Method writeMethod) {
        this.writeMethod = writeMethod;
    }

    public String getCodeCls() {
        return codeCls;
    }

    public void setCodeCls(String codeCls) {
        this.codeCls = codeCls;
    }

    public Set<String> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Set<String> annotationType) {
        this.annotationType = annotationType;
    }
}
