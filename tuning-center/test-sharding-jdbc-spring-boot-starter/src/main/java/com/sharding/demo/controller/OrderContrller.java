package com.sharding.demo.controller;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.sharding.demo.dao.CompanyMapper;
import com.sharding.demo.dao.OrderMapper;
import com.sharding.demo.dao.UserInfoMapper;
import com.sharding.demo.model.Company;
import com.sharding.demo.model.Order;
import com.sharding.demo.model.UserInfo;

@RestController
public class OrderContrller {
	private ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
	@Autowired
	private UserInfoMapper userInfoMapper;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private OrderMapper orderMapper;

	@GetMapping("/saveUser")
    public String saveUser() {
		String companyId = "2000";
        userInfoMapper.save(createUser(companyId,0));
        return "ok";
    }

	@GetMapping("/saveOrder")
    public String saveOrder(){
        Order order = Order.builder().orderName("122").createTime(new Date()).build();
        orderMapper.save(order);
        return "ok";
    }

	@GetMapping("/batchSaveOrder")
    public String batchSaveOrder() throws ParseException {
        Date now = new Date();
        Date nextDate = DateUtils.parseDate("2020", "YYYY");
        List<CompletableFuture<Void>> futures = IntStream.range(0, 5).mapToObj(j->CompletableFuture.runAsync(()->{
                    List<Order> list = IntStream.range(0, 20)
                            .mapToObj(i ->Order.builder().orderName("Order."+i).createTime(nextDate).build())
                            .collect(Collectors.toList());
                    orderMapper.batchSave(list);
                }, service)
        ).collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        return "ok";
    }

	@GetMapping("/listUser")
    public List<Map> listUser(){
        BigInteger companyId = BigInteger.valueOf(2000);
        Map map = Maps.newHashMap();
        map.put("companyId", companyId);

        return userInfoMapper.findAll(map);
    }
	 
	@GetMapping("/listCompany")
    public List<Map> listCompany(){
       
		Map map = Maps.newHashMap();
        return companyMapper.findAll(map);
    }
	
	 


	@GetMapping("/batchSaveUser")
    public String batchSaveUser() {
        String companyId = "2000";
        List<CompletableFuture<Void>> futures = IntStream.range(0, 5).mapToObj(j->CompletableFuture.runAsync(()->{
                List<UserInfo> list = IntStream.range(0, 20)
                        .mapToObj(i -> createUser(companyId, i))
                        .collect(Collectors.toList());
                userInfoMapper.batchSave(list);
            }, service)
        ).collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        return "ok";
    }

	@GetMapping("/saveCompany")
    public String saveCompany(){
        Company company = Company.builder()
                .companyId(RandomStringUtils.randomNumeric(4))
                .companyName("33")
                .address("222")
                .createTime(new Date())
                .build();

        companyMapper.save(company);
        return "ok";
    }

    private UserInfo createUser(String companyId, int index){
        return UserInfo.builder()
                .account("Account."+index)
                .companyId(companyId)
                .password(RandomStringUtils.randomAlphabetic(8))
                .userName("Name."+index)
                .build();
    }
}
