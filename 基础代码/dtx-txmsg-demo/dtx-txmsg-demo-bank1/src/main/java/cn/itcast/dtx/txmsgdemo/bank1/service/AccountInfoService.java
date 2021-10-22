package cn.itcast.dtx.txmsgdemo.bank1.service;

import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by Administrator on 2021/10/14.
 */
public interface AccountInfoService {
    void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent);

    @Transactional
    void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent);
}
