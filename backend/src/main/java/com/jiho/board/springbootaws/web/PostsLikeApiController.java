package com.jiho.board.springbootaws.web;

import java.security.Principal;

import com.jiho.board.springbootaws.domain.member.MemberRole;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.service.postLike.PostLikeService;
import com.jiho.board.springbootaws.web.dto.postLike.PostLikeResponseDto;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PostsLikeApiController {
    private final PostLikeService postLikeService;

    @Secured(MemberRole.ROLES.USER)
    @GetMapping("/api/v1/like/posts/{id}")
    public PostLikeResponseDto likePost(@PathVariable Long id, @AuthenticationPrincipal AuthMemberDto authMemberDto) {
        System.out.println(authMemberDto);
        return postLikeService.togglePostLike(authMemberDto, id);
    }
}
