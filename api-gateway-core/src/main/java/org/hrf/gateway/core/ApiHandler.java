package org.hrf.gateway.core;

import com.google.gson.Gson;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class ApiHandler {

    private static String METHOD = "method";
    private static String PARAMS = "params";

    public ApiHandler() {
    }

    public void doHandler(HttpServletRequest req, HttpServletResponse resp) {

        String methodStr = req.getParameter(METHOD);
        String paramsStr = req.getParameter(PARAMS);

        if (StringUtils.isEmpty(methodStr)) {
            doResponse(resp, false, "����������Ϊ��");
            return;
        }
        Method method = null;
        try {

            method = ApiContext.getApiMethodByName(methodStr);
        } catch (Exception e) {
            doResponse(resp, false, "api������");
            return;
        }

        if (StringUtils.isEmpty(paramsStr)) {
            doResponse(resp, false, "��������Ϊ��");
            return;
        }
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = gson.fromJson(paramsStr, Map.class);
        try {
            LocalVariableTableParameterNameDiscoverer parameterUtils = new LocalVariableTableParameterNameDiscoverer();
            String[] parameterNames = parameterUtils.getParameterNames(method);
            Class<?>[] types = method.getParameterTypes();
            Object[] args = new Object[parameterNames.length];
            for (int index = 0; index < types.length; index++) {
                if (types[index].isAssignableFrom(HttpServletRequest.class)) {
                    args[index] = req;
                } else {
                    if (map.containsKey(parameterNames[index])) {
                        args[index] = gson.fromJson(map.get(parameterNames[index]).toString(), types[index]);
                    }
                }

            }
            Object invokeResult = method.invoke(ApiContext.getInvokerByApiName(methodStr), args);
            doResponse(resp, true, gson.toJson(invokeResult));
        } catch (Exception e) {
            doResponse(resp, false, "��������");
        }

    }

    private void doResponse(HttpServletResponse resp, boolean flag, String result) {
        try {
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json; charset=utf-8");
            ServletOutputStream os = resp.getOutputStream();
            if (flag) {
                os.write(new Gson().toJson(ResponseMessage.success(result)).getBytes());
            } else {
                os.write(new Gson().toJson(ResponseMessage.error(result)).getBytes());
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ResponseMessage {
        private String code;
        private String content;

        public static ResponseMessage success(String message) {
            ResponseMessage obj = new ResponseMessage();
            obj.setCode("200");
            obj.setContent(message);
            return obj;
        }

        public static ResponseMessage error(String message) {
            ResponseMessage obj = new ResponseMessage();
            obj.setCode("500");
            obj.setContent(message);
            return obj;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }

}
