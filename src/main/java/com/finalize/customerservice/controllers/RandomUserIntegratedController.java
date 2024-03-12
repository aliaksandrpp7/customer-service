package com.finalize.customerservice.controllers;

import by.micros.modelstore.model.Customer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/customer")
@Slf4j
public class RandomUserIntegratedController {

    @Autowired
    Environment env;

    OkHttpClient okHttpClient = new OkHttpClient();

    @GetMapping("/")
    public String getPort() {
        String response = "customer service running at port: " + env.getProperty("server.port");
        log.info(response);
        return response;
    }

    @GetMapping("/user")
    public Customer getCustomer() throws IOException {
        String person = getResponseFromServer("https://randomuser.me/api");
        return convertStringToStudent(person);
    }

    private String getResponseFromServer(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    private Customer convertStringToStudent(String person) {
        JSONObject fullJsonObject = new JSONObject(person)
                .getJSONArray("results")
                .getJSONObject(0);

        Customer customer = new Customer();
        customer.setGender(fullJsonObject.getString("gender").substring(0, 1));
        customer.setEmail(fullJsonObject.getString("email"));

        JSONObject dobJsonObject = fullJsonObject.getJSONObject("dob");
        ZonedDateTime zdt = ZonedDateTime.parse(dobJsonObject.getString("date"));
        customer.setDob(zdt.toLocalDate());

        JSONObject nameJsonObject = fullJsonObject.getJSONObject("name");
        customer.setFirstName(nameJsonObject.getString("first"));
        customer.setLastName(nameJsonObject.getString("last"));

        log.info(customer.toString());

        return customer;
    }
}
