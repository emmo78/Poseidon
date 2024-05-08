import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


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
