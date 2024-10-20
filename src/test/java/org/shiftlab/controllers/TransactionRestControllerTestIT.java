package org.shiftlab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.shiftlab.Main;
import org.shiftlab.controllers.payload.NewTransactionPayload;
import org.shiftlab.dto.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class TransactionRestControllerTestIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    final String url = "/transaction";


    @Test
    @DisplayName("Get all transactions - Should return empty list when transactions dont exist")
    void getAllTransactions_TransactionNotExists_ReturnEmptyList() throws Exception {
        //given
        var requestBuilder = get(url);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("[]")
        );
    }

    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get all transactions - Should return list of transactions when transactions exist")
    void getAllTransactions_TransactionsExist_ReturnTransaction() throws Exception {
        //given
        var requestBuilder = get(url);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                
                        [
                    {
                        "id": 1,
                        "amount": 500.12,
                        "paymentType": "TRANSFER",
                        "transactionDate": "2024-10-22T14:30:00",
                        "sellerId": 1
                    },
                    {
                        "id": 2,
                        "amount": 100.50,
                        "paymentType": "CARD",
                        "transactionDate": "2024-10-22T15:00:00",
                        "sellerId": 1
                    },
                    {
                        "id": 3,
                        "amount": 325.51,
                        "paymentType": "TRANSFER",
                        "transactionDate": "2024-10-22T14:45:00",
                        "sellerId": 1
                    },
                    {
                        "id": 4,
                        "amount": 12.53,
                        "paymentType": "CARD",
                        "transactionDate": "2024-09-03T10:00:00",
                        "sellerId": 2
                    },
                    {
                        "id": 5,
                        "amount": 52.78,
                        "paymentType": "CASH",
                        "transactionDate": "2024-09-03T11:00:00",
                        "sellerId": 2
                    },
                    {
                        "id": 6,
                        "amount": 5.61,
                        "paymentType": "CASH",
                        "transactionDate": "2024-10-19T12:30:00",
                        "sellerId": 3
                    }
                ]"""));
    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get transaction by id - Should return transaction when transaction exist")
    void getTransactionById_TransactionExists_ReturnTransaction() throws Exception {
        //given
        int id = 1;
        var requestBuilder = get(url + "/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                    {
                        "id": 1,
                        "amount": 500.12,
                        "paymentType": "TRANSFER",
                        "transactionDate": "2024-10-22T14:30:00",
                        "sellerId" : 1
                    }
                    """));
    }
    @Test
    @DisplayName("Get transaction by id -Should return NOT FOUND when transaction doesnt exist")
    void getTransactionById_TransactionNotExists_ReturnNotFound() throws Exception {
        int id = 1;
        var requestBuilder = get(url + "/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @ParameterizedTest
    @MethodSource("validPayloadNewTransaction")
    @Sql("/sql/seller.sql")
    @DisplayName("Create transaction -Should return transaction when payload is valid")
    void createTransaction_PayloadValid_ReturnTransaction(NewTransactionPayload transaction) throws Exception {
        //given

        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction));
        //when
        var response = mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = objectMapper.readValue(response.getContentAsString(), TransactionDto.class);
        assertThat(content.getId()).isNotNull();
        assertThat(content.getTransactionDate()).isNotNull();
        assertThat(content.getAmount()).isEqualTo(transaction.amount());
        assertThat(content.getPaymentType().toString()).isEqualTo(transaction.paymentType());
    }
    @ParameterizedTest
    @MethodSource("invalidPayloadNewTransaction")
    @DisplayName("Create transaction - Should return bad request when payload is invalid")
    void createTransaction_PayloadInValid_ReturnBadRequest(NewTransactionPayload transaction) throws Exception {
        //given
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    @DisplayName("Create transaction - Should return not found when seller doesnt exist")
    void createTransaction_SellerNotExist_ReturnNotFound() throws Exception {
        //given
        var transaction = new NewTransactionPayload(1, BigDecimal.valueOf(12.5),"TRANSFER");

        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    static Stream<NewTransactionPayload> invalidPayloadNewTransaction() {
        return Stream.of(
                new NewTransactionPayload(1, BigDecimal.valueOf(-123.21),"TRANSFER"),
                new NewTransactionPayload(2,BigDecimal.valueOf(261),"DOLLAR"),
                new NewTransactionPayload(3,BigDecimal.valueOf(0), "CARD")
        );
    }
    static Stream<NewTransactionPayload> validPayloadNewTransaction() {
        return Stream.of(
                new NewTransactionPayload(1, BigDecimal.valueOf(50.5),"TRANSFER"),
                new NewTransactionPayload(2,BigDecimal.valueOf(25.0),"CASH"),
                new NewTransactionPayload(3,BigDecimal.valueOf(125.88), "CARD")
        );
    }
}
