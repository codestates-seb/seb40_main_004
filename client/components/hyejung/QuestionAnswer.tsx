/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 * 개요:
   - 제목/작성자/작성시간/좋아요/북마크 컴포넌트
   - 질문 본문 && 태그 컴포넌트
   - 코멘트 컴포넌트
 */
import { LikeBookmarkBtns } from './LikeBookmarkBtns';
import { QuestionMainText } from './QuestionContent/QuestionMainText';
import { CommentList } from './CommentList';
import { ProfileImage } from './ProfileImage';

export const QusetionAnswer = () => {
  return (
    <main className="flex flex-col w-full mt-6 bg-[#FCFCFC] border rounded-[20px]">
      <section className="flex pb-3 items-center justify-between bg-main-gray p-4 rounded-t-[20px] border-b">
        <div className="flex items-center space-x-2 text-white">
          <ProfileImage />
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
          <QuestionMainText />
          <article className="space-x-2 text-sm ml-auto">
            <button>수정</button>
            <button>삭제</button>
          </article>
        </section>

        <section className="space-y-3 pt-3 p-6">
          <div className="flex justify-between items-center border-b pb-2">
            <h3 className="text-xl font-bold">3 코멘트</h3>
            <LikeBookmarkBtns />
          </div>
          <CommentList />
        </section>
      </section>
    </main>
  );
};
