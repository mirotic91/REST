package com.mirotic.demorestapi.configs;

import com.mirotic.demorestapi.accounts.Account;
import com.mirotic.demorestapi.accounts.AccountRole;
import com.mirotic.demorestapi.accounts.AccountService;
import com.mirotic.demorestapi.common.BaseControllerTests;
import com.mirotic.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTests {

    @Autowired
    AccountService accountService;

    @Test
    @TestDescription("인증 토큰 발급")
    public void getAuthToken() throws Exception {
        String clientId = "application";
        String clientPassword = "password";

        String username = "tester@email.com";
        String password = "123123";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.save(account);

        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientPassword))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
                .andExpect(jsonPath("refresh_token").exists());
    }

    @Test
    @TestDescription("인증 정보 불일치로 인한 인증 토큰 발급 실패")
    public void getAuthToken_PasswordNotMatch() throws Exception {
        String clientId = "application";
        String clientPassword = "password";

        String username = "test@email.com";
        String password = "123123";
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.save(account);

        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientPassword))
                .param("username", username)
                .param("password", "wrong password")
                .param("grant_type", "password")
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").exists())
                .andExpect(jsonPath("error_description").value("Bad credentials"));
    }

}