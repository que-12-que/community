//package com.nowcoder.community.config;
//
//import com.nowcoder.community.quartz.AlphaJob;
//import com.nowcoder.community.service.AlphaService;
//import org.quartz.JobDataMap;
//import org.quartz.JobDetail;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.quartz.JobDetailFactoryBean;
//import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
//
//@Configuration
//public class QuartzConfig {
//
//    // 配置JobDetail
//    @Bean
//    public JobDetailFactoryBean alphaJobDetail() {
//        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
//        factoryBean.setJobClass(AlphaJob.class);
//        factoryBean.setName("alphaJob");
//        factoryBean.setGroup("alphaJobGroup");
//        factoryBean.setDurability(true);
//        factoryBean.setRequestsRecovery(true);
//        return factoryBean;
//    }
//
//    // 配置Trigger
//    @Bean
//    public SimpleTriggerFactoryBean alpahTrigger(JobDetail alphaJobDetail) {
//        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
//        factoryBean.setJobDetail(alphaJobDetail);
//        factoryBean.setName("alphaTrigger");
//        factoryBean.setGroup("alphaTriggerGroup");
//        factoryBean.setRepeatInterval(3000);
//        factoryBean.setJobDataMap(new JobDataMap());
//        return factoryBean;
//    }
//
//}
