package com.company.techblog.service;

import com.company.techblog.domain.Post;
import com.company.techblog.domain.User;
import com.company.techblog.dto.PostDto;
import com.company.techblog.repository.PostRepository;
import com.company.techblog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("TC1 : 활성 사용자는 게시글을 작성할 수 있다")
    void activeUserDoCreatePost() {
        Long userId = 2L;
        PostDto.Request request = new PostDto.Request(userId, "Test Title", "Test body");

        User activeUser = User.builder()
                .email("test@example.com")
                .name("Jane Smith")
                .build();

        Post post = Post.builder()
                .author(activeUser)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(activeUser));
        given(postRepository.save(any(Post.class))).willReturn(post);

        PostDto.Response response = postService.createPost(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(post.getTitle());
        assertThat(response.getContent()).isEqualTo(post.getContent());

        verify(userRepository).findById(userId);
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("TC2 : 비활성 사용자는 게시글 작성시 예외 발생한다")
    void inactiveUserUnablePost() {
        Long userId = 1L;
        PostDto.Request request = new PostDto.Request(userId, "Test Title", "Test body");

        User inactiveUser = User.builder()
                .email("inactivate@example.com")
                .name("퇴사자")
                .build();

        inactiveUser.resign();

        given(userRepository.findById(userId)).willReturn(Optional.of(inactiveUser));

        assertThatThrownBy(() -> postService.createPost(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Not Authorized User; Accessed by resigned user");

        verify(userRepository).findById(userId);
        verify(postRepository, never()).save(any(Post.class));
    }
}