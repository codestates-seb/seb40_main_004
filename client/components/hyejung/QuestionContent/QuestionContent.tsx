/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 * 개요:
   - 제목/작성자/작성시간/좋아요/북마크 컴포넌트
   - 질문 본문 && 태그 컴포넌트
   - 코멘트 컴포넌트
 */
import { QuestionTitle } from './QuestionTitle';
import { QuestionerInfo } from './QuestionerInfo';
import { BtnLike } from './BtnLike';
import { BtnBookmark } from './BtnBookmark';
import { QuestionMainText } from './QuestionMainText';
import { CommentList } from '../CommentList';
import { TagList } from './TagList';
import { QuestionWriterBtns } from './QuestionWriterBtns';

export const QuestionContent = () => {
  return (
    <main className="flex flex-col w-full p-[60px]">
      <section className="flex flex-col space-y-4 border-b pb-3">
        <QuestionTitle />
        <div className="flex justify-between items-center">
          <QuestionerInfo />
          <div className="flex space-x-1">
            <BtnLike />
            <span className="text-xl pr-3">14</span>
            <BtnBookmark />
          </div>
        </div>
      </section>

      <section className="p-3">
        <QuestionMainText />
        <div className="flex justify-between items-end py-4">
          <TagList />
          <QuestionWriterBtns />
        </div>
      </section>

      <section className="space-y-3 border-l pl-3">
        <h3 className="text-xl font-bold">3 코멘트</h3>
        <CommentList />
      </section>
    </main>
  );
};
