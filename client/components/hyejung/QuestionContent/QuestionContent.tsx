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
import { QuestionMainText } from './QuestionMainText';
import { CommentList } from '../Comment/CommentList';
import { TagList } from './TagList';
import { LikeBookmarkBtns } from '../LikeBookmarkBtns';

export const QuestionContent = () => {
  return (
    <main className="flex flex-col w-full pb-6 mb-16 border-b">
      <section className="flex flex-col space-y-4 border-b pb-3">
        <QuestionTitle />
        <div className="flex justify-between items-center">
          <QuestionerInfo />
          <LikeBookmarkBtns />
        </div>
      </section>

      <section className="p-6">
        <QuestionMainText />
        <div className="flex justify-between items-end space-y-3 sm:space-y-0 py-4 flex-col sm:flex-row">
          <TagList />
          <article className="space-x-2 text-sm w-[80px] flex justify-end">
            <button>수정</button>
            <button>삭제</button>
          </article>
        </div>
      </section>

      <section className="space-y-3 border-l pl-3">
        <h3 className="text-xl font-bold">3 코멘트</h3>
        <CommentList />
      </section>
    </main>
  );
};
