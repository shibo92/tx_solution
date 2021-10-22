package cn.itcast.dtx.txmsgdemo.bank2.service;

import cn.itcast.dtx.txmsgdemo.bank2.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank2.model.AccountChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author by Administrator on 2021/10/14.
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {
    @Autowired
    AccountInfoDao accountInfoDao;

    /**
     * 消费消息，更新本地事务，添加金额
     */
    @Transactional
    @Override
    public void addAccountInfoBalance(AccountChangeEvent accountChangeEvent) {
        log.info("bank2更新本地账号，账号：{},金额： {}", accountChangeEvent.getAccountNo(), accountChangeEvent.getAmount());
        //幂等校验
        int existTx = accountInfoDao.isExistTx(accountChangeEvent.getTxNo());
        if (existTx <= 0) {
            //执行更新
            accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(), accountChangeEvent.getAmount());
            //添加事务记录
            accountInfoDao.addTx(accountChangeEvent.getTxNo());
            // 异常情况
            if(accountChangeEvent.getAmount() == 4){
                throw new RuntimeException("bank2更新本地事务时抛出异常");
            }
            log.info("更新本地事务执行成功，本次事务号: {}", accountChangeEvent.getTxNo());
        } else {
            log.info("更新本地事务执行失败，本次事务号: {}", accountChangeEvent.getTxNo());
        }
    }
}