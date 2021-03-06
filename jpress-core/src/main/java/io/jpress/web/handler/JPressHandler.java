/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.handler;


import com.jfinal.handler.Handler;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 伪静态处理器
 * @Package io.jpress.web.handler
 */

public class JPressHandler extends Handler implements JPressOptions.OptionChangeListener {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static String getCurrentTarget() {
        return threadLocal.get();
    }

    private static String suffix = "";

    public static void init() {
        suffix = JPressOptions.getAppUrlSuffix();
    }

    public JPressHandler() {
        JPressOptions.addListener(this);
    }


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {

        if (StrUtils.isBlank(suffix) && target.indexOf('.') != -1) {
            return;
        }

        //启用伪静态
        if (StrUtils.isNotBlank(suffix) && target.endsWith(suffix)) {
            target = target.substring(0, target.length() - suffix.length());
        }


        try {
            threadLocal.set(target);
            request.setAttribute("CPATH", request.getContextPath());
            next.handle(target, request, response, isHandled);
        } finally {
            threadLocal.remove();
        }
    }


    @Override
    public void onChanged(String key, String newValue, String oldValue) {

        if (JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE.equals(key)
                || JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX.equals(key)) {
            init();
        }
    }
}
