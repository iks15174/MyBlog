package com.jiho.board.springbootaws.domain.posts.search;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.jiho.board.springbootaws.domain.postTag.PostTag;
import com.jiho.board.springbootaws.domain.postTag.QPostTag;
import com.jiho.board.springbootaws.domain.posts.Posts;
import com.jiho.board.springbootaws.domain.posts.QPosts;
import com.jiho.board.springbootaws.domain.tag.QTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;

public class SearchPostRepositoryImpl extends QuerydslRepositorySupport implements SearchPostRepository {

    public SearchPostRepositoryImpl() {
        super(Posts.class);
    }

    @Override
    public PageImpl<Posts> searchPost(String type, String keyword, ArrayList<Long> categoryIds,
            Pageable pageable) {
        QPosts posts = QPosts.posts;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        JPQLQuery<Posts> jpqlQueryPost = from(posts);
        jpqlQueryPost.leftJoin(posts.category).fetchJoin();
        jpqlQueryPost.leftJoin(posts.content);

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
        jpqlQueryPostTag.join(posts.author).fetchJoin();
        jpqlQueryPostTag.leftJoin(posts.category).fetchJoin();
        jpqlQueryPostTag.leftJoin(posts.tags, postTag).fetchJoin();
        jpqlQueryPostTag.join(postTag.tag, tag).fetchJoin();
        BooleanBuilder inPostList = new BooleanBuilder();
        inPostList.or(posts.id.eq((long) -1));
        for (Posts p : postList) {
            inPostList.or(posts.id.eq(p.getId()));
        }
        JPQLQuery<Posts> postTagResult = jpqlQueryPostTag.select(posts).distinct();
        postTagResult.where(inPostList);
        List<Posts> postTagList = postTagResult.fetch();

        // printState(postList, postTagList);

        long count = postResult.fetchCount();
        return new PageImpl<Posts>(
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
                        conditionBuilder.or(posts.content.fullContent.contains(keyword));
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

    private void printState(List<Posts> postList, List<Posts> postTag) {
        postList.forEach(p -> {
            System.out.println(p.getTitle());
        });

        postTag.stream().forEach(pt -> {
            System.out.println(pt.getTitle());
            for(PostTag t : pt.getTags()) {
                System.out.println(t.getTag().getName());
            }
        });
        // postTagMap.entrySet().stream().forEach(e -> {
        //     System.out.println(e.getKey().getTitle());
        //     System.out.println(e.getKey().getCategory().getName());
        //     for (Tag t : e.getValue()) {
        //         System.out.println(t);
        //     }
        // });
    }

    private BooleanBuilder tagBuilder(ArrayList<Long> tagId, QPosts posts) {
        BooleanBuilder tagBuilder = new BooleanBuilder();
        return tagBuilder;
    }

}
