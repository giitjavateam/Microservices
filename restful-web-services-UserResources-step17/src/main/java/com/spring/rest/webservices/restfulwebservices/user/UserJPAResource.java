package com.spring.rest.webservices.restfulwebservices.user;

import java.net.URI;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UserJPAResource {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;
	
	@GetMapping("/jpa/users")
	public List<User> retreiveAllUsers(){
		return userRepository.findAll();
	}
	
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retreiveUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		
		if(!user.isPresent())
			throw new UserNotFoundException("id-" + id);
		
		EntityModel<User> model = EntityModel.of(user.get());
		
		WebMvcLinkBuilder linkToUsers = linkTo(methodOn(this.getClass()).retreiveAllUsers());
		
		model.add(linkToUsers.withRel("all-users"));
		
		return model;
	}
	
	//delete user
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
		
	}
	
	//input - details of user
		//output - CREATED & return the created URI
		@PostMapping("/jpa/users")
		public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
			User savedUser = userRepository.save(user);
			//CREATED
			// /user/{id}   savedUser.getId()
			
			URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId()).toUri();
			
			return ResponseEntity.created(location).build();
		} 
		
	//Retreive all posts
		@GetMapping("/jpa/users/{id}/posts")
		public List<Post> retreiveAllUsers(@PathVariable int id){
			Optional<User> userOptional =  userRepository.findById(id);
			if(!userOptional.isPresent()) {
				throw new UserNotFoundException("id-" + id);
			}			
			return userOptional.get().getPosts();
		}
		
		@PostMapping("/jpa/users/{id}/posts")
		public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post) {
			
			Optional<User> userOptional =  userRepository.findById(id);
			if(!userOptional.isPresent()) {
				throw new UserNotFoundException("id-" + id);
			}		
			
			User user = userOptional.get();
			
			post.setUser(user);
			
			postRepository.save(post);
			
			URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(post.getId()).toUri();
			
			return ResponseEntity.created(location).build();
		} 
}


