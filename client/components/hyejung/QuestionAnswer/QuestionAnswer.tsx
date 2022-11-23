/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 * 개요:
   - 제목/작성자/작성시간/좋아요/북마크 컴포넌트
   - 질문 본문 && 태그 컴포넌트
   - 코멘트 컴포넌트
 */
import { CommentList } from '../Comment/CommentList';
import { ProfileImage } from '../ProfileImage';
import { AnswerMainText } from './AnswerMainText';
import { BtnLike } from '../BtnLike';
import { BtnBookmark } from '../BtnBookmark';
import { useRouter } from 'next/router';
import useSWR from 'swr';

import { Answer } from '../../../libs/interfaces';

export const QusetionAnswer = ({ answer }: Answer) => {
  return (
    <main className="flex flex-col w-full mt-6 bg-[#FCFCFC] border rounded-[20px]">
      <section className="flex pb-3 items-center justify-between bg-main-gray p-4 rounded-t-[20px] border-b">
        <div className="flex items-center space-x-2 text-white">
          <ProfileImage src="https://images.unsplash.com/photo-1668613965090-167f9263906f?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=869&q=80" />
          <span className="text-xl font-bold">🌻박해커해커</span>
          <time className="hidden sm:text-sm">
            2022년 11월 10일 오전 11시 22분
          </time>
        </div>
        <button className="text-white font-bold text-xs sm:text-base">
          답변 채택하기
        </button>
      </section>

      <section>
        <section className="p-6 flex flex-col space-y-2">
          <AnswerMainText />
          <article className="space-x-2 text-sm ml-auto">
            <button>수정</button>
            <button>삭제</button>
          </article>
        </section>

        <section className="space-y-3 pt-3 p-6">
          <div className="flex justify-between items-center border-b pb-2">
            <h3 className="text-xl font-bold">3 코멘트</h3>
            <div className="flex space-x-1">
              <BtnLike />
              <span className="text-xl pr-3">14</span>
              <BtnBookmark />
            </div>
          </div>
          <CommentList />
        </section>
      </section>
    </main>
  );
};

export const QuestionAnswerList = () => {
  const router = useRouter();
  const { articleId } = router.query;

  const { data: answers } = useSWR(
    `/articles/${articleId}/answers?page={1}&size={5}`,
  );
  const answersData = answers;

  return answersData.map((answer) => (
    <QusetionAnswer key={answer.answerId} answer={answer} />
  ));
};
