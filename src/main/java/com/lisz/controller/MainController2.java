package com.lisz.controller;

import com.lisz.service.HealthIndicatorService;
import com.netflix.appinfo.InstanceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@RestController
public class MainController2 {
	/*
		默认这个LB会是RR的策略，因为有这么个启动配置：
		@Bean
		@ConditionalOnMissingBean
		public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
				LoadBalancerClientFactory loadBalancerClientFactory) {
			String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
			return new RoundRobinLoadBalancer(
					loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
		}
	 */
	@Autowired
	private LoadBalancerClient lb;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private RestTemplate restTemplate;

	private Random random = new Random();

	@GetMapping("/client6")
	public Object client6() {
		final ServiceInstance instance = lb.choose("provider");
		String url = String.format("http://%s:%s/getHi", instance.getHost(), instance.getPort());
		final ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
		return entity.toString();
	}

	/*
		手动LB：随机
	 */
	@GetMapping("/client7")
	public String client7() {
		final List<ServiceInstance> instances = discoveryClient.getInstances("provider");
		final ServiceInstance instance = instances.get(random.nextInt(instances.size()));
//		instance.getMetadata(); 这里可以设置/取出权重，然后自定义带权重的RR
		//注意⚠️要想成功call client7这个API，配置方法getRestTemplate上面必须没有@LoadBalanced才可以
		String url = String.format("http://%s:%s/getHi", instance.getHost(), instance.getPort());
		final ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
		return entity.getBody();
	}

	@GetMapping("/client8")
	public String client8() {
		// restTemplate生成URL
		String url = "http://provider/getHi";
		// 配置方法getRestTemplate上面要加上@LoadBalanced才可以, 默认RR策略
		final ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
		return entity.getBody();
	}
}
