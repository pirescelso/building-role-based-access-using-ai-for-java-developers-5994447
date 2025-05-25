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
        testAccount = new PersonalAccount("developer@github.com", "gh_pat_123");
        accountRepository.save(testAccount);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    void testDeleteByEmail() {
        // Arrange
        assertTrue(accountRepository.existsByEmail("developer@github.com"));

        // Act
        accountRepository.deleteByEmail("developer@github.com");

        // Assert
        assertFalse(accountRepository.existsByEmail("developer@github.com"));
    }

    @Test
    void testExistsByEmail() {
        // Act & Assert
        assertTrue(accountRepository.existsByEmail("developer@github.com"));
        assertFalse(accountRepository.existsByEmail("nonexistent@github.com"));
    }

    @Test
    void testFindAllPersonalAccounts() {
        // Arrange
        PersonalAccount secondAccount = new PersonalAccount("maintainer@github.com", "gh_pat_456");
        accountRepository.save(secondAccount);

        // Act
        List<PersonalAccount> accounts = accountRepository.findAllPersonalAccounts();

        // Assert
        assertEquals(2, accounts.size());
        assertTrue(accounts.stream().anyMatch(a -> a.getEmail().equals("developer@github.com")));
        assertTrue(accounts.stream().anyMatch(a -> a.getEmail().equals("maintainer@github.com")));
    }

    @Test
    void testFindByEmailAndAccountType() {
        // Act
        Optional<PersonalAccount> found = accountRepository.findByEmailAndAccountType(
                "developer@github.com",
                AccountType.PERSONAL.toString());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("developer@github.com", found.get().getEmail());
    }

    @Test
    void testSavePersonalAccount() {
        // Arrange
        PersonalAccount newAccount = new PersonalAccount("new@github.com", "gh_pat_789");

        // Act
        PersonalAccount savedAccount = accountRepository.savePersonalAccount(newAccount);

        // Assert
        assertNotNull(savedAccount.getId());
        assertEquals("new@github.com", savedAccount.getEmail());
        assertTrue(accountRepository.existsByEmail("new@github.com"));
    }

    @Test
    void testSavePersonalAccount_WithWrongType() {
        // Arrange
        PersonalAccount account = new PersonalAccount("wrong@github.com", "password789");
        account.setAccountType(AccountType.ORGANIZATION);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            accountRepository.savePersonalAccount(account);
        });
    }

    @Test
    void testCompleteAccountCRUDOperations() {
        // CREATE
        PersonalAccount newAccount = new PersonalAccount("contributor@github.com", "gh_pat_789");
        PersonalAccount createdAccount = accountRepository.savePersonalAccount(newAccount);
        assertNotNull(createdAccount.getId());
        assertEquals("contributor@github.com", createdAccount.getEmail());

        // READ
        Optional<PersonalAccount> readAccount = accountRepository.findByEmailAndAccountType(
                "contributor@github.com",
                AccountType.PERSONAL.toString());
        assertTrue(readAccount.isPresent());
        assertEquals("contributor@github.com", readAccount.get().getEmail());

        // UPDATE
        PersonalAccount accountToUpdate = readAccount.get();
        accountToUpdate.setPassword("gh_pat_999");
        PersonalAccount updatedAccount = accountRepository.savePersonalAccount(accountToUpdate);
        assertTrue(updatedAccount.checkPassword("gh_pat_999"));

        // DELETE
        accountRepository.deleteByEmail("contributor@github.com");
        assertFalse(accountRepository.existsByEmail("contributor@github.com"));
    }
}
