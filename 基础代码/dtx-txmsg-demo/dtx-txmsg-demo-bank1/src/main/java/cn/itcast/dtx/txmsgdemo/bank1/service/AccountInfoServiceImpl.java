package cn.itcast.dtx.txmsgdemo.bank1.service;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author by Administrator on 2021/10/14.
 */
@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private AccountInfoDao accountInfoDao;

    /**
     * 更新帐号余额‐发送消息
     */
    @Override
    public void sendUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        //构建消息体
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountChange", accountChangeEvent);
        Message<String> message = MessageBuilder.withPayload(jsonObject.toJSONString()).build();
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction("producer_group_txmsg_bank1", "topic_txmsg", message, null);
        log.info("send transcation message body={},result= {}", message.getPayload(), sendResult.getSendStatus());
    }

    /**
     * 更新帐号余额‐本地事务,
     * producer发送消息完成后接收到MQ Server的回应即开始执行本地事务
     */
    @Transactional
    @Override
    public void doUpdateAccountBalance(AccountChangeEvent accountChangeEvent) {
        log.info("开始更新本地事务，事务号：{}", accountChangeEvent.getTxNo());
        accountInfoDao.updateAccountBalance(accountChangeEvent.getAccountNo(), -1 * accountChangeEvent.getAmount());
        //为幂等作准备
        accountInfoDao.addTx(accountChangeEvent.getTxNo());
        // 人为制造异常
        if (accountChangeEvent.getAmount() == 2) {
            throw new RuntimeException("bank1更新本地事务时抛出异常");
        }
        log.info("结束更新本地事务，事务号：{}", accountChangeEvent.getTxNo());
    }
}