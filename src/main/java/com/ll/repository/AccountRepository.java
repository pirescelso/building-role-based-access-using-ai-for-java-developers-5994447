package com.ll.repository;

import com.ll.model.AccountType;
import com.ll.model.PersonalAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<PersonalAccount, String> {

    // Busca conta pessoal por email
    Optional<PersonalAccount> findByEmailAndAccountType(String email, String accountType);

    // Lista todas as contas pessoais
    @Query("{'accountType': 'PERSONAL'}")
    List<PersonalAccount> findAllPersonalAccounts();

    // Verifica se existe conta com email
    boolean existsByEmail(String email);

    // Deleta conta por email
    void deleteByEmail(String email);

    // Salva uma conta pessoal e retorna a conta salva
    default PersonalAccount savePersonalAccount(PersonalAccount account) {
        if (account.getAccountType() != AccountType.PERSONAL) {
            throw new IllegalArgumentException("A conta deve ser do tipo PERSONAL");
        }
        return save(account);
    }
}
