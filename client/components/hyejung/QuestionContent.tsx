/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-29
 */

import { QuestionTitle } from './QuestionTitle';
import { CreatedDate } from './CreatedDate';
import { CommentContainer } from './CommentContainer';
import { TagList } from './TagList';
import { BtnBookmark } from './BtnBookmark';
import { UserNickname } from './UserNickname';
import useSWR, { mutate } from 'swr';
import { useRouter } from 'next/router';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../atomsYW';
import { BtnLike } from './BtnLike';
import { QuestionMainText } from './QuestionMainText';
import { client } from '../../libs/client';

export const QuestionContent = () => {
  const router = useRouter();
  const { articleId } = router.query;

  const isLogin = useRecoilValue(isLoginAtom);

  const { data } = useSWR(`/articles/${articleId}`);
  const article = data.article;

  let currUserId: any = '';
  if (typeof window !== 'undefined') {
    currUserId = localStorage.getItem('userId');
  }
  const authorId = article.userInfo.userId;
  const isClosed = article.isClosed;

  const onDelete = () => {
    if (confirm('게시글을 삭제하시겠습니까...?')) {
      client
        .delete(`/api/articles/${articleId}`)
        .then((res) => {
          console.log(res);
          alert('게시글이 삭제되었습니다. 게시글 목록으로 돌아갑니다.');
          router.replace(`/questions`);
          mutate(`/articles/${articleId}`);
        })
        .catch((err) => {
          console.log(err);
          alert('답변 삭제에 실패했습니다.');
        });
    }
  };

  const onEdit = () => {
    router.push(`/ask`);
  };

  return (
    <main className="flex flex-col w-full pb-6 border-b">
      {data ? (
        <>
          <section className="flex flex-col space-y-4 border-b pb-3">
            <QuestionTitle title={article.title} />
            <div className="flex justify-between items-center">
              <section className="flex w-full justify-between">
                <article className="space-x-3 flex items-end">
                  <UserNickname
                    nickname={article.userInfo.nickname}
                    userId={article.userInfo.userId}
                    grade={article.userInfo.grade}
                  />
                  <CreatedDate createdAt={article.createdAt} />
                </article>
              </section>
              <div className="flex space-x-1">
                <BtnLike isLiked={article.isLiked} likes={article.likes} />
                <BtnBookmark isBookmarked={article.isBookMarked} />
              </div>
            </div>
          </section>

          <section className="p-6">
            <QuestionMainText>{article.content}</QuestionMainText>
            <div className="flex justify-between items-end space-y-3 sm:space-y-0 py-4 flex-col sm:flex-row">
              <TagList tags={article.tags} />

              {isLogin && !isClosed && authorId.toString() === currUserId ? (
                <article className="space-x-2 text-sm w-[80px] flex justify-end">
                  <button onClick={onEdit}>수정</button>
                  <button onClick={onDelete}>삭제</button>
                </article>
              ) : null}
            </div>
          </section>

          <section className="space-y-3 border-l pl-4">
            <CommentContainer />
          </section>
        </>
      ) : (
        <div>Loading...</div>
      )}
    </main>
  );
};
