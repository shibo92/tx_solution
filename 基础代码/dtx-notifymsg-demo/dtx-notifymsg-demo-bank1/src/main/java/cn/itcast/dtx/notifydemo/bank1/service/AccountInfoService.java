package cn.itcast.dtx.notifydemo.bank1.service;

import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import cn.itcast.dtx.notifydemo.bank1.model.AccountChangeEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by Administrator on 2021/10/14.
 */
public interface AccountInfoService {
    @Transactional
    void updateAccountBalance(AccountChangeEvent accountChange);

    AccountPay queryPayResult(String tx_no);
}
