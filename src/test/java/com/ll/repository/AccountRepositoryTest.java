package com.ll.repository;

import com.ll.model.AccountType;
import com.ll.model.PersonalAccount;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private PersonalAccount testAccount;

    @BeforeEach
    void setup() {
        testAccount = new PersonalAccount("test@example.com", "password123");
        accountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    void testDeleteByEmail() {
        // Arrange
        assertTrue(accountRepository.existsByEmail("test@example.com"));

        // Act
        accountRepository.deleteByEmail("test@example.com");

        // Assert
        assertFalse(accountRepository.existsByEmail("test@example.com"));
    }

    @Test
    void testExistsByEmail() {
        // Act & Assert
        assertTrue(accountRepository.existsByEmail("test@example.com"));
        assertFalse(accountRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    void testFindAllPersonalAccounts() {
        // Arrange
        PersonalAccount secondAccount = new PersonalAccount("test2@example.com", "password456");
        accountRepository.save(secondAccount);

        // Act
        List<PersonalAccount> accounts = accountRepository.findAllPersonalAccounts();

        // Assert
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(a -> a.getEmail().equals("test@example.com")));
        assertTrue(accounts.stream().anyMatch(a -> a.getEmail().equals("test2@example.com")));
    }

    @Test
    void testFindByEmailAndAccountType() {
        // Act
        Optional<PersonalAccount> found = accountRepository.findByEmailAndAccountType(
                "test@example.com",
                AccountType.PERSONAL.toString());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testSavePersonalAccount() {
        // Arrange
        PersonalAccount newAccount = new PersonalAccount("new@example.com", "password789");

        // Act
        PersonalAccount savedAccount = accountRepository.savePersonalAccount(newAccount);

        // Assert
        assertNotNull(savedAccount.getId());
        assertEquals("new@example.com", savedAccount.getEmail());
        assertTrue(accountRepository.existsByEmail("new@example.com"));
    }

    @Test
    void testSavePersonalAccount_WithWrongType() {
        // Arrange
        PersonalAccount account = new PersonalAccount("wrong@example.com", "password789");
        account.setAccountType(AccountType.ORGANIZATION);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            accountRepository.savePersonalAccount(account);
        });
    }
}
