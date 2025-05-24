package com.ll.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Document(collection = "accounts")
public class PersonalAccount extends Account {

  @Indexed(unique = true)
  private String email;
  private String passwordHash;

  private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public PersonalAccount() {
    super(AccountType.PERSONAL);
  }

  public PersonalAccount(String email, String password) {
    super(AccountType.PERSONAL);
    this.email = email;
    this.setPassword(password);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.passwordHash = passwordEncoder.encode(password);
  }

  public boolean checkPassword(String password) {
    return passwordEncoder.matches(password, this.passwordHash);
  }
}
