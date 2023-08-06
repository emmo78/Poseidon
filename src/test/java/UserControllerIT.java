import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@SpringBootTest
@ActiveProfiles("mytest")
@AutoConfigureMockMvc
public class UserControllerIT {
	
	/*@Autowired
	MockMvc mockMvc;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Disabled
	@Test
	public void validateIT() throws Exception {
		mockMvc.perform(post("/user/validate")
	            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
	            .param("username", "New Username")
	            .param("password", "NewPasswdA1=")
	            .param("fullName", "New FullName")
	            .param("role", "USER")
	            .with(csrf()));

	        // THEN
	        //.andExpect(status().isFound())
	        //.andExpect(view().name("redirect:/user/list"));


	}
	*/



}
