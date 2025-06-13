package com.reinertisa.githubwebclient.service;

import com.reinertisa.githubwebclient.entity.Post;
import com.reinertisa.githubwebclient.repository.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackOn = Exception.class)
public class PostServiceImpl implements PostService {

    private final WebClient webClient;
    private final PostRepository postRepository;

    public PostServiceImpl(WebClient webClient, PostRepository postRepository) {
        this.webClient = webClient;
        this.postRepository = postRepository;
    }
    @Override
    public List<Post> getPosts() {

        //1. Approach
        // Mono<List<Post>> Emits the complete list once all items are collected.
        Mono<List<Post>> postMono = webClient.get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(Post.class)// Deserializes the response into a stream of Post objects.
                .collectList(); // Collects all emitted items from the Flux<Post> into a List<Post>.

        List<Post> posts = postMono.block(); // it blocks the reactive stream and retrieves the List<Post> from the Mono.

        // 2. Approach
        Mono<List<Post>> postMono2 = webClient.get()
                .uri("/posts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Post>>() {});

        List<Post> post2 = postMono2.block();

        // When to use Each?
        // bodyToFlux(...).collectList() - More flexible/reactive processing (streaming, filtering, mapping)
        // bodyToMono(new ParameterizedTypeReference<...>()) - Simpler if you just want the whole list directly

        if (posts != null) {
            postRepository.saveAll(posts);
        }


        return posts;
    }

    @Override
    public Post getPost(Integer id) {

        Optional<Post> post = postRepository.findById(id);

        if (post.isPresent()) {
            System.out.println("Coming from db");
            return post.get();
        } else {
            System.out.println("Coming from end point");
            // 1. Approach
            Mono<Post> postMono = webClient.get()
                    .uri("/posts/{id}", id)// / dynamically sets the Post ID in the URL
                    .retrieve()// // sends the GET request
                    .bodyToMono(Post.class); // // deserializes the JSON response into a Post

            Post post1 = postMono.block(); // blocks and waits for the Mono to complete, returning the Post

            // 2. Approach
            // Yes, this code works ✅ — but using new ParameterizedTypeReference<Post>() {}
            // is not necessary for non-generic types like Post.
            // Post is a concrete class, not a generic type. Go with 1. Approach
            Mono<Post> postMono2 = webClient.get()
                    .uri("/posts/{id}", id)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Post>() {});
            Post post2 = postMono2.block();

            // When to use ParameterizedTypeReference<T>
            // Only use it for generic types, like: bodyToMono(new ParameterizedTypeReference<List<Post>>() {});
            // Because Java erases generic types at runtime, List.class won’t preserve the Post type —
            // but ParameterizedTypeReference<List<Post>>() will.

            return post1;
        }

    }

    @Override
    public Post createPost(Post post) {
        Optional<Post> post1 = postRepository.findById(post.getId());

        if (post1.isPresent()) {
            System.out.println("Coming from db");
            return post1.get();
        } else {
            System.out.println("Coming from rest end point");
            Mono<Post> postMono = webClient.post()
                    .uri("/posts")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(post)
                    .retrieve()
                    .bodyToMono(Post.class);

            Post createdPost = postMono.block();
            if (createdPost != null) {
                postRepository.save(createdPost);
            }
            return createdPost;
        }

    }

    @Override
    public Post updatePost(Integer id, Post post) {

        Mono<Post> monoPost = webClient.put()
                .uri("/posts/{id}", id)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(Post.class);

        Post updatedPost = monoPost.block();

        if (updatedPost != null) {
            postRepository.save(updatedPost);
        }

        return updatedPost;
    }

    @Override
    public void deletePost(Integer id) {
        webClient.delete()
                .uri("/posts/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();

    }
}
