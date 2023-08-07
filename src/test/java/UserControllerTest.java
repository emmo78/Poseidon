import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
	
	/*@InjectMocks
	UserController userController;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private BindingResult bindingResult;
	
	@Mock
	private Model model;
	
	@Disabled
	@Test
	public void validateTest() {
		
		//GIVEN
		User user = new User();
		user.setId(null);
		user.setUsername("");
		user.setPassword("aaa1=Passwd");
		user.setFullname("AAA");
		user.setRole("USER");
		
		//WHEN
		String html = userController.validate(user, bindingResult, model);
		
		//THEN
		assertThat(html).isEqualTo("redirect:/user/list");
	}
	*/

}
