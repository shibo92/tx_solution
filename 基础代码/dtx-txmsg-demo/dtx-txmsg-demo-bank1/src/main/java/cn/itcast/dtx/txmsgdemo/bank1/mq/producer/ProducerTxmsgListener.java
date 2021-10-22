package cn.itcast.dtx.txmsgdemo.bank1.mq.producer;

import cn.itcast.dtx.txmsgdemo.bank1.dao.AccountInfoDao;
import cn.itcast.dtx.txmsgdemo.bank1.model.AccountChangeEvent;
import cn.itcast.dtx.txmsgdemo.bank1.service.AccountInfoService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RocketMQTransactionListener(txProducerGroup = "producer_group_txmsg_bank1")
public class ProducerTxmsgListener implements RocketMQLocalTransactionListener {
    @Autowired
    AccountInfoService accountInfoService;
    @Autowired
    AccountInfoDao accountInfoDao;

    // 监听消息发送，执行本地事务
    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {
        //解析消息内容
        try {
            String jsonString = new String((byte[]) message.getPayload());
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            AccountChangeEvent accountChangeEvent = JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
            //扣除金额
            accountInfoService.doUpdateAccountBalance(accountChangeEvent);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("executeLocalTransaction 事务执行失败", e);
            e.printStackTrace();
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    //此方法检查事务执行状态
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        RocketMQLocalTransactionState state;
        final JSONObject jsonObject = JSON.parseObject(new String((byte[]) message.getPayload()));
        AccountChangeEvent accountChangeEvent = JSONObject.parseObject(jsonObject.getString("accountChange"), AccountChangeEvent.class);
        //事务id
        String txNo = accountChangeEvent.getTxNo();
        int isexistTx = accountInfoDao.isExistTx(txNo);
        log.info("回查事务，事务号: {} 结果: {}", accountChangeEvent.getTxNo(), isexistTx);
        if (isexistTx > 0) {
            state = RocketMQLocalTransactionState.COMMIT;
        } else {
            state = RocketMQLocalTransactionState.UNKNOWN;
        }
        return state;
    }
}