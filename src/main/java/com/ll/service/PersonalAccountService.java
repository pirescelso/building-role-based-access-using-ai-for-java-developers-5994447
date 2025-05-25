package com.ll.service;

import org.springframework.stereotype.Service;

import com.ll.model.PersonalAccount;
import com.ll.repository.AccountRepository;

@Service
public class PersonalAccountService {

  private final AccountRepository accountRepository;

  public PersonalAccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public PersonalAccount createPersonalAccount(String email, String password) {
    if (accountRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Já existe uma conta com este email");
    }

    PersonalAccount account = new PersonalAccount(email, password);

    return accountRepository.savePersonalAccount(account);
  }

  public PersonalAccount getPersonalAccount(PersonalAccount requestingAccount, String accountId) {
    PersonalAccount account = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    if (!account.getEmail().equals(requestingAccount.getEmail())) {
      throw new SecurityException("Você não tem permissão para acessar esta conta");
    }

    return account;
  }

  public PersonalAccount updatePersonalAccount(PersonalAccount requestingAccount, String accountId,
      String newEmail, String newPassword) {
    PersonalAccount existingAccount = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    if (!existingAccount.getEmail().equals(requestingAccount.getEmail())) {
      throw new SecurityException("Você não tem permissão para atualizar esta conta");
    }

    if (!existingAccount.getEmail().equals(newEmail) && accountRepository.existsByEmail(newEmail)) {
      throw new IllegalArgumentException("O novo email já está em uso por outra conta");
    }

    existingAccount.setEmail(newEmail);
    existingAccount.setPassword(newPassword);
    return accountRepository.save(existingAccount);
  }

  public void deletePersonalAccount(PersonalAccount requestingAccount, String accountId) {
    PersonalAccount existingAccount = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));

    if (!existingAccount.getEmail().equals(requestingAccount.getEmail())) {
      throw new SecurityException("Você não tem permissão para deletar esta conta");
    }

    accountRepository.delete(existingAccount);
  }
}
