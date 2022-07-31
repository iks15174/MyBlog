package com.jiho.board.springbootaws.service.comments;

import javax.transaction.Transactional;

import com.jiho.board.springbootaws.domain.comments.Comments;
import com.jiho.board.springbootaws.domain.comments.CommentsRepository;
import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.member.MemberRepository;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.PostsRepository;
import com.jiho.board.springbootaws.exception.exceptions.CustomBasicException;
import com.jiho.board.springbootaws.exception.exceptions.ErrorCode;
import com.jiho.board.springbootaws.service.member.dto.AuthMemberDto;
import com.jiho.board.springbootaws.web.dto.comments.CommentsListResponseDto;
import com.jiho.board.springbootaws.web.dto.comments.CommentsResponseDto;
import com.jiho.board.springbootaws.web.dto.comments.CommentsSaveRequestDto;
import com.jiho.board.springbootaws.web.dto.comments.CommentsUpdateRequestDto;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentsService {
        private final CommentsRepository commentsRepository;
        private final PostsRepository postsRepository;
        private final MemberRepository memberRepository;

        @Transactional
        public Long save(CommentsSaveRequestDto requestDto) {
                AuthMemberDto memberDto = (AuthMemberDto) SecurityContextHolder
                                .getContext().getAuthentication().getPrincipal();
                Member author = memberRepository
                                .findByEmail(memberDto.getUsername())
                                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_USER));

                Long postsId = requestDto.getPostsId();
                Posts posts = postsRepository.findById(postsId)
                                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_POST));
                return commentsRepository.save(requestDto.toEntity(posts, author)).getId();
        }

        @Transactional
        public CommentsListResponseDto getList(Long postsId, Pageable pageable) {
                return new CommentsListResponseDto(commentsRepository.findByPosts_Id(postsId, pageable));
        }

        @Transactional
        public CommentsResponseDto findById(Long commentId) {
                Comments entity = commentsRepository.findById(commentId)
                                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_COMMENT));
                return new CommentsResponseDto(entity);
        }

        @Transactional
        public CommentsResponseDto update(Long commentId, CommentsUpdateRequestDto requestDto) {
                Comments entity = commentsRepository.findById(commentId)
                                .orElseThrow(() -> new CustomBasicException(ErrorCode.UNEIXIST_COMMENT));
                entity.update(requestDto.getContent());
                return new CommentsResponseDto(entity);
        }
}
