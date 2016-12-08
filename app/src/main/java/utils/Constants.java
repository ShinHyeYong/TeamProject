package utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by HY on 2016-11-09.
 */
public class Constants {

    public static final String FRAGMENT_KEY = "fragment_key";

    public static final int DRAWER_1_PAGE = 0;
    public static final int DRAWER_2_PAGE = 1;
    public static final int DRAWER_3_PAGE = 2;

    //스레드 병렬로 실행하기 위한 스레드 풀
    static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    public static final ThreadPoolExecutor tpexecutor = new ThreadPoolExecutor(
            NUMBER_OF_CORES * 3, NUMBER_OF_CORES * 3 + 2, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
}
