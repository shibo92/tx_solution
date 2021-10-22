package cn.itcast.dtx.notifydemo.bank1.feign;

import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import org.springframework.stereotype.Component;

/**
 * @author by Administrator on 2021/10/14.
 */
@Component
public class PayFallback implements PayClient {
    @Override
    public AccountPay queryPayResult(String txNo) {
        AccountPay accountPay = new AccountPay();
        accountPay.setResult("fail");
        return accountPay;
    }
}