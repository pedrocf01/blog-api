package com.pedrocf01.blog_api;


import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.pedrocf01.blog_api.entity.Comment;
import com.pedrocf01.blog_api.entity.Post;
import com.pedrocf01.blog_api.entity.User;
import com.pedrocf01.blog_api.repository.CommentRepository;
import com.pedrocf01.blog_api.repository.PostRepository;
import com.pedrocf01.blog_api.repository.UserRepository;

@SpringBootApplication
@EnableJpaAuditing
public class BlogApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogApiApplication.class, args);
	}

	@Bean
	ApplicationRunner runner(PostRepository postRepository, CommentRepository commentRepository, 
							 UserRepository userRepository) {

		return args -> {
			User user = new User("pedro", "pedro@gmail.com", "password", "Pedro Cordeiro");
			userRepository.save(user);
			Post post = new Post("My post", "my-post", "A simple post", "A regular post", "image.com/image", Post.PostStatus.PUBLISHED, user);
			postRepository.save(post);
			Comment comment = new Comment("My comment", post);
			commentRepository.save(comment);
		};
	}
}
