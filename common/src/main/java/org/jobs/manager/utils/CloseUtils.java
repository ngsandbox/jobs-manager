package org.jobs.manager.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloseUtils {
    public static void closeQuite(@NonNull Runnable runnable){
       try {
           runnable.run();
       }catch (Exception ex){
           log.trace("Error to close runnable ", ex);
       }
    }
}
