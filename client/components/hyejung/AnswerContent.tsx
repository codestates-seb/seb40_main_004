/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-24
 */
import { ProfileImage } from './ProfileImage';
import { AnswerMainText } from './AnswerMainText';
import { BtnLike } from './BtnLike';

import { Answer } from '../../libs/interfaces';
import { elapsedTime } from '../../libs/elapsedTime';

// 기본 이미지 생성 전 임시
const tempSrc =
  'https://images.unsplash.com/photo-1669177073562-f15947acac9c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80';

export type AnswerProps = {
  answer: Answer;
  userId: number;
  isClosed: boolean;
};

// 답변 컴포넌트
export const AnswerContent = ({ answer, userId, isClosed }: AnswerProps) => {
  return (
    <main className="flex flex-col w-full my-12 bg-[#FCFCFC] border rounded-[20px]">
      <section className="flex pb-3 items-center justify-between bg-main-gray p-4 rounded-t-[20px] border-b">
        <div className="flex items-center space-x-2 text-white">
          <ProfileImage src={answer.avatar.remotePath || tempSrc} />
          <span className="text-xl font-bold">{answer.userInfo.nickname}</span>
          <time className="text-white text-xs sm:text-sm">
            {elapsedTime(answer.createdAt)}
          </time>
        </div>
        {/* 답변 채택 여부 && 게시글이 유저 본인꺼인지 확인 후 표시 */}
        {!isClosed && userId ? (
          <button className="text-white font-bold text-xs sm:text-base">
            답변 채택하기
          </button>
        ) : null}
      </section>

      <section>
        <section className="p-6 flex flex-col space-y-2">
          <AnswerMainText>{answer.content}</AnswerMainText>
          {/* 현재 유저 정보와 answer 의 userId 를 확인해서 조건부 렌더링 필요! */}
          <article className="space-x-2 text-sm ml-auto">
            <button>수정</button>
            <button>삭제</button>
          </article>
        </section>

        <section className="space-y-3 pt-3 p-6">
          <div className="flex justify-between items-center border-b pb-2">
            <h3 className="text-xl font-bold">{answer.commentCount} 코멘트</h3>
            <div className="flex space-x-1">
              {/* 답변글에서 유저의 좋아요 여부를 확인할 수 있는 api가 아직 수정중. */}
              <BtnLike isLiked={false} answerId={answer.answerId} />
              <span className="text-xl pr-3">{answer.answerLikeCount}</span>
            </div>
          </div>
          {/* 코멘트 api 가 아직 확정되지 않은 관계로 추후 작업 예정 */}
          {/* <CommentContainer answerId={answer.answerId} /> */}
        </section>
      </section>
    </main>
  );
};
