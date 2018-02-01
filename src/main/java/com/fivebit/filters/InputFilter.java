package com.fivebit.filters;

import com.fivebit.common.Jlog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by fivebit on 2017/5/15.
 */

/**
 * 对输入数据进行过滤
 */
@Provider
public class InputFilter implements ContainerRequestFilter {
    private static Logger log = LoggerFactory.getLogger(InputFilter.class);
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        //这一步，作用是把Jlog的静态变量清空，生成一个新的UUID.如果不清空，则每次请求都一样。
        Jlog.requestid = "";
        String msg = containerRequestContext.getRequest().getMethod() + " "
                +containerRequestContext.getUriInfo().getAbsolutePath().getPath() + " request begin";
        log.info(msg);
    }
}
