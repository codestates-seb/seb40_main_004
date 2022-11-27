/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-24
 */
import { ProfileImage } from './ProfileImage';
import { AnswerMainText } from './AnswerMainText';
import { BtnLike } from './BtnLike';
import { CommentContainer } from './CommentList';
import { Answer } from '../../libs/interfaces';
import { elapsedTime } from '../../libs/elapsedTime';
import { useEffect, useRef, useState } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { articleAuthorIdAtom, isAnswerEditAtom } from '../../atomsHJ';
import { client } from '../../libs/client';
import { useRouter } from 'next/router';
import { mutate } from 'swr';

// 기본 이미지 생성 전 임시
const tempSrc =
  'https://images.unsplash.com/photo-1669177073562-f15947acac9c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80';

export type AnswerProps = {
  answer: Answer;
  userId: number;
  isClosed: boolean;
  pageInfo: number;
};

// 답변 컴포넌트
export const AnswerContent = ({
  answer,
  userId,
  isClosed,
  pageInfo,
}: AnswerProps) => {
  const router = useRouter();
  const { articleId } = router.query;

  const answerElement = useRef<null | HTMLDivElement>(null);
  const isAnswerEdit = useRecoilValue(isAnswerEditAtom);

  const currUserId = localStorage.getItem('userId');
  const articleAuthorId = useRecoilValue(articleAuthorIdAtom);
  useEffect(() => {
    if (isAnswerEdit.answerId === answer.answerId && answerElement.current)
      answerElement.current.scrollIntoView({ behavior: 'smooth' });
  }, [isAnswerEdit]);

  const setIsAnswerEdit = useSetRecoilState(isAnswerEditAtom);

  const onDelete = () => {
    if (confirm('정말 답변을 삭제하시겠습니까..?')) {
      client
        .delete(`/api/articles/${articleId}/answers/${answer.answerId}`)
        .then(() => {
          alert('삭제가 완료되었습니다!');
          mutate(`/api/articles/${articleId}/answers?page=1&size=5`);
        })
        .catch((err) => {
          console.error(err);
          alert('답변 삭제에 실패했습니다.');
        });
    }
  };

  const onEdit = () => {
    setIsAnswerEdit({
      isEdit: true,
      answerId: answer.answerId,
      answerPage: pageInfo,
      payload: answer.content,
    });
  };

  return (
    <main
      className="flex flex-col w-full mb-12 bg-[#FCFCFC] border rounded-[20px]"
      ref={answerElement}
    >
      <section className="flex items-center justify-between bg-main-gray px-4 pt-1.5 sm:px-4 sm:pb-2 sm:pt-3 rounded-t-[20px] border-b">
        <div className="flex items-center space-x-2 text-white">
          <ProfileImage src={answer.avatar.remotePath || tempSrc} />
          <span className="text-sm sm:text-xl font-bold">
            {answer.userInfo.nickname}
          </span>
          <time className="text-gray-200 text-xs sm:text-sm">
            {elapsedTime(answer.createdAt)}
          </time>
        </div>
        {!isClosed && articleAuthorId === currUserId ? (
          <button className="text-white font-bold text-xs sm:text-base">
            답변 채택하기
          </button>
        ) : null}
      </section>

      <section>
        <section className="p-6 flex flex-col space-y-2">
          <AnswerMainText>{answer.content}</AnswerMainText>
          {answer.userInfo.userId.toString() === currUserId ? (
            <article className="space-x-2 text-sm ml-auto">
              <button onClick={onEdit}>수정</button>
              <button onClick={onDelete}>삭제</button>
            </article>
          ) : null}
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
          <CommentContainer answerId={answer.answerId} />
        </section>
      </section>
    </main>
  );
};
