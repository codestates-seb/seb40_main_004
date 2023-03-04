import { useRecoilValue } from 'recoil';

import { BtnBookmark } from './BtnBookmark';
import { QuestionMainText } from './QuestionMainText';
import { CommentContainer } from '../Comment/index';

import { isLoginAtom } from '@atoms/loginAtom';

import { UserNickname } from '@components/common/UserNickname';
import { CreatedDate } from '@components/common/CreatedDate';
import { BtnLike } from '@components/common/BtnLike';
import { TagList } from '@components/common/TagList';

import { useFetch } from '@libs/useFetchSWR';
import { useQuestionContent } from './useQuestionContent';
import { useState } from 'react';
import { ReportModal } from './ReportModal';

type QuestionContentProps = {
  articleId: string;
};

export const QuestionContent = ({ articleId }: QuestionContentProps) => {
  const isLogin = useRecoilValue(isLoginAtom);
  const { handleDelete, handleToEdit, handleReport } = useQuestionContent(
    articleId as string,
  );

  // 게시글 데이터
  const { data: article, isLoading } = useFetch(`/api/articles/${articleId}`);

  let currUserId: string | null = '';

  if (typeof window !== 'undefined') {
    currUserId = localStorage.getItem('userId');
  }

  const authorId = isLoading || article.userInfo.userId;
  const isClosed = isLoading || article.isClosed;

  const [isModalOpen, setIsModalOpen] = useState(false);

  return (
    <main className="flex flex-col w-full pb-6 border-b relative">
      {!isLoading && (
        <>
          <section className="flex flex-col space-y-4 border-b pb-3">
            <section className="flex w-full text-2xl sm:text-3xl space-x-2">
              {article.category === 'QNA' && (
                <h1 className="text-main-yellow font-semibold">Q.</h1>
              )}
              <h1>{article.title}</h1>
            </section>
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
              <div className="flex space-x-1 items-end">
                {article && (
                  <>
                    <BtnLike />
                    <BtnBookmark />
                  </>
                )}
              </div>
            </div>
          </section>

          <section className="p-6">
            <QuestionMainText>{article?.content}</QuestionMainText>
            <div className="flex justify-between items-end space-y-3 sm:space-y-0 py-4 flex-col sm:flex-row">
              <TagList tags={article?.tags} />

              {isLogin && !isClosed && authorId.toString() === currUserId && (
                <article className="space-x-2 text-sm flex justify-end flex-wrap">
                  <button onClick={handleToEdit}>수정</button>
                  <button onClick={handleDelete}>삭제</button>
                </article>
              )}
              {isLogin && authorId.toString() !== currUserId && (
                <button
                  className="text-red-500 text-xs whitespace-nowrap"
                  onClick={() => setIsModalOpen(true)}
                >
                  신고
                </button>
              )}
            </div>
          </section>

          <section className="space-y-3 border-l pl-4">
            <CommentContainer />
          </section>
        </>
      )}
      {isModalOpen && (
        <ReportModal
          handleReport={handleReport}
          setIsModalOpen={setIsModalOpen}
        />
      )}
    </main>
  );
};
