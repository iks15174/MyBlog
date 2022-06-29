package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.jiho.board.springbootaws.domain.postTag.QPostTag;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.QPosts;
import com.jiho.board.springbootaws.domain.tag.QTag;
import com.jiho.board.springbootaws.domain.tag.Tag;
import com.jiho.board.springbootaws.web.dto.posts.PostsTagResultDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;

public class SearchPostRepositoryImpl extends QuerydslRepositorySupport implements SearchPostRepository {

    public SearchPostRepositoryImpl() {
        super(Posts.class);
    }

    @Override
    public PageImpl<PostsTagResultDto> searchPost(String type, String keyword, String category,
            Pageable pageable) {
        QPosts posts = QPosts.posts;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        // 검색 결과에 해당하는 Post를 가져온다.
        JPQLQuery<Posts> jpqlQueryPost = from(posts);
        jpqlQueryPost.join(posts.author).fetchJoin();
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (type != null) {
            String[] typeArr = type.split("");
            BooleanBuilder conditionBuilder = new BooleanBuilder();
            for (String t : typeArr) {
                switch (t) {
                    case "t":
                        conditionBuilder.or(posts.title.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(posts.content.contains(keyword));
                        break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }
        JPQLQuery<Posts> result = jpqlQueryPost.select(posts);
        result.where(booleanBuilder);
        result.groupBy(posts);
        JPQLQuery<Posts> postResult = jpqlQueryPost.select(posts);
        postResult.where(booleanBuilder);
        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();

            PathBuilder<Posts> orderByExpression = new PathBuilder<Posts>(Posts.class, "posts");
            postResult.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
        postResult.groupBy(posts);
        postResult.offset(pageable.getOffset());
        postResult.limit(pageable.getPageSize());
        List<Posts> postList = postResult.fetch();

        // 검색결과 Post의 tag들을 가져온다.
        JPQLQuery<Posts> jpqlQueryPostTag = from(posts);
        jpqlQueryPostTag.leftJoin(postTag).on(postTag.posts.eq(posts));
        jpqlQueryPostTag.leftJoin(tag).on(postTag.tag.eq(tag));
        BooleanBuilder inPostList = new BooleanBuilder();
        for (Posts p : postList) {
            inPostList.or(posts.id.eq(p.getId()));
        }
        JPQLQuery<Tuple> postTagResult = jpqlQueryPostTag.select(posts, tag);
        postTagResult.where(inPostList);
        Map<Posts, List<Tag>> postTagMap = postTagResult.transform(GroupBy.groupBy(posts).as(GroupBy.list(tag)));

        // 결과 확인
        // postList.forEach(p -> {
        // System.out.println(p);
        // });
        // postTagMap.entrySet().stream().forEach(e -> {
        // System.out.println(e.getKey());
        // for(Tag t:e.getValue()){
        // System.out.println(t);
        // }
        // });

        List<PostsTagResultDto> postsTagResultDtos = new ArrayList<>();
        for (Posts p : postList) {
            Boolean isFound = false;
            for (Posts key : postTagMap.keySet()) {
                if (key.getId() == p.getId()) {
                    postsTagResultDtos.add(new PostsTagResultDto(p, postTagMap.get(key)));
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                postsTagResultDtos.add(new PostsTagResultDto(p, new ArrayList<Tag>()));
            }
        }

        long count = postList.size();
        return new PageImpl<PostsTagResultDto>(
                postsTagResultDtos,
                pageable,
                count);
    }

}
