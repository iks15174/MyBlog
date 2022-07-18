package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.jiho.board.springbootaws.domain.postTag.QPostTag;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.QPosts;
import com.jiho.board.springbootaws.domain.tag.QTag;
import com.jiho.board.springbootaws.domain.tag.Tag;
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
    public PageImpl<List<Object>> searchPost(String type, String keyword, ArrayList<Long> categoryIds,
            Pageable pageable) {
        QPosts posts = QPosts.posts;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        JPQLQuery<Posts> jpqlQueryPost = from(posts);
        jpqlQueryPost.join(posts.author).fetchJoin();
        jpqlQueryPost.join(posts.category).fetchJoin();

        BooleanBuilder totalBuilder = new BooleanBuilder();
        BooleanBuilder keywBuilder = makeKeywordBuilder(type, keyword, posts);
        BooleanBuilder categoryBuilder = makeCategoryBuilder(categoryIds, posts);
        totalBuilder.and(keywBuilder).and(categoryBuilder);

        JPQLQuery<Posts> postResult = jpqlQueryPost.select(posts);
        postResult.where(totalBuilder);
        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();

            PathBuilder<Posts> orderByExpression = new PathBuilder<Posts>(Posts.class, "posts");
            postResult.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
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
        List<List<Object>> postTagList = postTagMap.entrySet().stream().map(e -> Arrays.asList(e.getKey(), e.getValue())).collect(Collectors.toList());

        // printState(postList, postTagMap);

        long count = postResult.fetchCount();
        return new PageImpl<List<Object>>(
                postTagList,
                pageable,
                count);
    }

    private BooleanBuilder makeKeywordBuilder(String type, String keyword, QPosts posts) {
        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if (type != null) {
            String[] typeArr = type.split("");
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
        }
        return conditionBuilder;
    }

    private BooleanBuilder makeCategoryBuilder(ArrayList<Long> categoryIds, QPosts posts) {
        BooleanBuilder categoryBuilder = new BooleanBuilder();
        for (int i = 0; i < categoryIds.size(); i++) {
            categoryBuilder.or(posts.category.id.eq(categoryIds.get(i)));
        }
        return categoryBuilder;
    }

    private void printState(List<Posts> postList, Map<Posts, List<Tag>> postTagMap) {
        postList.forEach(p -> {
            System.out.println(p);
        });
        postTagMap.entrySet().stream().forEach(e -> {
            System.out.println(e.getKey());
            for (Tag t : e.getValue()) {
                System.out.println(t);
            }
        });
    }

    private BooleanBuilder tagBuilder(ArrayList<Long> tagId, QPosts posts) {
        BooleanBuilder tagBuilder = new BooleanBuilder();
        return tagBuilder;
    }

}
