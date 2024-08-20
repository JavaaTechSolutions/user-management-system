package com.jts.user.service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jts.user.dto.CommonDTO;
import com.jts.user.entity.Users;
import com.jts.user.repository.UsersRepo;

@Service
public class UsersManagementService {

	@Autowired
	private UsersRepo usersRepo;

	@Autowired
	private JWTUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public CommonDTO register(CommonDTO registrationRequest) {
		CommonDTO resp = new CommonDTO();

		try {
			Users user = new Users();
			user.setEmail(registrationRequest.getEmail());
			user.setCity(registrationRequest.getCity());
			user.setRole(registrationRequest.getRole());
			user.setName(registrationRequest.getName());
			user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
			Users result = usersRepo.save(user);

			if (result.getId() > 0) {
				resp.setUser(result);
				resp.setMessage("User Saved Successfully");
				resp.setStatusCode(200);
			}
		} catch (Exception e) {
			resp.setStatusCode(500);
			resp.setError(e.getMessage());
		}

		return resp;
	}

	public CommonDTO login(CommonDTO loginRequest) {
		CommonDTO response = new CommonDTO();

		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
			var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
			var jwt = jwtUtils.generateToken(user);
			var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
			response.setStatusCode(200);
			response.setToken(jwt);
			response.setRole(user.getRole());
			response.setRefreshToken(refreshToken);
			response.setExpirationTime("24Hrs");
			response.setMessage("Successfully Logged In");

		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage(e.getMessage());
		}

		return response;
	}

	public CommonDTO refreshToken(CommonDTO tokenRequest) {
		CommonDTO response = new CommonDTO();

		try {
			String ourEmail = jwtUtils.extractUsername(tokenRequest.getToken());
			Users users = usersRepo.findByEmail(ourEmail).orElseThrow();

			if (jwtUtils.isTokenValid(tokenRequest.getToken(), users)) {
				var jwt = jwtUtils.generateToken(users);
				response.setStatusCode(200);
				response.setToken(jwt);
				response.setRefreshToken(tokenRequest.getToken());
				response.setExpirationTime("24Hr");
				response.setMessage("Successfully Refreshed Token");
			}

			response.setStatusCode(200);
			return response;
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage(e.getMessage());
			return response;
		}
	}

	public CommonDTO getAllUsers() {
		CommonDTO reqRes = new CommonDTO();

		try {
			List<Users> result = usersRepo.findAll();

			if (!result.isEmpty()) {
				reqRes.setUsers(result);
				reqRes.setStatusCode(200);
				reqRes.setMessage("Successful");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("No users found");
			}

			return reqRes;
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred: " + e.getMessage());
			return reqRes;
		}
	}

	public CommonDTO getUsersById(Integer id) {
		CommonDTO reqRes = new CommonDTO();

		try {
			Users usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
			reqRes.setUser(usersById);
			reqRes.setStatusCode(200);
			reqRes.setMessage("Users with id '" + id + "' found successfully");
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred: " + e.getMessage());
		}

		return reqRes;
	}

	public CommonDTO deleteUser(Integer userId) {
		CommonDTO reqRes = new CommonDTO();

		try {
			Optional<Users> userOptional = usersRepo.findById(userId);

			if (userOptional.isPresent()) {
				usersRepo.deleteById(userId);
				reqRes.setStatusCode(200);
				reqRes.setMessage("User deleted successfully");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("User not found for deletion");
			}
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
		}

		return reqRes;
	}

	public CommonDTO updateUser(Integer userId, Users updatedUser) {
		CommonDTO reqRes = new CommonDTO();

		try {
			Optional<Users> userOptional = usersRepo.findById(userId);

			if (userOptional.isPresent()) {
				Users existingUser = userOptional.get();
				existingUser.setEmail(updatedUser.getEmail());
				existingUser.setName(updatedUser.getName());
				existingUser.setCity(updatedUser.getCity());
				existingUser.setRole(updatedUser.getRole());

				// Check if password is present in the request
				if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
					// Encode the password and update it
					existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
				}

				Users savedUser = usersRepo.save(existingUser);
				reqRes.setUser(savedUser);
				reqRes.setStatusCode(200);
				reqRes.setMessage("User updated successfully");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("User not found for update");
			}
		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
		}

		return reqRes;
	}

	public CommonDTO getPersonalInfo(String email) {
		CommonDTO reqRes = new CommonDTO();

		try {
			Optional<Users> userOptional = usersRepo.findByEmail(email);

			if (userOptional.isPresent()) {
				reqRes.setUser(userOptional.get());
				reqRes.setStatusCode(200);
				reqRes.setMessage("successful");
			} else {
				reqRes.setStatusCode(404);
				reqRes.setMessage("User not found for update");
			}

		} catch (Exception e) {
			reqRes.setStatusCode(500);
			reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
		}

		return reqRes;
	}

}
