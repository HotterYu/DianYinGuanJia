package com.znt.vodbox.email;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender 
{
	private Properties properties;
	private Session session;
	private Message message;
	private MimeMultipart multipart;

	public EmailSender() {
		super();
		this.properties = new Properties();
	}
	public void setProperties(String host,String post){
		//閿熸枻鎷峰潃
		this.properties.put("mail.smtp.host",host);
		//閿熷壙鍙ｇ尨鎷�
		this.properties.put("mail.smtp.post",post);
		//閿熻鍑ゆ嫹閿熸枻鎷疯瘉
		this.properties.put("mail.smtp.auth",true);
		this.session= Session.getInstance(properties);
		this.message = new MimeMessage(session);
		this.multipart = new MimeMultipart("mixed");
	}
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熺Ц纭锋嫹閿熸枻鎷�
	 * @param receiver
	 * @throws MessagingException
	 */
	public void setReceiver(String[] receiver) throws MessagingException {
		Address[] address = new InternetAddress[receiver.length];
		for(int i=0;i<receiver.length;i++){
			address[i] = new InternetAddress(receiver[i]);
		}
		this.message.setRecipients(Message.RecipientType.TO, address);
	}
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熺粸纭锋嫹
	 * @param from 閿熸枻鎷锋簮
	 * @param title 閿熸枻鎷烽敓鏂ゆ嫹
	 * @param content 閿熸枻鎷烽敓鏂ゆ嫹
	 * @throws AddressException
	 * @throws MessagingException
	 */
	public void setMessage(String from,String title,String content) throws AddressException, MessagingException {
		this.message.setFrom(new InternetAddress(from));
		this.message.setSubject(title);
		//閿熸枻鎷烽敓渚ユ唻鎷烽敓渚ヤ紮鎷烽敓鏂ゆ嫹setText()閿熸枻鎷烽敓鍙綇鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鍙潻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹绀洪敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
		MimeBodyPart textBody = new MimeBodyPart();
		textBody.setContent(content,"text/html;charset=GB2312");
		this.multipart.addBodyPart(textBody);
	}
	/**
	 * 閿熸枻鎷风棃閿熸枻鎷烽敓锟�
	 * @param filePath 閿熶茎纭锋嫹璺敓鏂ゆ嫹
	 * @throws MessagingException
	 */
	public void addAttachment(String filePath) throws MessagingException {
		FileDataSource fileDataSource = new FileDataSource(new File(filePath));
		DataHandler dataHandler = new DataHandler(fileDataSource);
		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(dataHandler);
		String attachName = fileDataSource.getName();
		/*try {
			attachName = URLEncoder.encode(attachName, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/  
		mimeBodyPart.setFileName("DianYinExport.xls");
		this.multipart.addBodyPart(mimeBodyPart);
	}
	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熺粸纭锋嫹
	 * @param host 閿熸枻鎷峰潃
	 * @param account 閿熷壙浼欐嫹閿熸枻鎷�
	 * @param pwd 閿熸枻鎷烽敓鏂ゆ嫹
	 * @throws MessagingException
	 */
	public void sendEmail(String host,String account,String pwd) throws MessagingException {
		//閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹
		this.message.setSentDate(new Date());
		//閿熸枻鎷烽敓閰电鎷烽敓鏂ゆ嫹閿熸嵎锝忔嫹閿熶茎鎲嬫嫹閿熼叺闈╂嫹閿熸枻鎷�
		this.message.setContent(this.multipart);
		this.message.saveChanges();
		
		//閿熸枻鎷烽敓鏂ゆ嫹閿熺粸纭锋嫹閿熸枻鎷烽敓閰佃鎷烽敓瑗燂紝璇ф嫹鎸囬敓鏂ゆ嫹閿熸枻鎷蜂娇閿熸枻鎷稴MTP鍗忛敓浠嬪彂閿熸枻鎷烽敓缁炵》鎷�  
		Transport transport=session.getTransport("smtp");
		//閿熸枻鎷峰綍閿熸枻鎷烽敓鏂ゆ嫹  
		transport.connect(host,account,pwd);  
		//閿熸枻鎷烽敓鏂ゆ嫹閿熺粸纭锋嫹
		transport.sendMessage(message, message.getAllRecipients());
		//閿熸埅鎲嬫嫹閿熸枻鎷烽敓鏂ゆ嫹
		transport.close();
	}
}