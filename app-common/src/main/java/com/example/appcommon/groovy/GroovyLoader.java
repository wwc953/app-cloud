package com.example.appcommon.groovy;

import com.example.appcommon.commondata.CustomSqlProviderServer;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyLoader {
    private static ScriptEngineManager factory = new ScriptEngineManager();
    private static ScriptEngine engine = factory.getEngineByName("groovy");
    private static Map<String, CompiledScript> scriptcache = new ConcurrentHashMap<>();

    public static Object parseWithBinding(String scriptText, Map<String, Object> params) throws ScriptException {
        CompiledScript script = scriptcache.get(scriptText);
        if (script == null) {
            synchronized (scriptcache) {
                scriptcache.put(scriptText, ((Compilable) engine).compile(scriptText));
            }
            script = scriptcache.get(scriptText);
        }
        Bindings binding = engine.createBindings();
        params.forEach((k, v) -> {
            binding.put(k, v);
        });
        return script.eval(binding);
    }

    public static void main(String[] args) {
        Map<String, Object> param = new HashMap<>();
        param.put("mgtOrgCode", "123");
        String beforesql = "select * from table t where t.bb is nou null <if test=\"mgtOrgCode!=null && mgtOrgCode!=''\"> and t.mgtOrgCode like #{mgtOrgCode}||'%' </if> <if test=\"aa!=null && aa!=''\"> and t.aa=#{aa} </if>";
        String apiParamJson = "{\"mgtOrgCode\":\"\",\"aa\":\"\"}";

        CustomSqlProviderServer sqlUtil = new CustomSqlProviderServer();
        sqlUtil.getLabelSql(param, beforesql, apiParamJson);
    }



}
