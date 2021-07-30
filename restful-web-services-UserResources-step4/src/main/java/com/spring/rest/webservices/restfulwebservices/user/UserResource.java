package com.spring.rest.webservices.restfulwebservices.user;

import java.net.URI;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserResource {
	
	@Autowired
	private UserDaoService service;
	
	@GetMapping("/users")
	public List<User> retreiveAllUsers(){
		return service.findAll();
	}
	
	@GetMapping("/users/{id}")
	public User retreiveUser(@PathVariable int id) {
		User user = service.findOne(id);
		if(user == null)
			throw new UserNotFoundException("id-" + id);
		
		return user;
	}
	
	//delete user
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable int id) {
		User user = service.deleteById(id);
		if(user == null)
			throw new UserNotFoundException("id-" + id);
	}
	
	//input - details of user
		//output - CREATED & return the created URI
		@PostMapping("/users")
		public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
			User savedUser = service.save(user);
			//CREATED
			// /user/{id}   savedUser.getId()
			
			URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId()).toUri();
			
			return ResponseEntity.created(location).build();
		}
}


