
package com.znt.vodbox.activity; 

import android.app.Activity;

import java.util.Stack;


/** 
 * @ClassName: ActivityManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2014-6-13 ����11:17:20  
 */
public class MyActivityManager
{
	private static Stack activityStack;
    private static MyActivityManager instance;
    private MyActivityManager(){
         
    }
    public static MyActivityManager getScreenManager()
    {
        if(instance==null)
        {
            instance = new MyActivityManager();
        }
        return instance;
    }
    /**
    * @Description: �˳�ջ��
    * @param @param activity   
    * @return void 
    * @throws
     */
    public void popActivity(Activity activity)
    {
        if(activity != null)
        {
            activity.finish();
            if(activityStack != null)
            	activityStack.remove(activity);
            activity=null;
        }
    }
    /**
    * @Description: ��õ�ǰջ��
    * @param @return   
    * @return Activity 
    * @throws
     */
    public Activity currentActivity()
    {
        Activity activity = null;
        if(!activityStack.empty())
        {
            activity = (Activity)activityStack.lastElement();
            Activity a = (Activity)activityStack.firstElement();

            
        }
        return activity;
    }
    
    public Activity firstActivity()
    {
    	Activity activity = null;
    	if(!activityStack.empty())
    	{
    		activity = (Activity)activityStack.firstElement();
    	}
    	return activity;
    }
    /**
    * @Description: ��ǰActivity����ջ��
    * @param @param activity   
    * @return void 
    * @throws
     */
    public void pushActivity(Activity activity)
    {
        if(activityStack == null)
        {
            activityStack = new Stack();
        }
        popActivity(activity.getClass());
        activityStack.add(activity);
    }
    /**
    * @Description: �˳�����Activity
    * @param @param cls   
    * @return void 
    * @throws
     */
    public void popAllActivity()
    {
    	if(activityStack == null)
    		return;
        int size = activityStack.size();
        for(int i=0;i<size;i++)
        {
        	Activity activity = currentActivity();
            if(activity != null)
            	popActivity(activity);
        }
    }
    public void popAllActivityExceptionOne(Class cls)
    {
    	int index = 0;
    	if(activityStack == null || activityStack.size() == 0)
    	    return;
    	while(activityStack.size() > 1)
    	{
    		Activity activity = (Activity) activityStack.get(index);
    		if(activity != null)
    		{

    			if(!activity.getClass().equals(cls))
    			{
    				popActivity(activity);
    				index = 0;
    			}
    			else
    				index ++;
    		}
    	}
    }
    public void popActivity(Class cls)
    {
    	for(int i=0;i<activityStack.size();i++)
    	{
    		Activity activity = (Activity)activityStack.get(i);
    		if(activity != null && activity.getClass().equals(cls))
    		{
    			popActivity(activity);
    			break;
    		}
    	}
    }
}
 
