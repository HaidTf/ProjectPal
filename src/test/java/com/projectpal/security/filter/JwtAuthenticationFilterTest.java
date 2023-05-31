package com.projectpal.security.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.projectpal.entity.User;
import com.projectpal.service.JwtService;




@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    public void testWithValidJwt() throws Exception {
        
    	User user = new User("haid", "haidar@gmail.com", "1234");
       
       String jwt = jwtService.generateToken(user);
       
       Mockito.when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void testWithInValidJwt() throws Exception {
        
    	
    	User user2 = new User("anonymous", "haidartf@gmail.com", "123456");
       
       String jwt = jwtService.generateToken(user2);
       
       Mockito.when(userDetailsService.loadUserByUsername(user2.getUsername())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/user")
        		.header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
    
    @Test
    public void testWithNoHeader() throws Exception {

    	
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
