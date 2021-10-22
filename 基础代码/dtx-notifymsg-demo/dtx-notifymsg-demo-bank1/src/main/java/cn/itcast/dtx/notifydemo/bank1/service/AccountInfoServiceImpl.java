package cn.itcast.dtx.notifydemo.bank1.service;

import cn.itcast.dtx.notifydemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.notifydemo.bank1.entity.AccountPay;
import cn.itcast.dtx.notifydemo.bank1.feign.PayClient;
import cn.itcast.dtx.notifydemo.bank1.model.AccountChangeEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
    @Autowired
    PayClient payClient;

    /**
     * 更新帐号余额,并发送消息
     */
    @Transactional
    @Override
    public void updateAccountBalance(AccountChangeEvent accountChange) {
        //幂等校验
        int existTx = accountInfoDao.isExistTx(accountChange.getTxNo());
        if (existTx > 0) {
            log.info("已处理消息：{}", JSONObject.toJSONString(accountChange));
            return;
        }
        //添加事务记录
        accountInfoDao.addTx(accountChange.getTxNo());
        //更新账户金额
        accountInfoDao.updateAccountBalance(accountChange.getAccountNo(), accountChange.getAmount());
    }

    /**
     * 主动查询充值结果
     **/
    @Override
    public AccountPay queryPayResult(String tx_no) {
        //主动请求充值系统查询充值结果
        AccountPay accountPay = payClient.queryPayResult(tx_no);
        //充值结果
        String result = accountPay.getResult();
        log.info("主动查询充值结果：{}", JSON.toJSONString(accountPay));
        if ("success".equals(result)) {
            AccountChangeEvent accountChangeEvent = new AccountChangeEvent();
            accountChangeEvent.setAccountNo(accountPay.getAccountNo());
            accountChangeEvent.setAmount(accountPay.getPayAmount());
            accountChangeEvent.setTxNo(accountPay.getId());
            updateAccountBalance(accountChangeEvent);
        }
        return accountPay;
    }
}