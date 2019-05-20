package com.mirotic.demorestapi.accounts;

import com.mirotic.demorestapi.common.TestDescription;
import com.mirotic.demorestapi.configs.AppProperties;
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

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("등록된 계정으로 로그인 성공")
    public void findByUsername() {
        UserDetails userDetails = accountService.loadUserByUsername(appProperties.getUserUsername());

        boolean matches = passwordEncoder.matches(appProperties.getUserPassword(), userDetails.getPassword());
        assertThat(matches).isTrue();
        assertThat(userDetails.getPassword()).isNotEqualTo(appProperties.getUserPassword());
    }

    @Test
    @TestDescription("등록되지 않은 계정으로 로그인 시도시 실패")
    public void findByUsername_UsernameNotFound() {
        String username = "jonguk@email.com";
        expectedException.expect(UsernameNotFoundException.class);
        expectedException.expectMessage(containsString(username));

        accountService.loadUserByUsername(username);
    }

}