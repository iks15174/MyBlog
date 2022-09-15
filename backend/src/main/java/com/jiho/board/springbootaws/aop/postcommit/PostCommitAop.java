package com.jiho.board.springbootaws.aop.postcommit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;

@Aspect
@AllArgsConstructor
@Configuration
public class PostCommitAop {
    private final PostCommitAdapter postCommitAdapter;

    @Pointcut("@annotation(com.jiho.board.springbootaws.aop.postcommit)")
    private void postCommitPointCut(){}


    @Around("postCommitPointCut()")
    public Object aroundAdvice(final ProceedingJoinPoint pjp) {
        postCommitAdapter.execute(new PjpAfterCommitRunnable(pjp));
        return null;
    }

    private static final class PjpAfterCommitRunnable implements Runnable {
        private final ProceedingJoinPoint pjp;

        public PjpAfterCommitRunnable(ProceedingJoinPoint pjp) {
            this.pjp = pjp;
        }

        @Override
        public void run() {
            try {
                pjp.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
