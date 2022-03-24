package com.example.appcommon.groovy;

import javax.script.*;
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


}
