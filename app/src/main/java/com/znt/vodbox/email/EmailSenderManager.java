package com.znt.vodbox.email;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/** 
 * @ClassName: EmailSenderManager 
 * @Description: TODO
 * @author yan.yu 
 * @date 2015-4-20 娑撳宕�2:37:28  
 */
public class EmailSenderManager
{

	
	
	public void sendEmail(final String title, final String emailContent, final String[] receiver, final File file)
	{
        new Thread(new Runnable() 
        {
    		@Override
    		public void run()
    		{
    			try {
    				

    				EmailSender sender = new EmailSender();
    				sender.setProperties("smtp.sina.com", "25");
    				sender.setMessage("yuyan19850204@sina.com", title, emailContent);
    				if(file != null && file.exists())
    					sender.addAttachment(file.getAbsolutePath());
    				sender.setReceiver(receiver);
    				sender.sendEmail("smtp.sina.com", "yuyan19850204@sina.com", "DianYin1818");
    				
    			} catch (AddressException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (MessagingException e) {
    				// TODO Auto-generated catch blockf
    				e.printStackTrace();
    			}
    		}
    	}).start();
	}
}
 
