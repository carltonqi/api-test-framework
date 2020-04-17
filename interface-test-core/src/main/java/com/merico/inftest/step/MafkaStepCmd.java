package com.merico.inftest.step;

import com.merico.inftest.cases.CaseInfo;
import com.merico.inftest.context.Context;
import com.merico.inftest.log4atc.AtcLogUtils;
import com.merico.inftest.response.Response;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * @create
 */
/*
public class MafkaStepCmd extends StepCmd{
    private static ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

    private IProducerProcessor producer;

    private MafkaInfo mafkaInfo;

    public MafkaStepCmd(){}

    public MafkaStepCmd(MafkaInfo mafkaInfo){
        this.mafkaInfo = mafkaInfo;
    }

    @Override
    public Response doExcute(CaseInfo CaseInfo, Response preResponse, Context context) throws Throwable{
        String mafkaBGNamespace = mafkaInfo.getMafkaBGNamespace();
        String mafkaClientAppkey = mafkaInfo.getMafkaClientAppkey();
        String mafkaTopic = mafkaInfo.getMafkaTopic();
        String mafkaMsgBody = mafkaInfo.getMafkaMsgBody();

        producer = (IProducerProcessor)concurrentHashMap.get(mafkaBGNamespace+mafkaClientAppkey+mafkaTopic);

        //创建topic对应的producer对象(注意每次build调用会产生一个新的实例)
        //请注意:若调用MafkaClient.buildProduceFactory()创建实例抛出有异常，请重点关注并排查异常原因，不可频繁调用该方法给服务端带来压力
        try{
            if(producer == null){
                Properties properties = new Properties();
                //设置业务所在BG的namespace
                properties.setProperty(ConsumerConstants.MafkaBGNamespace,mafkaBGNamespace);
                //设置生产者appkey
                properties.setProperty(ConsumerConstants.MafkaClientAppkey,mafkaClientAppkey);
                producer = MafkaClient.buildProduceFactory(properties,mafkaTopic);
                concurrentHashMap.put(mafkaBGNamespace+mafkaClientAppkey+mafkaTopic,producer);
            }
            //同步发送，注意:producer只实例化一次，不要每次调用sendMessage方法前都创建producer实例
            ProducerResult result = producer.sendMessage(mafkaMsgBody);
            logger.info("send message status:{}",result.getProducerStatus());
        }catch(Exception e){
            logger.error("step execte faild! step name {}", getClass().getName());
            AtcLogUtils.printLog(String.format("%s execte faild！%s", this.getClass().getName(), e.getMessage()));

            throw e;
        }
        return new Response("{\"code\":200,\"msg\":\"mafka消息发送成功\",\"data\":{}}");
    }

    public MafkaInfo getMafkaInfo() {
        return mafkaInfo;
    }

    public void setMafkaInfo(MafkaInfo mafkaInfo) {
        this.mafkaInfo = mafkaInfo;
    }
}
*/
