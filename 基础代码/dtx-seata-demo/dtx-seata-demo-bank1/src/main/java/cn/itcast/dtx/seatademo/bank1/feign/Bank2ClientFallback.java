package cn.itcast.dtx.seatademo.bank1.feign;

import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version 1.0
 **/
@Component
public class Bank2ClientFallback implements Bank2Client {

    @Override
    public String transfer(Double amount) {

        return "fallback";
    }
}
