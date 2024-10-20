package org.shiftlab.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.shiftlab.Main;
import org.shiftlab.controllers.payload.NewSellerPayload;
import org.shiftlab.controllers.payload.UpdateSellerPayload;
import org.shiftlab.dto.SellerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@Testcontainers
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class SellerRestControllerTestIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    private Clock clock;

    final String url = "/seller";


    @BeforeEach
    void setUp() {
        Instant mockTime = Instant.parse("2024-10-20T12:30:00Z");
        when(clock.instant()).thenReturn(mockTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    }
    @Test
    @DisplayName("Get all sellers - Should return empty list when sellers dont exist")
    void getAllSellers_SellersNotExists_ReturnEmptyList() throws Exception {
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
    @Sql("/sql/seller.sql")
    @DisplayName("Get all sellers - Should return list of sellers when sellers exist")
    void getAllSellers_SellersExist_ReturnSellers() throws Exception {
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
                        "name": "Alberto Mayert",
                        "contactInfo": "878-999-0161",
                        "registrationDate": "2024-10-22T14:30:00"
                    },
                    {
                        "id": 2,
                        "name": "Elmer Runte",
                        "contactInfo": "645-423-7550",
                        "registrationDate": "2024-09-03T09:45:00"
                    },
                    {
                        "id": 3,
                        "name": "Christina Zieme",
                        "contactInfo": "921-270-2943",
                        "registrationDate": "2024-10-19T12:00:00"
                    }

                  ]"""));
    }
    @Test
    @Sql("/sql/seller.sql")
    @DisplayName("Get seller by id - Should return seller when seller exist")
    void getSellerById_SellerExists_ReturnSeller() throws Exception {
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
                        "name": "Alberto Mayert",
                        "contactInfo": "878-999-0161",
                        "registrationDate": "2024-10-22T14:30:00"
                    }
                    """));
    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get seller by id - Should return seller with transaction when seller exist and transaction is true in params")
    void getSellerById_SellerExistsTransactionParamIsTrue_ReturnSellerAndHisTransaction() throws Exception {
        //given
        int id = 1;
        var requestBuilder = get(url + "/" + id).param("transactions","true");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        {
                          "id": 1,
                          "name": "Alberto Mayert",
                          "contactInfo": "878-999-0161",
                          "registrationDate": "2022-10-22T14:30:00",
                          "transactions": [
                            {
                              "id": 1,
                              "amount": 500.12,
                              "paymentType": "TRANSFER",
                              "transactionDate": "2024-09-11T14:30:00",
                              "sellerId": 1
                            },
                            {
                              "id": 2,
                              "amount": 100.50,
                              "paymentType": "CARD",
                              "transactionDate": "2024-09-11T15:00:00",
                              "sellerId": 1
                            },
                            {
                              "id": 3,
                              "amount": 325.51,
                              "paymentType": "TRANSFER",
                              "transactionDate": "2024-09-11T14:45:00",
                              "sellerId": 1
                            }
                          ]
                    }"""));
    }
    @Test
    @DisplayName("Get seller by id -Should return NOT FOUND when seller doesnt exist")
    void getSellerById_SellerNotExists_ReturnNotFound() throws Exception {
        int id = 1;
        var requestBuilder = get(url + "/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    }

    @Test
    @DisplayName("Create seller -Should return Seller when payload is valid")
    void createSeller_PayloadValid_ReturnSeller() throws Exception {
        //given
        var seller = new NewSellerPayload("Alexander M.", "821-123-12");

        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seller));
        //when
        var response = mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isCreated(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = objectMapper.readValue(response.getContentAsString(), SellerDto.class);
        assertThat(content.getId()).isEqualTo(1);
        assertThat(content.getName()).isEqualTo(seller.name());
        assertThat(content.getContactInfo()).isEqualTo(seller.contactInfo());
    }
    @ParameterizedTest
    @MethodSource("invalidPayloadNewSeller")
    @DisplayName("Create seller - Should return bad request when payload is invalid")
    void createSeller_PayloadInValid_ReturnBadRequest(NewSellerPayload sellerPayload) throws Exception {
        //given
        var requestBuilder = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellerPayload));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    @DisplayName("Update seller - Should return no content when payload is valid")
    @Sql("/sql/seller.sql")
    void updateSeller_PayloadIsValid_ReturnsNoContent() throws Exception {
        //given
        int id = 1;
        var updateSeller = new UpdateSellerPayload(null,"125-122-122");
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateSeller));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isOk());
        var response = mvc.perform(get(url+"/"+id))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        var content = objectMapper.readValue(response.getContentAsString(), SellerDto.class);
        assertThat(content.getName()).isNotNull();
        assertThat(content.getContactInfo()).isEqualTo(updateSeller.contactInfo());
        assertThat(content.getRegistrationDate()).isNotNull();

    }
    @ParameterizedTest
    @MethodSource("invalidPayloadUpdateSeller")
    @Sql("/sql/seller.sql")
    @DisplayName("Update seller - Should return bad request when payload is invalid")
    void updateSeller_PayloadIsInValid_ReturnsBadRequest(UpdateSellerPayload seller) throws Exception {
        //given
        int id = 1;
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seller));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                );

    }
    @Test
    @DisplayName("Update seller - Should return NOT FOUND when payload is valid and seller doesnt exist ")
    void updateSeller_PayloadIsValidSellerNotExists_ReturnsNotFound() throws Exception {
        //given
        int id = 1;
        var requestBuilder = put(url+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UpdateSellerPayload(null,"125-122-122")));
        //when
        mvc.perform(requestBuilder)
                .andExpectAll(
                        //then
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    }
    @Test
    @Sql("/sql/seller.sql")
    @DisplayName("Delete seller - Should return no content when seller exists")
    void deleteSeller_SellerExists_ReturnsNoContent() throws Exception {
        //given
        int id = 1;
        var requestBuilder = delete(url+"/"+id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNoContent()
        );
        mvc.perform(get(url+"/" +id))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
    @Test
    @DisplayName("Delete seller - Should return NOT FOUND when seller doest exist")
    void deleteSeller_SellerDoesNotExist_ReturnsNotFound() throws Exception {
        //given
        int id = 1;
        var requestBuilder = delete(url+"/" + id);
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );

    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get most productive seller in period(year) - Should return valid top seller when period is valid")
    void getMostProductiveSellerInPeriod_PayloadIsValidPeriodIsYearSellerExist_ReturnsSeller() throws Exception {
        //given
        var requestBuilder = get(url+"/most-productive")
                .param("period","year");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        {
                          "id": 1,
                          "name": "Alberto Mayert",
                          "contactInfo": "878-999-0161",
                          "registrationDate": "2022-10-22T14:30:00"
                        }
                        """)
        );

    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get most productive seller in period(month) - Should return valid top seller when period is valid")
    void getMostProductiveSellerInPeriod_PayloadIsValidPeriodIsMonthSellerExist_ReturnsSeller() throws Exception {
        //given
        var requestBuilder = get(url+"/most-productive")
                .param("period","month");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        {
                          "id": 2,
                          "name": "Elmer Runte",
                          "contactInfo": "645-423-7550",
                          "registrationDate": "2024-09-03T09:45:00"
                        }
                        """)
        );

    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get most productive seller in period(day) - Should return valid top seller when period is valid")
    void getMostProductiveSellerInPeriod_PayloadIsValidPeriodIsDaySellerExist_ReturnsSeller() throws Exception {
        //given
        var requestBuilder = get(url+"/most-productive")
                .param("period","day");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                            {
                              "id": 3,
                              "name": "Christina Zieme",
                              "contactInfo": "921-270-2943",
                              "registrationDate": "2024-10-19T12:00:00"
                            }
                        """)
        );

    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get most productive seller in period - Should return not found when there is no sellers in period")
    void getMostProductiveSellerInPeriod_PayloadIsValidSellerNotExists_ReturnsNotFound() throws Exception {
        //given
        Instant mockTime = Instant.parse("2021-10-20T12:30:00Z");
        when(clock.instant()).thenReturn(mockTime);
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);
        var requestBuilder = get(url+"/most-productive")
                .param("period","day");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get most productive seller in period - Should return bad request when period is invalid")
    void getMostProductiveSellerInPeriod_PayloadIsInValid_ReturnsBadRequest() throws Exception {
        //given
        var requestBuilder = get(url+"/most-productive")
                .param("period","ssm");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }

    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get sellers which sum of transaction`s amount less then very large sum and transaction filtered by date "+
            "- Should return list of all sellers which transaction were between dates ")
    void getSellersTransactionsAmountLessThenSumma_PayloadIsValidSellersExistSum1_ReturnListOfThreeSellers() throws Exception {
        //given
        var requestBuilder = get(url+"/less-then-summa")
                .param("dateFrom","2024-09-10")
                .param("dateTo","2024-10-20")
                .param("summa", "10000");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(""" 
                        [
                              {
                                "id": 1,
                                "name": "Alberto Mayert",
                                "contactInfo": "878-999-0161",
                                "registrationDate": "2022-10-22T14:30:00"
                              },
                              {
                                "id": 2,
                                "name": "Elmer Runte",
                                "contactInfo": "645-423-7550",
                                "registrationDate": "2024-09-03T09:45:00"
                              },
                              {
                                "id": 3,
                                "name": "Christina Zieme",
                                "contactInfo": "921-270-2943",
                                "registrationDate": "2024-10-19T12:00:00"
                              }
                          ]
        """)
        );

    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get sellers which sum of transaction`s amount less then summa and transaction filtered by date "+
            "- Should return list of sellers when payload is valid and there is sellers ")
    void getSellersTransactionsAmountLessThenSumma_PayloadIsValidSellersExistSum2_ReturnListOfTwoSellers() throws Exception {
        //given
        var requestBuilder = get(url+"/less-then-summa")
                .param("dateFrom","2024-09-10")
                .param("dateTo","2024-10-20")
                .param("summa", "58.12");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(""" 
                        [
                              {
                                "id": 2,
                                "name": "Elmer Runte",
                                "contactInfo": "645-423-7550",
                                "registrationDate": "2024-09-03T09:45:00"
                              },
                              {
                                "id": 3,
                                "name": "Christina Zieme",
                                "contactInfo": "921-270-2943",
                                "registrationDate": "2024-10-19T12:00:00"
                              }
                          ]
        """)
        );

    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get sellers which sum of transaction`s amount less then summa and transaction filtered by date "+
            "- Should return list of sellers when payload is valid and there is sellers ")
    void getSellersTransactionsAmountLessThenSumma_PayloadIsValidSellersExistSum3_ReturnListOfOneSellers() throws Exception {
        //given
        var requestBuilder = get(url+"/less-then-summa")
                .param("dateFrom","2024-09-10")
                .param("dateTo","2024-10-20")
                .param("summa", "7.12");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(""" 
                        [
                              {
                                "id": 3,
                                "name": "Christina Zieme",
                                "contactInfo": "921-270-2943",
                                "registrationDate": "2024-10-19T12:00:00"
                              }
                          ]
        """)
        );
    }
    @Test
    @Sql("/sql/insert.sql")
    @DisplayName("Get sellers which sum of transaction`s amount less then summa and transaction filtered by date "+
            "- Should return empty list when payload is valid and there is not sellers ")
    void getSellersTransactionsAmountLessThenSumma_PayloadIsValidSellersExistSum4_ReturnEmptyList() throws Exception {
        //given
        var requestBuilder = get(url+"/less-then-summa")
                .param("dateFrom","2024-09-10")
                .param("dateTo","2024-10-20")
                .param("summa", "1.1");
        //when
        mvc.perform(requestBuilder).andExpectAll(
                //then
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("[]")
        );
    }
    @Test
    @DisplayName("Get sellers which sum of transaction`s amount less then summa and transaction filtered by date "+
            "- Should return bad request when payload is invalid ( dateFrom is after then dateTo )")
    void getSellersTransactionsAmountLessThenSumma_PayloadIsInValid_ReturnBadRequest() throws Exception {
        //given
        var requestBuilder = get(url+"/less-then-summa")
                .param("dateFrom","2025-09-10")
                .param("dateTo","2023-10-20")
                .param("summa", "1.1");

        //when
        mvc.perform(requestBuilder).andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_PROBLEM_JSON)
        );
    }

    static Stream<NewSellerPayload> invalidPayloadNewSeller() {
        return Stream.of(
                new NewSellerPayload(null,null),
                new NewSellerPayload("",null),
                new NewSellerPayload("a",null),
                new NewSellerPayload("ab",null),
                new NewSellerPayload("Alexander M.","x"),
                new NewSellerPayload("Maria D.","xl"),
                new NewSellerPayload("x","123-231-731"),
                new NewSellerPayload(null,"123-231-731")
        );
    }
    static Stream<UpdateSellerPayload> invalidPayloadUpdateSeller() {
        return Stream.of(
                new UpdateSellerPayload(null,"1"),
                new UpdateSellerPayload("x","y"),
                new UpdateSellerPayload("","")
        );
    }


}
