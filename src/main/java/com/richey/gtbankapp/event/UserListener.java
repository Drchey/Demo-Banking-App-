package com.richey.gtbankapp.event;

import com.richey.gtbankapp.dto.mail.UserRegisteredEvent;
import com.richey.gtbankapp.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserListener {
    private final MailService mailService;


    @EventListener
    public void handleUserLogin(UserRegisteredEvent event){
        String html = "<h1>Thanks for Signing Up <h1>";
        try{
            mailService.sendHtml(event.email(), "Welcome to Gtbank App", html);
        }catch (MessagingException e){

        }
    }
}
