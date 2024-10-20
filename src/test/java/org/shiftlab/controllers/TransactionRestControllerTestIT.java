package org.shiftlab.controllers;

import org.shiftlab.Main;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Main.class})
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class TransactionRestControllerTestIT {
}
