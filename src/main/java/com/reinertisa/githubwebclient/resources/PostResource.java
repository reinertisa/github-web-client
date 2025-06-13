package com.reinertisa.githubwebclient.resources;

import com.reinertisa.githubwebclient.entity.Post;
import com.reinertisa.githubwebclient.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@CrossOrigin(origins = "*")
public class PostResource {

    private final PostService postService;

    public PostResource(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public ResponseEntity<List<Post>> getPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(id));
    }

    @PostMapping("")
    public ResponseEntity<Post> create(@RequestBody Post post) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable(name = "id") Integer id, @RequestBody Post post) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Integer id) {
        postService.deletePost(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
