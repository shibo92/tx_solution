package cn.itcast.dtx.txmsgdemo.bank2.service;

import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by Administrator on 2021/10/14.
 */
public interface AccountInfoService {
    @Transactional
    void addAccountInfoBalance(AccountChangeEvent accountChangeEvent);
}
