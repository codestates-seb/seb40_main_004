/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-21
 */

import { QuestionTitle } from './QuestionTitle';
import { CreatedDate } from './CreatedDate';
import { CommentList } from './Comment/CommentList';
import { TagList } from './TagList';
import { BtnLike } from './BtnLike';
import { BtnBookmark } from './BtnBookmark';
import { UserNickname } from './UserNickname';
import useSWR from 'swr';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

type MainTextProps = {
  content: string;
};

// 본문 텍스트
const QuestionMainText = ({ content }: MainTextProps) => {
  return (
    <main>
      <p>{content}</p>
    </main>
  );
};

export const QuestionContent = () => {
  const router = useRouter();
  const { articleId } = router.query;

  // api 연결 이후 /articles/{id} 로 요청을 보낼 수 있습니다.
  const { data } = useSWR('/articles');
  const article = data.article;

  return (
    <main className="flex flex-col w-full pb-6 mb-16 border-b">
      {article ? (
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
                <BtnLike isLiked={article.isLiked} />
                <span className="text-xl pr-3">14</span>
                <BtnBookmark isBookmarked={article.isBookMarked} />
              </div>
            </div>
          </section>

          <section className="p-6">
            <QuestionMainText content={article.content} />
            <div className="flex justify-between items-end space-y-3 sm:space-y-0 py-4 flex-col sm:flex-row">
              <TagList tags={article.tags} />

              {/* 로그인 후 user state를 확인하여 보임/숨김 처리 필요! */}
              <article className="space-x-2 text-sm w-[80px] flex justify-end">
                <button>수정</button>
                <button>삭제</button>
              </article>
            </div>
          </section>

          <section className="space-y-3 border-l pl-3">
            <h3 className="text-xl font-bold">
              {article.comments.length} 코멘트
            </h3>
            <CommentList />
          </section>
        </>
      ) : (
        <div>Loading...</div>
      )}
    </main>
  );
};
