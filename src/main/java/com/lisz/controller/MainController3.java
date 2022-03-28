package com.lisz.controller;

import com.lisz.entity.Person;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.*;

@RestController
public class MainController3 {
	@Autowired
	private LoadBalancerClient lb;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/client9")
	public Map<String, String> client9() {
		// restTemplate生成URL
		String url = "http://provider/getMap";
		// 配置方法getRestTemplate上面要加上@LoadBalanced才可以, 默认RR策略
		// 这里好像getForEntity不太行，报错
		return restTemplate.getForObject(url, Map.class);
	}

	@GetMapping("/client10")
	public Person client10() {
		// restTemplate生成URL
		String url = "http://provider/getPerson";
		// 配置方法getRestTemplate上面要加上@LoadBalanced才可以, 默认RR策略
		// 这里好像getForEntity不太行，报错
		Person p = restTemplate.getForObject(url, Person.class);
		System.out.println(p);
		return p;
	}

	// http://localhost:91/client11/lisz
	@GetMapping("/client11/{name}")
	public Person client11(@PathVariable String name) {
		System.out.println("name " + name);
		// restTemplate生成URL
		String url = "http://provider/person/{1}"; // 下面的Person.class后面的第一个参数
		// 配置方法getRestTemplate上面要加上@LoadBalanced才可以, 默认RR策略
		// 这里好像getForEntity不太行，报错
		Person p = restTemplate.getForObject(url, Person.class, name);
		System.out.println(p);
		return p;
	}

	// Map 传参数
	@GetMapping("/client12")
	public Person client12(@RequestParam int id, @RequestParam String name) {
		String url = "http://provider/person?id={1}&name={2}";
		Person p = restTemplate.getForObject(url, Person.class, id, name);
		System.out.println(p);
		return p;
	}

	@GetMapping("/client13")
	public Person client13(@RequestParam int id, @RequestParam String name) {
		System.out.println("id " + id);
		System.out.println("name " + name);
		// restTemplate根据provider这个服务名生成URL
		// {}里面的要跟下面map里的各个key一致，这里Provider端的Controller分别接受两个entry也行，直接先拿到一个@RequestParam Map也可以
		String url = "http://provider/person2?id={id}&name={name}";
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", name);
		Person p = restTemplate.getForObject(url, Person.class, map);
		System.out.println(p);
		return p;
	}

	/*  Postman：
		http://localhost:91/client14
		{
            "id":1,
            "name": "lisz"
		}
	 */
	@GetMapping("/client14")
	public Person client14(@RequestBody Person person) {
		String url = "http://provider/person3";
		Person p = restTemplate.postForObject(url, person, Person.class); // provider端用@RequestBody Person person 接收这个person
		System.out.println(p);
		return p;
	}

	@GetMapping("/client15")
	public Object client15(HttpServletResponse response) throws Exception{
		String url = "http://provider/postLocation";
		final Map<String, String> map = Collections.singletonMap("name", "666");
		// RestTemplate也可以用exchange方法，这个方法可以自定义头信息
		final URI location = restTemplate.postForLocation(url, map, Person.class);  // 类似上面，provider端用@RequestBody Person person 接收这个map
		System.out.println(location);
		response.sendRedirect(location.toString());
		return location;
	}
}
