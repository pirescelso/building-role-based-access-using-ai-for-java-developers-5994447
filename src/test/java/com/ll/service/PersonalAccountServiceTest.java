package com.ll.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ll.model.PersonalAccount;
import com.ll.repository.AccountRepository;

@SpringBootTest(properties = { "spring.data.mongodb.uri=mongodb://localhost:27017/testdb",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration" })
public class PersonalAccountServiceTest {

  @Autowired
  private PersonalAccountService personalAccountService;

  @Autowired
  private AccountRepository accountRepository;

  @AfterEach
  void cleanup() {
    accountRepository.deleteAll();
  }

  @Test
  void testCreatePersonalAccount() {
    // Dado
    String email = "joao.silva@exemplo.com";
    String password = "senha123";

    // Quando
    PersonalAccount createdAccount = personalAccountService.createPersonalAccount(email, password);

    // Então
    assertNotNull(createdAccount);
    assertEquals(email, createdAccount.getEmail());
    assertTrue(accountRepository.existsByEmail(email));
  }

  @Test
  void testCreatePersonalAccountWithDuplicateEmail() {
    // Dado
    String email = "maria.santos@exemplo.com";
    String password = "senha456";
    personalAccountService.createPersonalAccount(email, password);

    // Quando & Então
    assertThrows(IllegalArgumentException.class, () -> {
      personalAccountService.createPersonalAccount(email, "outrasenha789");
    });
  }

  @Test
  void testGetPersonalAccount() {
    // Dado
    String email = "carlos.oliveira@exemplo.com";
    String password = "senha789";
    PersonalAccount account = personalAccountService.createPersonalAccount(email, password);

    // Quando
    PersonalAccount retrievedAccount = personalAccountService.getPersonalAccount(account, account.getId());

    // Então
    assertNotNull(retrievedAccount);
    assertEquals(email, retrievedAccount.getEmail());
  }
}
