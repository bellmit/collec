package com.unicom.roleRightShiro.utils;


import com.wf.captcha.ArithmeticCaptcha;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class UnicomArithmeticCaptcha extends ArithmeticCaptcha {
    public UnicomArithmeticCaptcha() {
    }

    public UnicomArithmeticCaptcha(int width, int height) {
        super(width, height);

    }

    public UnicomArithmeticCaptcha(int width, int height, int len) {
        super(width, height, len);
    }


    @Override
    /**
     * 生成随机验证码
     *
     * @return 验证码字符数组
     */
    protected char[] alphas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(num(1, 80));
            if (i < len - 1) {
                int type = num(1, 3);
                if (type == 1) {
                    sb.append("+");
                } else if (type == 2) {
                    sb.append("-");
                }
            }
        }
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        try {
            chars = String.valueOf(engine.eval(sb.toString().replaceAll("x", "*")));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        sb.append("=?");
        setArithmeticString(sb.toString());
        return chars.toCharArray();
    }


}
