package com.ibm.microclimate.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.jobs.Job;

public class TestUtil {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy k:mm:ss");
	public static boolean isDebugEnabled = true;
	
    public static void print(String msg) {
        print(msg, null);
    }

    public static void print(String msg, Throwable t) {
        if (isDebugEnabled) {
            System.out.println(sdf.format(new Date()) + "  " + msg);
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }
    
    // Wait for a condition.  The timeout and interval are in seconds.
    public static boolean wait(Condition condition, long timeout, long interval) {
    	for (long time = 0; time < timeout; time += interval) {
    		if (condition.test()) {
    			return true;
    		}
    		try {
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				// Ignore
			}
    	}
    	return false;
    }
    
    public static void waitForJobs(long timeout, long interval) {
    	wait(new Condition() {
			public boolean test() {
				return Job.getJobManager().isIdle();
			}
		}, timeout, interval);
    }
    
//    public static void waitForJobs(long timeout, long interval) {
//		final IJobManager jobManager = Job.getJobManager();
//
//		boolean contLoop = true;
//
//		while (contLoop && System.nanoTime() < expireTimeInNanos) {
//			contLoop = false;
//
//			for (Job j : jobManager.find(null)) {
//				// Ignore system jobs, and some slow, unimportant jobs.
//				if (!j.isSystem() && !j.getName().contains("Updating Error Reports Database")
//						&& !j.getName().contains("Refreshing server adapter list") && !j.getName().startsWith("http")) {
//					contLoop = true;
//					break;
//				}
//			}
//
//			if (contLoop) {
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					// Ignore
//				}
//			}
//		} 
//    }
    
    
    public static void updateFile(String path, String originalString, String replaceString) throws Exception{
        File fileNeedsUpdate = new File(path);
        BufferedReader br = null;
        FileWriter writer = null;

		try {
		    br = new BufferedReader(new FileReader(fileNeedsUpdate));
		    String line;
		    StringBuilder stringbuilder = new StringBuilder();;
		
		    while ((line = br.readLine())!= null){
		        stringbuilder.append(line + System.lineSeparator());
		    }
		
		    String Content = stringbuilder.toString();
		    String newContent = Content.replaceAll(originalString, replaceString);
		    writer = new FileWriter(fileNeedsUpdate);
		
		    writer.write(newContent);
		} finally {
	        if(br != null)
	        	br.close();
	        if(writer != null)
	        	writer.close();
	    }
    }
    
}