package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.error.ApiError;
import com.poseidoninc.poseidon.service.RequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Test
    public void methodArgumentNotValidExceptionTest() throws Exception {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, new MethodArgumentNotValidException(null, null));
        apiError.setTimestamp(LocalDateTime.now());

        when(requestService.requestToString(any())).thenReturn("request");

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/endpoint")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{ \"invalid\": \"data\" }");

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().string(apiError.toString()));
    }
}