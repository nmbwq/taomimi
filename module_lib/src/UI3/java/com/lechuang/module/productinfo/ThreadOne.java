package java.com.lechuang.module.productinfo;

import java.util.concurrent.Callable;

/**
 * Created by lianzun on 2018/12/28.
 */

public class ThreadOne implements Callable<String> {
    @Override
    public String call() throws Exception {
        Thread.sleep( 1000 );
        return null;
    }
}
