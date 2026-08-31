package com.project.staynest.auth.email.adapter;

import com.project.staynest.auth.email.models.SendOtpByEmailEvent;
import com.project.staynest.auth.email.port.SendOtpByEmailPort;
import com.project.staynest.auth.validation.Validation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMqSendOtpByEmailAdapter implements SendOtpByEmailPort {

    private final RabbitTemplate rabbitmqTemplate;
    public RabbitMqSendOtpByEmailAdapter(RabbitTemplate rabbitmqTemplate){
        this.rabbitmqTemplate = rabbitmqTemplate;
    }

    @Override
    public void sendOtpByEmail(SendOtpByEmailEvent otpEvent) {
        Validation.validate(otpEvent, "otpEvent", this.getClass().getSimpleName());
        rabbitmqTemplate.convertAndSend(
                "notification.exchange",
                "mail.otp",
                otpEvent,
                message -> {

                    var props =
                            message.getMessageProperties();
                    props.setExpiration("300000");

                    props.setHeader(
                            "requestId",
                            UUID.randomUUID().toString()
                    );

                    props.setHeader(
                            "sourceService",
                            "auth"
                    );

                    return message;
                }
        );
    }
}
