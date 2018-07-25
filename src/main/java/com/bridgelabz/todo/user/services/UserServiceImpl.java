package com.bridgelabz.todo.user.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.todo.note.services.NoteService;
import com.bridgelabz.todo.user.exceptions.UserActivationException;
import com.bridgelabz.todo.user.exceptions.UserNotFoundException;
import com.bridgelabz.todo.user.factories.UserFactory;
import com.bridgelabz.todo.user.models.Email;
import com.bridgelabz.todo.user.models.RegistrationDto;
import com.bridgelabz.todo.user.models.User;
import com.bridgelabz.todo.user.models.UserDto;
import com.bridgelabz.todo.user.repositories.UserRepository;
import com.bridgelabz.todo.user.utils.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserFactory userFactory;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	@Value("${registration.template.path}")
	private String emailTemplatePath;
	
	@Autowired
	private NoteService noteService;;

	@Override
	public void register(RegistrationDto registrationDto, String url) throws IOException, MessagingException {
		UserUtility.validateUser(registrationDto);

		User user = userFactory.getUserFromRegistrationDto(registrationDto);
		user.setActivated(false);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("USER");

		userRepository.save(user);

		File mailFile = ResourceUtils.getFile(emailTemplatePath);
		String mailText = new String(Files.readAllBytes(mailFile.toPath()));

		String token = UserUtility.generate(user.getId(), -1, "activation_token");
		mailText = mailText.replace("@link", url + "/activate?token=" + token);

		Email email = userFactory.getEmail("ghargesiddharth@gmail.com", "User activation", mailText);
		emailService.sendEmail(email);
	}

	@Override
	public void activateUser(String token) throws UserActivationException {
		try {
			long userId = UserUtility.verify(token);

			Optional<User> optionalUser = userRepository.findById(userId);

			if (!optionalUser.isPresent()) {
				throw new UserActivationException("User does not exist");
			}

			User user = optionalUser.get();
			user.setActivated(true);

			userRepository.save(user);
		} catch (Exception e) {
			throw new UserActivationException("Malformed link");
		}

	}

	@Override
	public String uploadProfilePicture(MultipartFile image, String url, long userId) {
		String link = noteService.saveImage(image);
		link = url + link;
		
		Optional<User> optionalUser = userRepository.findById(userId);

		User user = optionalUser.get();
		user.setProfileUrl(link);
		
		userRepository.save(user);
		
		return link;
	}
	
	@Override
	public UserDto getUserProfile(long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		
		User user = optionalUser.get();

		UserDto userDto = userFactory.getUserDtoFromUser(user);
		
		return userDto;
	}
	
	@Override
	public UserDto getUserProfile(String email) throws UserNotFoundException {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		
		if (!optionalUser.isPresent()) {
			throw new UserNotFoundException(String.format("User with email id '%s' does not exist", email));
		}
		
		User user = optionalUser.get();

		UserDto userDto = userFactory.getUserDtoFromUser(user);
		
		return userDto;
	}

}
