package com.lisz.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@RestController
public class MainController {
	// 用的是Spring Cloud 的抽象层。下面的实现类未必就是Netflix的了。联系服务部署.
	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private LoadBalancerClient lb;  // BlockingLoadBalancerClient

	private Random rand = new Random();

	@GetMapping("/client")
	public String client(){
		final List<String> services = discoveryClient.getServices();
		return services.toString();
	}

	@GetMapping("/client2")
	public Object client2(){
		return discoveryClient.getInstances("provider"); // 上面 /client API的输出
	}

	@GetMapping("/client3")
	public Object client3(){
		final List<ServiceInstance> instances = discoveryClient.getInstances("provider");
		if (!instances.isEmpty()) {
			final EurekaServiceInstance instance = (EurekaServiceInstance) instances.get(rand.nextInt(instances.size()));
			if (instance.getInstanceInfo().getStatus() == InstanceInfo.InstanceStatus.UP) {
				String url = instance.getScheme() + "://" + instance.getHost() + ":" + instance.getPort() + "/getHi";
				System.out.println(url);
				final RestTemplate restTemplate = new RestTemplate();
//				final String res = restTemplate.getForObject(url, String.class);
				final ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);

				return "Provider returned: " + entity.getBody() + " <br /> status code: " + entity.getStatusCode();
			}
		}
		return "Default";
	}

	@GetMapping("/client5")
	public Object client5() {
		final ServiceInstance instance = lb.choose("provider");
		System.out.println("Port: " + instance.getPort());
		final RestTemplate restTemplate = new RestTemplate();
		System.out.println("URI: " + instance.getUri());
		String url = instance.getUri() + "/getHi";
		final ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
		return "Provider returned: " + entity.getBody() + " <br /> status code: " + entity.getStatusCode();
	}
}
