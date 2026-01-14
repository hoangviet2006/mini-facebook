package com.example.mini_facebook.controller;

import com.example.mini_facebook.config.JwtUtil;
import com.example.mini_facebook.dto.PostDTO;
import com.example.mini_facebook.exception.UserNotFoundException;
import com.example.mini_facebook.model.Post;
import com.example.mini_facebook.model.User;
import com.example.mini_facebook.repository.IUserRepository;
import com.example.mini_facebook.service.impl.ICloudinaryService;
import com.example.mini_facebook.service.impl.IPostService;
import com.example.mini_facebook.service.impl.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    private IPostService postService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ICloudinaryService cloudinaryService;
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50mb

    @GetMapping("")
    public ResponseEntity<?> findPost() {
        try {
            List<Post> posts = postService.findPost();
            if (posts.isEmpty()) {
                return ResponseEntity.ok("Chưa có bài đăng nào!");
            }
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/create/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(@RequestPart("content") String content,
                                        @RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest httpServletRequest) {
        try {
            String auth = httpServletRequest.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không lấy được thông tin từ người dùng");
            }
            String token = auth.substring(7);
            String username = jwtUtil.getUsernameByToken(token);
            User user = userRepository.findByUsername(username).orElse(null);
            PostDTO postDto = new PostDTO();
            if (content != null) {
                postDto.setContent(content);
            }
            if (file != null && file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity
                        .badRequest()
                        .body("File quá lớn. Tối đa 50MB");
            }
            if (file != null && !file.isEmpty()) {
                String img = cloudinaryService.uploadFIle(file);
                postDto.setUrl(img);
            }
            Post post = userService.createPost(postDto, user);
            return ResponseEntity.ok("Đăng bài thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/find/post/by-user")
    public ResponseEntity<?> findPostByUser(HttpServletRequest httpServletRequest) {
        try {
            String auth = httpServletRequest.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không lấy được thông tin từ người dùng!");
            }
            String token = auth.substring(7);
            String username = jwtUtil.getUsernameByToken(token);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Không lấy được thông tin từ người dùng!"));
            List<Post> posts = postService.findPostByUser(user.getId());
            if (posts.isEmpty()) {
                return ResponseEntity.ok("Chưa có bài đăng nào!");
            }
            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Xoá thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
