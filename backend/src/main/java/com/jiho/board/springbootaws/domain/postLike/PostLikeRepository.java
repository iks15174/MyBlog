package com.jiho.board.springbootaws.domain.postLike;

import com.jiho.board.springbootaws.domain.member.Member;
import com.jiho.board.springbootaws.domain.posts.Posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByMemberAndPosts(Member member, Posts posts);

}
