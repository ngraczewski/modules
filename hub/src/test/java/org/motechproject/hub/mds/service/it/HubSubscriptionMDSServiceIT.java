package org.motechproject.hub.mds.service.it;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.hub.mds.HubSubscription;
import org.motechproject.hub.mds.HubTopic;
import org.motechproject.hub.mds.service.HubSubscriptionMDSService;
import org.motechproject.hub.mds.service.HubTopicMDSService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HubSubscriptionMDSServiceIT extends BasePaxIT {

    @Inject
    private HubSubscriptionMDSService hubSubscriptionMDSService;
    @Inject
    private HubTopicMDSService hubTopicMDSService;
    
    private String callbackUrl = "http://callback/url";
    private String topicUrl = "http://topic/url";
    
    @Test
    public void testHubSubscriptionByCallbackUrl() {
        List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService.findSubByCallbackUrl(callbackUrl);
        Assert.assertNotNull(hubSubscriptions);
        Assert.assertEquals(0, hubSubscriptions.size());
        
        HubSubscription hubSubscription = new HubSubscription();
        hubSubscription.setCallbackUrl(callbackUrl);
        hubSubscription.setHubSubscriptionStatusId(3);
        hubSubscription.setHubTopicId(1);
        hubSubscriptionMDSService.create(hubSubscription);
        
        hubSubscriptions = hubSubscriptionMDSService.findSubByCallbackUrl(callbackUrl);
        Assert.assertNotNull(hubSubscriptions);
        Assert.assertEquals(1, hubSubscriptions.size());
        Assert.assertEquals(callbackUrl, hubSubscriptions.get(0).getCallbackUrl());
        Assert.assertEquals(1, (int) hubSubscriptions.get(0).getHubTopicId());
        
        hubSubscriptionMDSService.delete(hubSubscriptions.get(0));
        hubSubscriptions = hubSubscriptionMDSService.findSubByCallbackUrl(callbackUrl);
        Assert.assertNotNull(hubSubscriptions);
        Assert.assertEquals(0, hubSubscriptions.size());
    }
    
    @Test
    public void testHubSubscriptionByTopicAndCallbackUrl() {
        HubTopic hubTopic = new HubTopic();
        hubTopic.setTopicUrl(topicUrl);
        hubTopicMDSService.create(hubTopic);
        
        List<HubTopic> hubTopics = hubTopicMDSService.findByTopicUrl(topicUrl);
        Assert.assertNotNull(hubTopics);
        Assert.assertEquals(1, hubTopics.size());
        
        int topicId = (int) (long) hubTopicMDSService.getDetachedField(hubTopic, "id");
        
        List<HubSubscription> hubSubscriptions = hubSubscriptionMDSService.findSubByCallbackUrlAndTopicId(callbackUrl, topicId);
        Assert.assertNotNull(hubSubscriptions);
        Assert.assertEquals(0, hubSubscriptions.size());
        
        HubSubscription hubSubscription = new HubSubscription();
        hubSubscription.setCallbackUrl(callbackUrl);
        hubSubscription.setHubSubscriptionStatusId(3);
        hubSubscription.setHubTopicId(topicId);
        hubSubscriptionMDSService.create(hubSubscription);
        
        hubSubscriptions = hubSubscriptionMDSService.findSubByCallbackUrlAndTopicId(callbackUrl, topicId);
        Assert.assertNotNull(hubSubscriptions);
        Assert.assertEquals(1, hubSubscriptions.size());
        Assert.assertEquals(callbackUrl, hubSubscriptions.get(0).getCallbackUrl());
        Assert.assertEquals(3, (int) hubSubscriptions.get(0).getHubSubscriptionStatusId());
        Assert.assertEquals(topicId, (int) hubSubscriptions.get(0).getHubTopicId());
        
        hubSubscriptionMDSService.delete(hubSubscriptions.get(0));
        hubSubscriptions = hubSubscriptionMDSService.findSubByCallbackUrlAndTopicId(callbackUrl, topicId);
        Assert.assertNotNull(hubSubscriptions);
        Assert.assertEquals(0, hubSubscriptions.size());
        
        hubTopicMDSService.delete(hubTopic);
        hubTopics = hubTopicMDSService.findByTopicUrl(topicUrl);
        Assert.assertNotNull(hubTopics);
        Assert.assertEquals(0, hubTopics.size());
    }
}
