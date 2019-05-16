package com.mirotic.demorestapi.accounts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUsername() {
        String username = "jonguk@email.com";
        String password = "1234";

        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();

        accountService.save(account);

        UserDetails userDetails = accountService.loadUserByUsername(username);

        boolean matches = passwordEncoder.matches(password, userDetails.getPassword());
        assertThat(matches).isTrue();
        assertThat(userDetails.getPassword()).isNotEqualTo(password);
    }

    @Test
    public void findByUsername_UsernameNotFound() {
        String username = "jonguk@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(containsString(username));

        accountService.loadUserByUsername(username);
    }

}