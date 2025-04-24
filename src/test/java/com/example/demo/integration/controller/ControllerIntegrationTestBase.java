package com.example.demo.integration.controller;

import com.example.demo.ExchangeRatePortalApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Map;

@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ExchangeRatePortalApplication.class
)
@RequiredArgsConstructor
abstract class ControllerIntegrationTestBase {

    @Autowired
    private MockMvc mvc;

    protected ResultActions get(String path, Map<String, String> queryParams) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(path)
                .contentType(MediaType.APPLICATION_JSON);

        if (queryParams != null) {
            queryParams.forEach(requestBuilder::param);
        }

        return mvc.perform(requestBuilder);
    }
}
