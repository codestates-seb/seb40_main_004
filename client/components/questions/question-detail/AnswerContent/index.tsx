import { ProfileImage } from './ProfileImage';
import { AnswerMainText } from './AnswerMainText';
import { CommentContainer } from '../Comment/index';
import { useEffect, useRef } from 'react';
import { useRecoilState, useRecoilValue, useSetRecoilState } from 'recoil';

import { useRouter } from 'next/router';
import { mutate } from 'swr';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleCheck as voidCheck } from '@fortawesome/free-regular-svg-icons';
import { faCircleCheck as solidCheck } from '@fortawesome/free-solid-svg-icons';
import Link from 'next/link';
import { Answer } from '@type/answer';

import { isAnswerEditAtom } from '@atoms/answerAtom';
import { articleAuthorIdAtom } from '@atoms/articleAtom';
import { reviewRequestAtom } from '@atoms/reviewAtom';

import { client } from '@libs/client';
import { changeGradeEmoji } from '@libs/changeGradeEmoji';
import { elapsedTime } from '@libs/elapsedTime';
import { BtnLike } from '@components/common/BtnLike';
import { toast } from 'react-toastify';
import { confirmAlert } from 'react-confirm-alert';
import { useGetArticleId } from 'hooks/useGetArticleId';

// 기본 이미지 생성 전 임시
const tempSrc =
  'https://images.unsplash.com/photo-1669177073562-f15947acac9c?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80';

export type AnswerProps = {
  answer: Answer;
  isClosed: boolean;
  pageInfo: number;
};

export const AnswerContent = ({ answer, isClosed, pageInfo }: AnswerProps) => {
  const { articleId } = useGetArticleId();
  const router = useRouter();

  // 채택된 경우 클래스
  const pickedAnswerClass =
    answer && answer.isPicked ? 'bg-main-orange' : 'bg-main-gray';

  const answerElement = useRef<null | HTMLDivElement>(null);
  const isAnswerEdit = useRecoilValue(isAnswerEditAtom);

  let currUserId: string | null = '';
  if (typeof window !== 'undefined') {
    currUserId = localStorage.getItem('userId');
  }

  const articleAuthorId = useRecoilValue(articleAuthorIdAtom);

  useEffect(() => {
    if (isAnswerEdit.answerId === answer.answerId && answerElement.current)
      answerElement.current.scrollIntoView({ behavior: 'smooth' });
  }, [isAnswerEdit]);

  const onDelete = () => {
    confirmAlert({
      message: '정말 답변을 삭제하시겠습니까?',
      buttons: [
        {
          label: 'YES',
          onClick: async () => {
            try {
              await client.delete(
                `/api/articles/${articleId}/answers/${answer.answerId}`,
              );
              toast.success('삭제가 완료되었습니다!');
              mutate(`/api/articles/${articleId}/answers?page=1&size=5`);
            } catch (error) {
              console.error(error);
              toast.error('답변 삭제에 실패했습니다.');
            }
          },
        },
        {
          label: 'NO',
        },
      ],
    });
  };

  const setIsAnswerEdit = useSetRecoilState(isAnswerEditAtom);
  const onEdit = () => {
    setIsAnswerEdit({
      isEdit: true,
      answerId: answer.answerId,
      answerPage: pageInfo,
      payload: answer.content,
    });
  };

  // 답변 채택 페이지로 넘어갈 때 실행
  const [reviewRequest, setReviewRequest] = useRecoilState(reviewRequestAtom);
  const moveToReview = () => {
    const newState = {
      targetId: answer.answerId,
      articleId: articleId as string,
      targetUserName: answer.userInfo.nickname,
    };
    setReviewRequest({
      ...reviewRequest,
      ...newState,
    });
    router.push('/review');
  };

  return (
    <main
      className="flex flex-col w-full mb-12 bg-[#FCFCFC] border rounded-[20px]"
      ref={answerElement}
    >
      <section
        className={`flex items-center justify-between  px-4 pt-1.5 sm:px-4 sm:pb-2 sm:pt-3 rounded-t-[20px] border-b ${pickedAnswerClass}`}
      >
        <div className="flex items-center space-x-2 text-white">
          <ProfileImage src={answer.avatar.remotePath || tempSrc} />
          <Link href={`/dashboard/${answer.userInfo.userId}`}>
            <span className="text-sm sm:text-xl font-bold cursor-pointer">
              {changeGradeEmoji(answer.userInfo.grade)}
              {answer.userInfo.nickname}
            </span>
          </Link>
          <time className="text-gray-200 text-xs sm:text-sm">
            {elapsedTime(answer.createdAt)}
          </time>
        </div>
        {!answer.isClosed &&
          articleAuthorId === currUserId &&
          answer.userInfo.userId.toString() !== currUserId && (
            <button
              className="text-white font-bold text-xs sm:text-base space-x-2"
              onClick={moveToReview}
            >
              <FontAwesomeIcon icon={voidCheck} className={'fa-lg'} />
              <span>답변 채택하기</span>
            </button>
          )}
        {answer.isPicked && (
          <div
            className="text-white font-bold text-xs sm:text-base space-x-2"
            onClick={moveToReview}
          >
            <FontAwesomeIcon icon={solidCheck} className={'fa-lg'} />
            <span>채택된 답변</span>
          </div>
        )}
      </section>

      <section>
        <section className="p-8 flex flex-col space-y-2">
          <AnswerMainText>{answer.content}</AnswerMainText>
          {answer.userInfo.userId.toString() === currUserId && (
            <article className="space-x-2 text-sm ml-auto">
              <button onClick={onEdit}>수정</button>
              <button onClick={onDelete}>삭제</button>
            </article>
          )}
        </section>

        <section className="space-y-3 pt-3 p-6">
          <div className="flex justify-between items-center border-b pb-2">
            <div className="ml-auto">
              <BtnLike
                answerId={answer.answerId}
                isLikedInit={answer.isLiked}
                likeCntInit={answer.answerLikeCount}
              />
            </div>
          </div>
          <CommentContainer answerId={answer.answerId} />
        </section>
      </section>
    </main>
  );
};
