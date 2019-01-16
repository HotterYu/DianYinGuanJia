package com.znt.vodbox.utils;

import android.text.TextUtils;

import com.znt.vodbox.model.Shopinfo;

import java.util.Comparator;

public class DeviceComparator implements Comparator<Shopinfo>
{  
    public int compare(Shopinfo file1, Shopinfo file2)
    {  
    	if(file1.getTmlRunStatus() == null
                || file1.getTmlRunStatus().size() == 0
                || file2.getTmlRunStatus() == null
                || file2.getTmlRunStatus().size() == 0
                || file1.getTmlRunStatus().get(0) == null
                || file2.getTmlRunStatus().get(0) == null)
        {
            return -1;
        }
    	String devT1 = file1.getTmlRunStatus().get(0).getLastBootTime();
    	String devT2 = file2.getTmlRunStatus().get(0).getLastBootTime();
    	
    	long lT1 = 0;
    	long lT2 = 0;
    	if(!TextUtils.isEmpty(devT1))
    		lT1 = Long.parseLong(devT1);
    	if(!TextUtils.isEmpty(devT2))
    		lT2 = Long.parseLong(devT2);
    	
        if(lT1 > lT2)  
        {  
            return -1;  
        }else  
        {  
            return 1;  
        }  
    }  
}  
