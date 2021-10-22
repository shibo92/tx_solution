package cn.itcast.dtx.notifymsg.pay.service;

import cn.itcast.dtx.notifymsg.pay.entity.AccountPay;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by Administrator on 2021/10/14.
 */
public interface AccountPayService {
    AccountPay insertAccountPay(AccountPay accountPay);

    AccountPay getAccountPay(String txNo);
}
