package com.casemgr.service.impl;

import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class MailServiceImpl {
//	@Autowired
//    private JavaMailSender mailSender;
//	
//	@Value("${mail-from-user}")
//	private String from;
	private final Resend resend = new Resend("re_KcAU7Y9Y_N7LPuVgB92Wt2WtsBNS3oD2g");

    public void sendEmail(String to, String subject, String body) throws ResendException {
    	CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <noreply@hayaku.jp>")
                .to(to)
                .subject(subject)
                .text(body)
                .build();
    	resend.emails().send(params);
//    	String body = generateEmailTemplate(bodyTemplate, variables);
//        String subject = generateEmailTemplate(subjectTemplate, variables);
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//        
//        helper.setFrom("NPI System<"+from+">");
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(body, true);
//        
//        mailSender.send(message);
    }

//    private String generateEmailTemplate(String template, Map<String, String> variables) {
//        String body = template;
//        for (Map.Entry<String, String> entry : variables.entrySet()) {
//        	String key = entry.getKey();
//            String value = entry.getValue();
//            body = body.replace("$$" + key + "$$", value);
//        }
//        return body;
//    }
}
