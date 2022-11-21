package com.morakmorak.morak_back_end.domain;

// TODO : 아래 주석들은 기능 완성 이후 삭제되어야 합니다.
/*
*
* 기능 1. 게시글/답글에 댓글이 추가될 때 게시글 작성자에게 알림
* 기능 2. 게시글/답글에 좋아요가 추가될 때 게시글 작성자에게 알림
* 기능 3. 답변이 채택될 때, 채택받은 사람에게 알림
* 기능 4. 누군가 후원할 때, 후원 받은 사람에게 알림.
* 기능 5. 질문에 답변이 추가될 때, 질문 작성자에게 알림
*
* 메세지1 : 회원님께서 작성하신 "제가 한 쩌는 코딩 봐주실 분?"에 OOO님께서 답변해주셨어요.
* 메세지2 : 회원님께서 작성하신 "누가 이거 좀 알려주세요"의 답변에 OOO님께서 댓글을 남기셨어요.
* 메세지3 : 회원님께서 작성하신 "솔직히 개발자 거품인듯"의 좋아요가 10개를 돌파했어요. -> "클릭 시 해당 글로 이동"
* 메세지4 : OOO님께서 OOO님께 포인트와 리뷰를 남기셨어요. -> 리뷰 페이지(대시보드?)로 이동
*
* /

/
* TODO : Notification이 생성되는 로직
* TODO : 매번 CRUD에 이걸 적용해야하는가? AOP를 만들면 편할 듯.
*
* 로직 -> 게시글에 댓글이 작성된 상황
* 1. 댓글 작성 서비스 계층에서 (article)의 작성자를 조회.
* 2. article의 제목과 comment 작성자의 아이디를 이용해 동적으로 메세지 생성
* 3. Notification은 article의 작성자인 user와 양방향 연관관계를 가진 상태로 저장된다.
*
* 로직 -> 좋아요 누적
* 1. 좋아요를 누를 때마다 좋아요하고 있는 article or answer의 총 좋아요 수를 카운트한다.
* 2. 좋아요 10개 단위마다 좋아요 개수를 이용해 동적으로 메세지 생성
* 3. 위와 같다.
*
* 로직 -> 답글에 댓글 작성
* 1. 답글 작성 서비스 계정에서 (answer)의 작성자를 조회.
* 2. answer -> article의 제목과 comment 작성자의 아이디를 이용해 동적으로 메세지 생성
* 3. 위와 같다.
*
* 로직 -> 후원 받음
* 1. 후원 로직이 정해지지 않음.
*
* */

/*
* TODO : Notification 조회 로직
*
* 1. GET /notification
* 2. AccessToken에서 id를 추출하여 해당 아이디에 연결된 notification을 모두 조회함 (최대 N개)
* 3. {
* notificationId : id
* message : message
* isChecked : boolean
* } 형태로 반환
* */

/*
* TODO : 특정 알림을 클릭한 경우
*
* 1. GET /notification/{id}
* 2. 해당 id를 가진 notification을 조회
* 3. notification에 저장된 uri를 반환
* 4. 컨트롤러에서는 해당 uri로 리다이렉트
* */

/*
* TODO : 특정 알림을 삭제하려는 경우
*
* 1. DELETE /notification/{id}
* 2. 해당 id를 가진 notification을 삭제한다.
* */

import com.morakmorak.morak_back_end.entity.*;
import org.springframework.stereotype.Component;

@Component
public class NotificationGenerator {
    private final StringBuilder stringBuilder = new StringBuilder();

    public String generateAnswerNotification(User sender, Article article) {
        String message = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(article.getTitle())
                .append("\"")
                .append("에 ")
                .append(sender.getNickname())
                .append("님께서 답변해주셨어요.")
                .toString();

        cleanBuilder();
        return message;
    }

    public String generateCommentNotification(User sender, Article article) {
        String message = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(article.getTitle())
                .append("\"")
                .append("에 ")
                .append(sender.getNickname())
                .append("님께서 댓글을 남기셨어요.")
                .toString();

        cleanBuilder();
        return message;
    }

    public String generateCommentNotification(User sender, Answer answer) {
        String message = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(answer.getArticle().getTitle())
                .append("\"")
                .append("의 답변에 ")
                .append(sender.getNickname())
                .append("님께서 댓글을 남기셨어요.")
                .toString();

        cleanBuilder();
        return message;
    }

    public String generateLikeNotification(ArticleLike articleLike, int count) {
        verifyCount(count);

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(articleLike.getArticle().getTitle())
                .append("\"")
                .append("의 좋아요가 ")
                .append(count)
                .append("개를 돌파했어요.")
                .toString();

        cleanBuilder();
        return result;
    }

    public String generateLikeNotification(AnswerLike answerLike, int count) {
        verifyCount(count);

        String result = stringBuilder.append("회원님께서 작성하신 ")
                .append("\"")
                .append(answerLike.getAnswer().getArticle().getTitle())
                .append("\"")
                .append("의 답변에 대한 좋아요가 ")
                .append(count)
                .append("개를 돌파했어요.")
                .toString();

        cleanBuilder();
        return result;
    }

    private void cleanBuilder() {
        stringBuilder.delete(0, stringBuilder.length());
    }

    private void verifyCount(int count) {
        if (count % 10 != 0) throw new IllegalArgumentException("유효하지 않은 카운트");
    }
}
