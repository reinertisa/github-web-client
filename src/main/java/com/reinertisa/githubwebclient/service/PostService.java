package com.reinertisa.githubwebclient.service;

import com.reinertisa.githubwebclient.entity.Post;

import java.util.List;

public interface PostService {

    List<Post> getPosts();
    Post getPost(Integer id);
    Post createPost(Post post);
    Post updatePost(Integer id, Post post);
    void deletePost(Integer id);
}
